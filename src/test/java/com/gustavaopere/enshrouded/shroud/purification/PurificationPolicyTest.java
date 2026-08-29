package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PurificationPolicyTest {
    @Test
    void decayPreservesCellIdentityAndRetiresOnlyAtZero() {
        PurificationPolicy policy = new PurificationPolicy(0.25D);
        ShroudCellPos pos = new ShroudCellPos(2, 3, 4);
        ShroudCellState cell = new ShroudCellState(pos, 0.75D, ShroudSeverity.DEADLY);

        ShroudCellState decayed = policy.regress(cell).orElseThrow();
        assertEquals(pos, decayed.position());
        assertEquals(0.50D, decayed.intensity(), 1.0E-9);
        assertEquals(ShroudSeverity.DEADLY, decayed.severity());

        assertTrue(policy.regress(new ShroudCellState(pos, 0.25D, ShroudSeverity.SHROUD)).isEmpty());
    }

    @Test
    void invalidDecayFailsClosed() {
        assertThrows(IllegalArgumentException.class, () -> new PurificationPolicy(0.0D));
        assertThrows(IllegalArgumentException.class, () -> new PurificationPolicy(-0.1D));
        assertThrows(IllegalArgumentException.class, () -> new PurificationPolicy(1.1D));
        assertThrows(IllegalArgumentException.class, () -> new PurificationPolicy(Double.NaN));
    }
}
