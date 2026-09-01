package com.gustavaopere.enshrouded.integration.ftbchunks;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/** Thin FTB Chunks boundary; provider failures become INDETERMINATE for Stage-02 fail-closed policy. */
public final class FtbChunksProtectionAdapter implements ProtectedAreaService {
    private final Query query;

    public FtbChunksProtectionAdapter(Query query) {
        this.query = Objects.requireNonNull(query, "query");
    }

    @Override
    public ProtectionDecision protectionAt(ServerLevel level, BlockPos pos, MutationKind kind) {
        try {
            ProtectionDecision decision = query.protectionAt(level, pos);
            return decision == null ? ProtectionDecision.INDETERMINATE : decision;
        } catch (RuntimeException failure) {
            return ProtectionDecision.INDETERMINATE;
        }
    }

    @FunctionalInterface
    public interface Query {
        ProtectionDecision protectionAt(ServerLevel level, BlockPos pos);
    }
}
