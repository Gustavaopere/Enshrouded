package com.gustavaopere.enshrouded.integration.minecolonies;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/** Thin MineColonies boundary; provider failures become INDETERMINATE for Stage-02 fail-closed policy. */
public final class MineColoniesProtectionAdapter implements ProtectedAreaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MineColoniesProtectionAdapter.class);

    private final Query query;
    private final AtomicBoolean failureLogged = new AtomicBoolean();

    public MineColoniesProtectionAdapter(Query query) {
        this.query = Objects.requireNonNull(query, "query");
    }

    @Override
    public ProtectionDecision protectionAt(ServerLevel level, BlockPos pos, MutationKind kind) {
        try {
            ProtectionDecision decision = query.protectionAt(level, pos);
            if (decision != null) {
                return decision;
            }
            logFailureOnce("MineColonies protection query returned null; treating as INDETERMINATE.", null);
        } catch (RuntimeException failure) {
            logFailureOnce("MineColonies protection query failed; treating as INDETERMINATE.", failure);
        }
        return ProtectionDecision.INDETERMINATE;
    }

    private void logFailureOnce(String message, RuntimeException failure) {
        if (!failureLogged.compareAndSet(false, true)) {
            return;
        }
        if (failure == null) {
            LOGGER.warn(message);
        } else {
            LOGGER.warn(message, failure);
        }
    }

    @FunctionalInterface
    public interface Query {
        ProtectionDecision protectionAt(ServerLevel level, BlockPos pos);
    }
}
