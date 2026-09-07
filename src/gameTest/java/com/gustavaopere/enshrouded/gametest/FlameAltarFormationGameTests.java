package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarFormationState;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarMenu;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarOffering;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuntime;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarFormationGameTests {
    private static final String BATCH = "flameAltarFormation";
    private static final ResourceLocation RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_unformed_altar_gate");
    private static final ResourceLocation INTENT_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_unformed_altar_gate_intent");
    private static boolean registered;

    private FlameAltarFormationGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void unformedAltarCannotExecuteRitualFromServerMenu(GameTestHelper helper) {
        ensureRitualRegistered();
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = requireServerPlayer(helper);
        ProgressionOwner owner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());
        var before = FlameProgressionSavedData.get(level).progression(owner);

        BlockPos relative = new BlockPos(1, 1, 1);
        helper.setBlock(relative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity altar = requireAltar(helper, relative);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.DIRT));

        FlameAltarMenu menu = new FlameAltarMenu(1, player.getInventory(), altar);
        boolean handled = menu.clickMenuButton(player, FlameAltarMenu.ACTIVATE_BUTTON_ID);
        var after = FlameProgressionSavedData.get(level).progression(owner);

        helper.assertTrue(handled, "UNFORMED altar activation request should be handled server-side");
        helper.assertTrue(altar.inventory().getStackInSlot(0).is(Items.DIRT),
                "UNFORMED altar must not consume a valid synthetic ritual offering");
        helper.assertTrue(after.equals(before),
                "UNFORMED altar must not mutate authoritative Flame progression");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void persistedFormedIntentRequiresRecoveryBeforeGameplay(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        helper.setBlock(relative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity altar = requireAltar(helper, relative);

        CompoundTag persisted = altar.saveWithoutMetadata(level.registryAccess());
        CompoundTag formation = new CompoundTag();
        formation.putInt("SchemaVersion", FlameAltarFormationState.CURRENT_SCHEMA_VERSION);
        formation.putBoolean("Formed", true);
        persisted.put("Formation", formation);

        altar.loadWithComponents(persisted, level.registryAccess());
        helper.assertTrue(!altar.isFormed(),
                "Persisted FORMED intent must not bypass bounded world revalidation after reload");

        CompoundTag reserialized = altar.saveWithoutMetadata(level.registryAccess());
        helper.assertTrue(reserialized.contains("Formation"),
                "Formation recovery intent must survive BlockEntity persistence round-trip");
        CompoundTag savedFormation = reserialized.getCompound("Formation");
        helper.assertTrue(savedFormation.getInt("SchemaVersion") == FlameAltarFormationState.CURRENT_SCHEMA_VERSION,
                "Formation recovery persistence must retain the current schema version");
        helper.assertTrue(savedFormation.getBoolean("Formed"),
                "A valid persisted FORMED intent must remain pending until recovery validation can run");
        helper.succeed();
    }

    private static synchronized void ensureRitualRegistered() {
        if (registered) {
            return;
        }
        FlameAltarRuntime.registerRitual(new FlameRitual() {
            @Override
            public ResourceLocation id() {
                return RITUAL_ID;
            }

            @Override
            public ResourceLocation intentId() {
                return INTENT_ID;
            }

            @Override
            public boolean isEligible(Context context) {
                return context.progression().flameLevel() == 1 && context.progression().passageLevel() == 1;
            }

            @Override
            public OfferingContract offering() {
                return new OfferingContract() {
                    @Override
                    public boolean accepts(Context context, Offering offering) {
                        return offering instanceof FlameAltarOffering altarOffering
                                && altarOffering.stack().is(Items.DIRT);
                    }

                    @Override
                    public void consume(Context context, Offering offering) {
                        if (!(offering instanceof FlameAltarOffering altarOffering) || !altarOffering.consumeOne()) {
                            throw new IllegalStateException("Formation GameTest offering changed before consumption");
                        }
                    }
                };
            }

            @Override
            public RitualOutcome outcome(Context context) {
                return RitualOutcome.levelOneCheckpoint();
            }
        });
        registered = true;
    }

    @SuppressWarnings("removal")
    private static ServerPlayer requireServerPlayer(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        helper.assertTrue(player != null, "Formation GameTest requires a server-side mock player");
        return player;
    }

    private static FlameAltarBlockEntity requireAltar(GameTestHelper helper, BlockPos relative) {
        var blockEntity = GameTestBootstrap.requireServerLevel(helper).getBlockEntity(helper.absolutePos(relative));
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Placed enshrouded:flame_altar must create FlameAltarBlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
