package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.client.state.ClientShroudState;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Enshrouded network registration. Level 1 exposes only the authoritative clientbound local Shroud
 * presentation snapshot; there is no serverbound mutation/query command payload.
 */
public final class ModNetworking {
    public static final String PROTOCOL_VERSION = "1";

    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                ShroudSamplePayload.TYPE,
                ShroudSamplePayload.STREAM_CODEC,
                (payload, context) -> ClientShroudState.INSTANCE.accept(payload));
    }
}
