package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.Objects;

/** Aggregates optional protection providers with protected-first, fail-closed tri-state semantics. */
public final class CompositeProtectedAreaService implements ProtectedAreaService {
    private final List<ProtectedAreaService> delegates;

    public CompositeProtectedAreaService(List<ProtectedAreaService> delegates) {
        this.delegates = List.copyOf(Objects.requireNonNull(delegates, "delegates"));
    }

    @Override
    public ProtectionDecision protectionAt(ServerLevel level, BlockPos pos, MutationKind kind) {
        boolean indeterminate = false;
        for (ProtectedAreaService delegate : delegates) {
            ProtectionDecision decision;
            try {
                decision = Objects.requireNonNull(
                        delegate.protectionAt(level, pos, kind),
                        "protection provider returned null"
                );
            } catch (RuntimeException failure) {
                decision = ProtectionDecision.INDETERMINATE;
            }
            if (decision == ProtectionDecision.PROTECTED) {
                return ProtectionDecision.PROTECTED;
            }
            if (decision == ProtectionDecision.INDETERMINATE) {
                indeterminate = true;
            }
        }
        return indeterminate ? ProtectionDecision.INDETERMINATE : ProtectionDecision.UNPROTECTED;
    }
}
