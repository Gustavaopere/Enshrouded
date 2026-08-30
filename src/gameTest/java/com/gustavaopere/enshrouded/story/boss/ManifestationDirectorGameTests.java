package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Optional;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ManifestationDirectorGameTests {
    private static final String BATCH = "manifestationDirector";

    private ManifestationDirectorGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void emptyHigherPriorityProviderFallsBackWithoutTouchingStoryState(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        EncounterContext context = context(helper, "60602001-0000-4000-8000-000000000001");
        LichStoryState storyBefore = StorySavedData.get(level).state();

        LichManifestationProvider fallback = spawningProvider("enshrouded:native-test", level, context);
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(fallback);
        registry.register(emptyProvider("optional:empty"), 100);

        ManifestationDirector.ActiveManifestation active = new ManifestationDirector(registry)
                .spawn(level, context)
                .orElseThrow(() -> new AssertionError("native fallback must recover Optional.empty() from a preferred provider"));

        helper.assertTrue(active.providerId().equals("enshrouded:native-test"),
                "director must continue to the deterministic fallback after Optional.empty()");
        helper.assertTrue(ManifestationDirector.encounterId(active.entity()).orElseThrow().equals(context.encounterId()),
                "director must attach the stable encounter UUID to the accepted physical actor");
        helper.assertTrue(StorySavedData.get(level).state().equals(storyBefore),
                "provider selection/spawn must not mutate StorySavedData; 06.03 owns encounter transitions");
        active.entity().discard();
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void removedHigherPriorityEntityIsRejectedAndFallbackRemainsRecoverable(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        EncounterContext context = context(helper, "60602001-0000-4000-8000-000000000002");

        LichManifestationProvider fallback = spawningProvider("enshrouded:native-test", level, context);
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(fallback);
        registry.register(removedProvider("optional:removed", level), 100);

        ManifestationDirector.ActiveManifestation active = new ManifestationDirector(registry)
                .spawn(level, context)
                .orElseThrow(() -> new AssertionError("unusable preferred actors must not block the native fallback"));

        helper.assertTrue(active.providerId().equals("enshrouded:native-test"),
                "dead/removed/unadded provider actors must be rejected before encounter acceptance");
        helper.assertTrue(active.entity().isAlive() && !active.entity().isRemoved(),
                "accepted manifestation actor must be a live usable LivingEntity");
        active.entity().discard();
        helper.succeed();
    }

    private static EncounterContext context(GameTestHelper helper, String encounterId) {
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        return new EncounterContext(UUID.fromString(encounterId), origin, 1, 60602001L);
    }

    private static LichManifestationProvider emptyProvider(String id) {
        return new TestProvider(id) {
            @Override
            public Optional<LivingEntity> spawn(ServerLevel level, EncounterContext context) {
                return Optional.empty();
            }
        };
    }

    private static LichManifestationProvider removedProvider(String id, ServerLevel level) {
        return new TestProvider(id) {
            @Override
            public Optional<LivingEntity> spawn(ServerLevel ignored, EncounterContext context) {
                Zombie zombie = EntityType.ZOMBIE.create(level);
                if (zombie == null) {
                    return Optional.empty();
                }
                zombie.discard();
                return Optional.of(zombie);
            }
        };
    }

    private static LichManifestationProvider spawningProvider(String id, ServerLevel level, EncounterContext expectedContext) {
        return new TestProvider(id) {
            @Override
            public Optional<LivingEntity> spawn(ServerLevel requestedLevel, EncounterContext context) {
                if (requestedLevel != level || !context.equals(expectedContext)) {
                    return Optional.empty();
                }
                Zombie zombie = EntityType.ZOMBIE.create(level);
                if (zombie == null) {
                    return Optional.empty();
                }
                zombie.moveTo(context.origin().getX() + 0.5D, context.origin().getY() + 1.0D, context.origin().getZ() + 0.5D, 0.0F, 0.0F);
                if (!level.addFreshEntity(zombie)) {
                    return Optional.empty();
                }
                return Optional.of(zombie);
            }
        };
    }

    private abstract static class TestProvider implements LichManifestationProvider {
        private final String id;

        private TestProvider(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean matches(Entity entity, UUID encounterId) {
            return ManifestationDirector.encounterId(entity).filter(encounterId::equals).isPresent();
        }
    }
}
