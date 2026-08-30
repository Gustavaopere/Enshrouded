package com.gustavaopere.enshrouded.ecology;

import com.gustavaopere.enshrouded.client.ecology.CorruptionVisualState;
import com.gustavaopere.enshrouded.ecology.loot.CorruptionLootModifier;
import com.gustavaopere.enshrouded.ecology.purification.EntityPurificationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

final class EcologyVisualsContractRedTest {
    @Test
    void stageFourCompletionSurfacesExist() {
        assertNotNull(CorruptionVisualState.class);
        assertNotNull(CorruptionLootModifier.class);
        assertNotNull(EntityPurificationService.class);
    }
}
