package com.gustavaopere.enshrouded.integration;

import com.gustavaopere.enshrouded.integration.ftbchunks.FtbChunksCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.ftbchunks.FtbChunksProtectionAdapter;
import com.gustavaopere.enshrouded.integration.ftbteams.FtbTeamsCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.minecolonies.MineColoniesCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.minecolonies.MineColoniesProtectionAdapter;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatClaimsTeamsProviderRedTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final UUID PLAYER = UUID.fromString("58e0206f-46f6-4fd0-94be-96a0ee54484b");
    private static final UUID PARTY = UUID.fromString("0b630207-f444-47ce-96cf-f23f24fe6341");

    @BeforeEach
    void resetFixtures() {
        FakeTeamsManager.team = new FakeTeam(true, PLAYER);
        FakeClaimManager.claimed = false;
        FakeColonyManager.protectedPosition = false;
    }

    @Test
    void ftbTeamsProbeUsesCurrentApiAndDistinguishesPersonalTeamFromSharedTeam() {
        FtbTeamsCompatibilityProbe probe = FtbTeamsCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::teamsClass);
        assertEquals(FtbTeamsCompatibilityProbe.Status.AVAILABLE, probe.status());
        assertEquals(Optional.empty(), probe.teamId(PLAYER), "personal FTB team must remain player-owned");

        FakeTeamsManager.team = new FakeTeam(false, PARTY);
        assertEquals(Optional.of(PARTY.toString()), probe.teamId(PLAYER), "party ownership must use the stable FTB team UUID");
    }

    @Test
    void ftbTeamsAbsentIsStandaloneButPresentApiMismatchFailsClosed() {
        FtbTeamsCompatibilityProbe absent = FtbTeamsCompatibilityProbe.detect(false, ignored -> {
            throw new AssertionError("absent provider must not probe classes");
        });
        assertEquals(FtbTeamsCompatibilityProbe.Status.MOD_ABSENT, absent.status());
        assertEquals(Optional.empty(), absent.teamId(PLAYER));

        FtbTeamsCompatibilityProbe mismatch = FtbTeamsCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::missingClass);
        assertEquals(FtbTeamsCompatibilityProbe.Status.INCOMPATIBLE, mismatch.status());
        assertThrows(IllegalStateException.class, () -> mismatch.teamId(PLAYER));
    }

    @Test
    void ftbChunksProbeUsesIndexedGetChunkAndPreservesProtectedVsDefinitelyUnprotected() {
        FtbChunksCompatibilityProbe probe = FtbChunksCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::chunksClass);
        assertEquals(FtbChunksCompatibilityProbe.Status.AVAILABLE, probe.status());
        assertEquals(ProtectionDecision.UNPROTECTED, probe.protectionAt(null, BlockPos.ZERO));

        FakeClaimManager.claimed = true;
        assertEquals(ProtectionDecision.PROTECTED, probe.protectionAt(null, BlockPos.ZERO));

        FtbChunksCompatibilityProbe mismatch = FtbChunksCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::missingClass);
        assertEquals(
                ProtectionDecision.INDETERMINATE,
                new FtbChunksProtectionAdapter(mismatch::protectionAt).protectionAt(null, BlockPos.ZERO, null)
        );
    }

    @Test
    void mineColoniesProbeUsesPositionLookupAndPreservesProtectedVsDefinitelyUnprotected() {
        MineColoniesCompatibilityProbe probe = MineColoniesCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::mineColoniesClass);
        assertEquals(MineColoniesCompatibilityProbe.Status.AVAILABLE, probe.status());
        assertEquals(ProtectionDecision.UNPROTECTED, probe.protectionAt(null, BlockPos.ZERO));

        FakeColonyManager.protectedPosition = true;
        assertEquals(ProtectionDecision.PROTECTED, probe.protectionAt(null, BlockPos.ZERO));

        MineColoniesCompatibilityProbe mismatch = MineColoniesCompatibilityProbe.detect(true, CombatClaimsTeamsProviderRedTest::missingClass);
        assertEquals(
                ProtectionDecision.INDETERMINATE,
                new MineColoniesProtectionAdapter(mismatch::protectionAt).protectionAt(null, BlockPos.ZERO, null)
        );
    }

    @Test
    void sharedTeamOwnershipIsDeliberateAndProviderRuntimeContainsNoGlobalClaimOrColonyScan() throws IOException {
        String config = source("src/main/java/com/gustavaopere/enshrouded/config/EnshroudedConfig.java");
        String runtime = source("src/main/java/com/gustavaopere/enshrouded/integration/CombatClaimsTeamsIntegrationRuntime.java");

        assertTrue(config.contains("\"ftbTeamsSharedProgression\", false"),
                "shared ownership must default off so existing player saves are never silently migrated");
        assertTrue(runtime.contains("ModList.get().isLoaded(\"epicfight\")"));
        assertTrue(runtime.contains("ProtectionRuntimeBindings.install"));
        assertTrue(runtime.contains("ServerStartedEvent"));
        assertTrue(runtime.contains("ServerStoppedEvent"));
        assertTrue(!runtime.contains("getAllClaimedChunks"));
        assertTrue(!runtime.contains("getAllColonies"));
    }

    private static String source(String relative) throws IOException {
        return Files.readString(ROOT.resolve(relative));
    }

    private static Class<?> missingClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }

    private static Class<?> teamsClass(String name) throws ClassNotFoundException {
        return switch (name) {
            case "dev.ftb.mods.ftbteams.api.FTBTeamsAPI" -> FakeTeamsApiRoot.class;
            case "dev.ftb.mods.ftbteams.api.FTBTeamsAPI$API" -> FakeTeamsApi.class;
            case "dev.ftb.mods.ftbteams.api.TeamManager" -> FakeTeamsManager.class;
            case "dev.ftb.mods.ftbteams.api.Team" -> FakeTeam.class;
            default -> throw new ClassNotFoundException(name);
        };
    }

    private static Class<?> chunksClass(String name) throws ClassNotFoundException {
        return switch (name) {
            case "dev.ftb.mods.ftbchunks.api.FTBChunksAPI" -> FakeChunksApiRoot.class;
            case "dev.ftb.mods.ftbchunks.api.FTBChunksAPI$API" -> FakeChunksApi.class;
            case "dev.ftb.mods.ftbchunks.api.ClaimedChunkManager" -> FakeClaimManager.class;
            case "dev.ftb.mods.ftblibrary.math.ChunkDimPos" -> FakeChunkDimPos.class;
            default -> throw new ClassNotFoundException(name);
        };
    }

    private static Class<?> mineColoniesClass(String name) throws ClassNotFoundException {
        if (name.equals("com.minecolonies.api.colony.IColonyManager")) {
            return FakeColonyManager.class;
        }
        throw new ClassNotFoundException(name);
    }

    public static final class FakeTeamsApiRoot {
        public static FakeTeamsApi api() {
            return FakeTeamsApi.INSTANCE;
        }
    }

    public static final class FakeTeamsApi {
        private static final FakeTeamsApi INSTANCE = new FakeTeamsApi();

        public boolean isManagerLoaded() {
            return true;
        }

        public FakeTeamsManager getManager() {
            return FakeTeamsManager.INSTANCE;
        }
    }

    public static final class FakeTeamsManager {
        private static final FakeTeamsManager INSTANCE = new FakeTeamsManager();
        private static FakeTeam team;

        public Optional<FakeTeam> getTeamForPlayerID(UUID playerId) {
            return Optional.ofNullable(team);
        }
    }

    public record FakeTeam(boolean playerTeam, UUID id) {
        public boolean isPlayerTeam() {
            return playerTeam;
        }

        public UUID getId() {
            return id;
        }
    }

    public static final class FakeChunksApiRoot {
        public static FakeChunksApi api() {
            return FakeChunksApi.INSTANCE;
        }
    }

    public static final class FakeChunksApi {
        private static final FakeChunksApi INSTANCE = new FakeChunksApi();

        public boolean isManagerLoaded() {
            return true;
        }

        public FakeClaimManager getManager() {
            return FakeClaimManager.INSTANCE;
        }
    }

    public static final class FakeClaimManager {
        private static final FakeClaimManager INSTANCE = new FakeClaimManager();
        private static boolean claimed;

        public Object getChunk(FakeChunkDimPos pos) {
            return claimed ? new Object() : null;
        }
    }

    public static final class FakeChunkDimPos {
        public FakeChunkDimPos(Level level, BlockPos pos) {
        }
    }

    public static final class FakeColonyManager {
        private static final FakeColonyManager INSTANCE = new FakeColonyManager();
        private static boolean protectedPosition;

        public static FakeColonyManager getInstance() {
            return INSTANCE;
        }

        public Object getColonyByPosFromWorld(Level level, BlockPos pos) {
            return protectedPosition ? new Object() : null;
        }
    }
}
