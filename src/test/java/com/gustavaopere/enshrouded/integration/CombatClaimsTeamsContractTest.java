package com.gustavaopere.enshrouded.integration;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.integration.ftbchunks.FtbChunksProtectionAdapter;
import com.gustavaopere.enshrouded.integration.ftbteams.FtbTeamsOwnerResolver;
import com.gustavaopere.enshrouded.integration.minecolonies.MineColoniesProtectionAdapter;
import com.gustavaopere.enshrouded.protection.CompositeProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombatClaimsTeamsContractTest {
    private static final BlockPos POS = new BlockPos(32, 70, -16);

    @Test
    void ftbTeamsResolverUsesStableTeamOwnerAndOnlyFutureResolutionsObserveMembershipChanges() {
        UUID playerId = UUID.fromString("2d64f1d9-86d9-4e99-9e1b-1db69b7e2b56");
        AtomicReference<Optional<String>> team = new AtomicReference<>(Optional.of("0b630207-f444-47ce-96cf-f23f24fe6341"));
        FtbTeamsOwnerResolver resolver = new FtbTeamsOwnerResolver(ignored -> team.get());

        ProgressionOwner inFlightOwner = resolver.resolve(playerId);
        assertEquals("team:0b630207-f444-47ce-96cf-f23f24fe6341", inFlightOwner.stableKey());

        team.set(Optional.empty());
        assertEquals(inFlightOwner, inFlightOwner, "an already captured transaction owner must remain immutable");
        assertEquals(ProgressionOwner.player(playerId), resolver.resolve(playerId),
                "membership changes affect only future owner resolutions");
    }

    @Test
    void ftbTeamsResolverFailsClosedWhenAnEnabledLookupCannotAnswer() {
        UUID playerId = UUID.fromString("352216f4-1b43-4f51-bf93-2a5d60f5385e");
        FtbTeamsOwnerResolver resolver = new FtbTeamsOwnerResolver(ignored -> {
            throw new IllegalStateException("provider unavailable");
        });

        assertThrows(IllegalStateException.class, () -> resolver.resolve(playerId));
    }

    @Test
    void claimAndColonyAdaptersPreserveTriStateAndTurnProviderFailureIntoIndeterminate() {
        FtbChunksProtectionAdapter claimed = new FtbChunksProtectionAdapter((level, pos) -> ProtectionDecision.PROTECTED);
        FtbChunksProtectionAdapter unclaimed = new FtbChunksProtectionAdapter((level, pos) -> ProtectionDecision.UNPROTECTED);
        MineColoniesProtectionAdapter colony = new MineColoniesProtectionAdapter((level, pos) -> ProtectionDecision.PROTECTED);
        MineColoniesProtectionAdapter broken = new MineColoniesProtectionAdapter((level, pos) -> {
            throw new IllegalStateException("API mismatch");
        });

        assertEquals(ProtectionDecision.PROTECTED, claimed.protectionAt(null, POS, MutationKind.CORRUPTION));
        assertEquals(ProtectionDecision.UNPROTECTED, unclaimed.protectionAt(null, POS, MutationKind.CORRUPTION));
        assertEquals(ProtectionDecision.PROTECTED, colony.protectionAt(null, POS, MutationKind.CORRUPTION));
        assertEquals(ProtectionDecision.INDETERMINATE, broken.protectionAt(null, POS, MutationKind.CORRUPTION));
    }

    @Test
    void compositeProtectionIsProtectedFirstThenFailClosedOnUncertainty() {
        CompositeProtectedAreaService protectedComposite = new CompositeProtectedAreaService(List.of(
                (level, pos, kind) -> ProtectionDecision.INDETERMINATE,
                (level, pos, kind) -> ProtectionDecision.PROTECTED
        ));
        CompositeProtectedAreaService uncertainComposite = new CompositeProtectedAreaService(List.of(
                (level, pos, kind) -> ProtectionDecision.UNPROTECTED,
                (level, pos, kind) -> ProtectionDecision.INDETERMINATE
        ));
        CompositeProtectedAreaService clearComposite = new CompositeProtectedAreaService(List.of(
                (level, pos, kind) -> ProtectionDecision.UNPROTECTED
        ));

        assertEquals(ProtectionDecision.PROTECTED, protectedComposite.protectionAt(null, POS, MutationKind.CORRUPTION));
        assertEquals(ProtectionDecision.INDETERMINATE, uncertainComposite.protectionAt(null, POS, MutationKind.CORRUPTION));
        assertEquals(ProtectionDecision.UNPROTECTED, clearComposite.protectionAt(null, POS, MutationKind.CORRUPTION));
    }
}
