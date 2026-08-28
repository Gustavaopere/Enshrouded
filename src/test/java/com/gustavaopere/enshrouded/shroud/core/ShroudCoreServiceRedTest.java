package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class ShroudCoreServiceRedTest {
    @Test
    void serviceExposesPureDimensionLocalLifecycleMutations() throws Exception {
        Class<?> service = Class.forName("com.gustavaopere.enshrouded.shroud.core.ShroudCoreService");
        Class<?> result = Class.forName("com.gustavaopere.enshrouded.shroud.core.CoreMutationResult");

        Method register = service.getMethod(
                "registerDormant",
                ShroudWorldState.class,
                UUID.class,
                UUID.class,
                BlockPos.class,
                int.class,
                int.class,
                long.class
        );
        assertEquals(result, register.getReturnType());
        assertEquals(result, service.getMethod("activate", ShroudWorldState.class, UUID.class).getReturnType());
        assertEquals(result, service.getMethod("destroy", ShroudWorldState.class, UUID.class).getReturnType());
        assertEquals(result, service.getMethod("markPurified", ShroudWorldState.class, UUID.class).getReturnType());

        assertEquals(ShroudWorldState.class, result.getMethod("state").getReturnType());
        assertEquals(boolean.class, result.getMethod("changed").getReturnType());
    }

    @Test
    void resultTypesAreAbsentUntilProductionImplementation() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.gustavaopere.enshrouded.shroud.core.CoreMutationResult"));
    }
}
