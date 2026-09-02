package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuntimeProtectionCompositionRedTest {
    @AfterEach
    void resetRuntimeProtection() {
        ProtectionRuntimeBindings.reset();
    }

    @Test
    void explicitFactoryProtectionCannotBypassInstalledRuntimeProviders() {
        ProtectedAreaService runtimeProvider = (level, pos, kind) -> ProtectionDecision.PROTECTED;
        ProtectionRuntimeBindings.install(List.of(runtimeProvider));

        ProtectedAreaService effective = DefaultMutationAuthority.composeProtectedAreasForConfig(
                ProtectedAreaService.none()
        );

        assertEquals(
                ProtectionDecision.PROTECTED,
                effective.protectionAt(null, BlockPos.ZERO, MutationKind.PURIFICATION),
                "an explicitly supplied protection service must not bypass Stage-08 runtime claim/colony providers"
        );
    }
}
