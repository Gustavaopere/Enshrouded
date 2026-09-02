package com.gustavaopere.enshrouded.integration;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.integration.epicfight.EpicFightAdapter;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import com.gustavaopere.enshrouded.protection.ProtectionRuntimeBindings;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatClaimsTeamsBootstrapRedTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    @AfterEach
    void resetProtectionBinding() {
        ProtectionRuntimeBindings.reset();
    }

    @Test
    void runtimeProtectionBindingComposesInstalledProvidersAndResetsToStandalone() {
        ProtectedAreaService protectedProvider = (level, pos, kind) -> ProtectionDecision.PROTECTED;
        ProtectionRuntimeBindings.install(List.of(protectedProvider));
        assertEquals(
                ProtectionDecision.PROTECTED,
                ProtectionRuntimeBindings.protectedAreas().protectionAt(null, BlockPos.ZERO, MutationKind.CORRUPTION)
        );

        ProtectionRuntimeBindings.reset();
        assertEquals(
                ProtectionDecision.UNPROTECTED,
                ProtectionRuntimeBindings.protectedAreas().protectionAt(null, BlockPos.ZERO, MutationKind.CORRUPTION)
        );
    }

    @Test
    void epicFightAdapterIsCompatibilityOnlyAndNeverOwnsDamage() {
        EpicFightAdapter present = new EpicFightAdapter(true);
        EpicFightAdapter absent = new EpicFightAdapter(false);

        assertTrue(present.loaded());
        assertFalse(absent.loaded());
        assertFalse(present.ownsDamagePipeline());
        assertFalse(absent.ownsDamagePipeline());
    }

    @Test
    void productionBootstrapWiresOptionalIntegrationsThroughExistingAuthorities() throws IOException {
        String bootstrap = source("src/main/java/com/gustavaopere/enshrouded/Enshrouded.java");
        String progression = source("src/main/java/com/gustavaopere/enshrouded/flame/state/FlameProgressionRuntime.java");
        String mutationAuthority = source("src/main/java/com/gustavaopere/enshrouded/protection/DefaultMutationAuthority.java");
        String integrationRuntime = source("src/main/java/com/gustavaopere/enshrouded/integration/CombatClaimsTeamsIntegrationRuntime.java");

        assertTrue(bootstrap.contains("CombatClaimsTeamsIntegrationRuntime.register(modBus)"));
        assertTrue(progression.contains("CombatClaimsTeamsIntegrationRuntime.ownerResolver()"));
        assertTrue(mutationAuthority.contains("ProtectionRuntimeBindings.protectedAreas()"));
        assertFalse(integrationRuntime.contains("LivingDamageEvent"));
        assertFalse(integrationRuntime.contains("setNewDamage"));
    }

    private static String source(String relative) throws IOException {
        return Files.readString(ROOT.resolve(relative));
    }
}
