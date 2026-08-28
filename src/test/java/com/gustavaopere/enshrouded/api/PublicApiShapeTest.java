package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PublicApiShapeTest {
    @Test
    void shroudQueryShapeIsStableAndReadOnlyByContractSurface() throws Exception {
        Method sample = ShroudQuery.class.getDeclaredMethod(
                "sample",
                ServerLevel.class,
                BlockPos.class,
                Entity.class);

        assertEquals(ShroudSample.class, sample.getReturnType());
        assertArrayEquals(
                new Class<?>[]{ServerLevel.class, BlockPos.class, Entity.class},
                sample.getParameterTypes());
        assertFunctionalInterfaceHasSingleAbstractMethod(ShroudQuery.class, "sample");
        assertEquals(0, ShroudQuery.class.getDeclaredFields().length,
                "ShroudQuery must remain a stateless read boundary");
    }

    @Test
    void mutationAuthorityShapeRemainsTheSingleTerrainGate() throws Exception {
        Method canMutate = MutationAuthority.class.getDeclaredMethod(
                "canMutate",
                ServerLevel.class,
                BlockPos.class,
                MutationKind.class);

        assertEquals(boolean.class, canMutate.getReturnType());
        assertFunctionalInterfaceHasSingleAbstractMethod(MutationAuthority.class, "canMutate");
        assertEquals(0, MutationAuthority.class.getDeclaredFields().length,
                "MutationAuthority must not own mutable global state");
    }

    @Test
    void flameWardQueryShapeStaysSpatialAndReadOnly() throws Exception {
        Method suppresses = FlameWardQuery.class.getDeclaredMethod("suppresses", ServerLevel.class, BlockPos.class);
        Method none = FlameWardQuery.class.getDeclaredMethod("none");

        assertEquals(boolean.class, suppresses.getReturnType());
        assertEquals(FlameWardQuery.class, none.getReturnType());
        assertTrue(Modifier.isStatic(none.getModifiers()));
        assertFunctionalInterfaceHasSingleAbstractMethod(FlameWardQuery.class, "suppresses");
        assertEquals(0, FlameWardQuery.class.getDeclaredFields().length,
                "FlameWardQuery must remain a stateless read boundary");
    }

    @Test
    void progressionOwnerResolverShapeStaysUuidBasedAndProviderNeutral() throws Exception {
        Method resolve = ProgressionOwnerResolver.class.getDeclaredMethod("resolve", UUID.class);
        Method standalone = ProgressionOwnerResolver.class.getDeclaredMethod("standalone");

        assertEquals(ProgressionOwner.class, resolve.getReturnType());
        assertEquals(ProgressionOwnerResolver.class, standalone.getReturnType());
        assertTrue(Modifier.isStatic(standalone.getModifiers()));
        assertFunctionalInterfaceHasSingleAbstractMethod(ProgressionOwnerResolver.class, "resolve");
        assertEquals(0, ProgressionOwnerResolver.class.getDeclaredFields().length,
                "ProgressionOwnerResolver must remain a stateless provider-neutral boundary");
    }

    @Test
    void flamePassageQueryShapeRemainsReadOnlyAndOwnerBased() throws Exception {
        Method passageLevel = FlamePassageQuery.class.getDeclaredMethod("passageLevel", ProgressionOwner.class);
        Method fallback = FlamePassageQuery.class.getDeclaredMethod("levelOneFallback");

        assertEquals(int.class, passageLevel.getReturnType());
        assertEquals(FlamePassageQuery.class, fallback.getReturnType());
        assertTrue(Modifier.isStatic(fallback.getModifiers()));
        assertFunctionalInterfaceHasSingleAbstractMethod(FlamePassageQuery.class, "passageLevel");
        assertEquals(0, FlamePassageQuery.class.getDeclaredFields().length,
                "FlamePassageQuery must remain a stateless read boundary");
    }

    @Test
    void magicClassifierShapeDoesNotExposeProviderSpecificTypes() throws Exception {
        Method classify = MagicDamageClassifier.class.getDeclaredMethod("classify", DamageSource.class);

        assertEquals(MagicDamageClassification.class, classify.getReturnType());
        assertFunctionalInterfaceHasSingleAbstractMethod(MagicDamageClassifier.class, "classify");
        assertEquals(0, MagicDamageClassifier.class.getDeclaredFields().length,
                "MagicDamageClassifier must remain a stateless classification boundary");
    }

    @Test
    void encounterContextShapeRequiresExplicitOrigin() throws Exception {
        var constructor = EncounterContext.class.getDeclaredConstructor(
                UUID.class,
                BlockPos.class,
                int.class,
                long.class);

        assertArrayEquals(
                new Class<?>[]{UUID.class, BlockPos.class, int.class, long.class},
                constructor.getParameterTypes());
        assertEquals(1, EncounterContext.class.getDeclaredConstructors().length,
                "EncounterContext must not add an origin-omitting convenience constructor");
    }

    @Test
    void lichProviderOwnsEntityProvisionButNotStoryRewardSurface() throws Exception {
        Method id = LichManifestationProvider.class.getDeclaredMethod("id");
        Method available = LichManifestationProvider.class.getDeclaredMethod("isAvailable");
        Method spawn = LichManifestationProvider.class.getDeclaredMethod(
                "spawn",
                ServerLevel.class,
                EncounterContext.class);
        Method matches = LichManifestationProvider.class.getDeclaredMethod(
                "matches",
                Entity.class,
                UUID.class);

        assertEquals(String.class, id.getReturnType());
        assertEquals(boolean.class, available.getReturnType());
        assertEquals(Optional.class, spawn.getReturnType());
        assertEquals(boolean.class, matches.getReturnType());
        assertEquals(0, LichManifestationProvider.class.getDeclaredFields().length,
                "Lich providers must not own global story state");

        String methodNames = Arrays.stream(LichManifestationProvider.class.getDeclaredMethods())
                .map(Method::getName)
                .sorted()
                .toList()
                .toString();
        assertEquals("[id, isAvailable, matches, spawn]", methodNames,
                "Reward/progression methods must not leak into the provider contract");

        assertTrue(spawn.getGenericReturnType().getTypeName().contains(LivingEntity.class.getName()),
                "spawn must remain Optional<LivingEntity>");
    }

    private static void assertFunctionalInterfaceHasSingleAbstractMethod(Class<?> type, String expectedName) {
        Method[] abstractMethods = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .toArray(Method[]::new);

        assertEquals(1, abstractMethods.length, type.getSimpleName() + " must remain functional");
        assertEquals(expectedName, abstractMethods[0].getName());
    }
}
