package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainSafetyBoundaryRedTest {
    @Test
    void terrainSafetyTypesExposeTheFailClosedBoundary() throws Exception {
        Class<?> decisionType = Class.forName("com.gustavaopere.enshrouded.protection.ProtectionDecision");
        Class<?> modeType = Class.forName("com.gustavaopere.enshrouded.protection.MutationSafetyMode");
        Class<?> protectedAreaType = Class.forName("com.gustavaopere.enshrouded.protection.ProtectedAreaService");
        Class<?> authorityType = Class.forName("com.gustavaopere.enshrouded.protection.DefaultMutationAuthority");

        assertTrue(decisionType.isEnum());
        assertEquals(Set.of("UNPROTECTED", "PROTECTED", "INDETERMINATE"), enumNames(decisionType));
        assertTrue(modeType.isEnum());
        assertEquals(Set.of("SAFE", "AGGRESSIVE"), enumNames(modeType));

        Method protectionAt = protectedAreaType.getMethod(
                "protectionAt",
                ServerLevel.class,
                BlockPos.class,
                MutationKind.class
        );
        assertEquals(decisionType, protectionAt.getReturnType());

        Method none = protectedAreaType.getMethod("none");
        assertEquals(protectedAreaType, none.getReturnType());

        assertTrue(MutationAuthority.class.isAssignableFrom(authorityType));
        Constructor<?> constructor = authorityType.getConstructor(
                modeType,
                FlameWardQuery.class,
                protectedAreaType,
                boolean.class,
                boolean.class
        );
        assertEquals(5, constructor.getParameterCount());
    }

    private static Set<String> enumNames(Class<?> type) {
        return Arrays.stream(type.getEnumConstants())
                .map(value -> ((Enum<?>) value).name())
                .collect(Collectors.toUnmodifiableSet());
    }
}
