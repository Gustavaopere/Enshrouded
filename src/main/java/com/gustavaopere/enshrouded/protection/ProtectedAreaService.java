package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Read-only aggregate protection query consumed by the mutation authority. */
@FunctionalInterface
public interface ProtectedAreaService {
    ProtectionDecision protectionAt(ServerLevel level, BlockPos pos, MutationKind kind);

    static ProtectedAreaService none() {
        return (level, pos, kind) -> ProtectionDecision.UNPROTECTED;
    }
}
