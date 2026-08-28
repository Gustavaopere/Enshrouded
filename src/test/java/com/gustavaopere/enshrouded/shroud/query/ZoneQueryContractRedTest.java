package com.gustavaopere.enshrouded.shroud.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ZoneQueryContractRedTest {
    @Test
    void plannedZoneQueryAndSyncTypesExist() {
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.query.ShroudSpatialIndex"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.network.ShroudSamplePayload"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.client.state.ClientShroudState"));
    }
}
