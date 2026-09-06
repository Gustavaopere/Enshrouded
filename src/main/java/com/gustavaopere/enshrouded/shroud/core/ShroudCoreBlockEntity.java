package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.UUID;

/**
 * Persistent physical Shroud Core identity plus a presentation-only GeckoLib view.
 * Lifecycle, registration and corruption remain owned by the canonical Stage 01 state/services.
 */
public final class ShroudCoreBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final String CORE_ID_TAG = "CoreId";
    private static final String REGION_ID_TAG = "RegionId";
    private static final String AUTO_ACTIVATE_TAG = "AutoActivate";
    private static final String PRESENTATION_PROFILE_TAG = "PresentationProfile";
    private static final int LEVEL_ONE_TIER = 1;
    private static final int PROFILE_REFRESH_INTERVAL_TICKS = 20;

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.shroud_core.idle");
    private static final RawAnimation THREAT_ANIMATION = RawAnimation.begin().thenLoop("animation.shroud_core.threat");
    private static final RawAnimation COLLAPSE_ANIMATION = RawAnimation.begin().thenPlay("animation.shroud_core.collapse");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private UUID coreId;
    private UUID regionId;
    private boolean autoActivate;
    private PresentationProfile presentationProfile = PresentationProfile.ORDINARY;

    public ShroudCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHROUD_CORE.get(), pos, state);
    }

    public PresentationProfile presentationProfile() {
        return presentationProfile;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ensureLocalIdentity();
        ShroudCoreRegistrationQueue.enqueue(
                serverLevel,
                worldPosition,
                coreId,
                regionId,
                LEVEL_ONE_TIER,
                EnshroudedConfig.coreMaxInfluenceRadius(),
                expansionSeed(coreId),
                autoActivate
        );
        refreshPresentationProfile(serverLevel);
    }

    /** Marks this physical core as an automatic seed; canonical activation still occurs server-side. */
    public void requestAutomaticActivation() {
        autoActivate = true;
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            enqueueRegistration(serverLevel, true);
        }
    }

    /** Immutable physical identity exposed only so operator recovery can compare, never rewrite, it. */
    public RecoveryIdentity recoveryIdentity() {
        ensureLocalIdentity();
        return new RecoveryIdentity(coreId, regionId, worldPosition.immutable(), autoActivate);
    }

    /** Replays the normal bounded registration handoff without mutating SavedData directly. */
    public void enqueueRecoveryRegistration(ServerLevel serverLevel) {
        Objects.requireNonNull(serverLevel, "serverLevel");
        if (level != serverLevel) {
            throw new IllegalArgumentException("recovery server level does not own this Shroud core");
        }
        enqueueRegistration(serverLevel, autoActivate);
    }

    boolean matchesIdentity(UUID expectedCoreId, UUID expectedRegionId) {
        return expectedCoreId.equals(coreId) && expectedRegionId.equals(regionId);
    }

    static void serverTick(Level level, BlockPos pos, BlockState state, ShroudCoreBlockEntity core) {
        if (!(level instanceof ServerLevel serverLevel) || core.isRemoved()) {
            return;
        }
        long phase = Math.floorMod(pos.asLong(), PROFILE_REFRESH_INTERVAL_TICKS);
        if (Math.floorMod(serverLevel.getGameTime(), PROFILE_REFRESH_INTERVAL_TICKS) == phase) {
            core.refreshPresentationProfile(serverLevel);
        }
    }

    private void refreshPresentationProfile(ServerLevel serverLevel) {
        ensureLocalIdentity();
        ShroudSample sample = PresentationQueryHolder.QUERY.sample(serverLevel, worldPosition, null);
        boolean belongsToThisCore = sample.sourceId().map(coreId::equals).orElse(false);
        PresentationProfile next = belongsToThisCore && sample.severity() == ShroudSeverity.DEADLY
                ? PresentationProfile.DEADLY
                : PresentationProfile.ORDINARY;
        if (next != presentationProfile) {
            presentationProfile = next;
            BlockState state = getBlockState();
            serverLevel.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "shroud_core", 6,
                state -> state.setAndContinue(
                        presentationProfile == PresentationProfile.DEADLY ? THREAT_ANIMATION : IDLE_ANIMATION))
                .triggerableAnim("collapse", COLLAPSE_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    /** Presentation-only collapse, downstream of canonical ACTIVE -> DESTROYED retirement. */
    void triggerAuthoritativeCollapsePresentation() {
        if (level instanceof ServerLevel) {
            triggerAnim("shroud_core", "collapse");
        }
    }

    CoreMutationResult retirePhysicalCore(ServerLevel serverLevel) {
        ShroudSavedData savedData = ShroudSavedData.get(serverLevel);
        if (coreId == null) {
            return CoreMutationResult.unchanged(savedData.state());
        }
        ShroudCoreState core = savedData.state().cores().get(coreId);
        if (core == null) {
            return CoreMutationResult.unchanged(savedData.state());
        }

        CoreMutationResult retirement = switch (core.lifecycleState()) {
            case DORMANT -> ShroudCoreService.discardDormant(savedData.state(), coreId);
            case ACTIVE -> ShroudCoreService.destroy(savedData.state(), coreId);
            case DESTROYED, PURIFIED -> CoreMutationResult.unchanged(savedData.state());
        };
        if (retirement.changed()) {
            savedData.replace(retirement.state());
            if (core.lifecycleState() == CoreLifecycleState.ACTIVE) {
                NeoForge.EVENT_BUS.post(new ShroudCoreDestroyedEvent(serverLevel, coreId));
                triggerAuthoritativeCollapsePresentation();
            }
        }
        return retirement;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString(PRESENTATION_PROFILE_TAG, presentationProfile.id());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        presentationProfile = PresentationProfile.fromId(tag.getString(PRESENTATION_PROFILE_TAG));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        boolean hasCoreId = tag.hasUUID(CORE_ID_TAG);
        boolean hasRegionId = tag.hasUUID(REGION_ID_TAG);
        if (hasCoreId != hasRegionId) {
            throw new IllegalStateException("Shroud core block entity has incomplete persistent identity");
        }
        coreId = hasCoreId ? tag.getUUID(CORE_ID_TAG) : null;
        regionId = hasRegionId ? tag.getUUID(REGION_ID_TAG) : null;
        autoActivate = tag.getBoolean(AUTO_ACTIVATE_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (coreId != null && regionId != null) {
            tag.putUUID(CORE_ID_TAG, coreId);
            tag.putUUID(REGION_ID_TAG, regionId);
        }
        if (autoActivate) {
            tag.putBoolean(AUTO_ACTIVATE_TAG, true);
        }
    }

    private void enqueueRegistration(ServerLevel serverLevel, boolean activateAfterRegistration) {
        ensureLocalIdentity();
        ShroudCoreRegistrationQueue.enqueue(
                serverLevel,
                worldPosition,
                coreId,
                regionId,
                LEVEL_ONE_TIER,
                EnshroudedConfig.coreMaxInfluenceRadius(),
                expansionSeed(coreId),
                activateAfterRegistration
        );
    }

    private void ensureLocalIdentity() {
        if (coreId == null && regionId == null) {
            coreId = UUID.randomUUID();
            regionId = UUID.randomUUID();
            setChanged();
            return;
        }
        if (coreId == null || regionId == null) {
            throw new IllegalStateException("Shroud core block entity has incomplete local identity");
        }
    }

    private static long expansionSeed(UUID coreId) {
        return coreId.getMostSignificantBits() ^ coreId.getLeastSignificantBits();
    }

    public enum PresentationProfile {
        ORDINARY("ordinary"), DEADLY("deadly");
        private final String id;
        PresentationProfile(String id) { this.id = id; }
        public String id() { return id; }
        static PresentationProfile fromId(String id) { return DEADLY.id.equals(id) ? DEADLY : ORDINARY; }
    }

    public record RecoveryIdentity(UUID coreId, UUID regionId, BlockPos position, boolean autoActivate) {
        public RecoveryIdentity {
            Objects.requireNonNull(coreId, "coreId");
            Objects.requireNonNull(regionId, "regionId");
            Objects.requireNonNull(position, "position");
            position = position.immutable();
        }
    }

    /** Lazy holder prevents runtime query binding during physical-client class initialization. */
    private static final class PresentationQueryHolder {
        private static final ShroudQuery QUERY = DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne());
    }
}
