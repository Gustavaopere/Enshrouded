package com.gustavaopere.enshrouded.integration.journeymap;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.state.ClientShroudDiscoveryState;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;

/**
 * Optional JourneyMap client plugin. NeoForge/JourneyMap discovers this class by annotation; the
 * common Enshrouded bootstrap deliberately never references it, preserving soft-dependency safety.
 */
@JourneyMapPlugin(apiVersion = "2.0.0")
public final class EnshroudedJourneyMapPlugin implements IClientPlugin {
    private JourneyMapAdapter adapter;

    public EnshroudedJourneyMapPlugin() {
    }

    @Override
    public String getModId() {
        return Enshrouded.MOD_ID;
    }

    @Override
    public void initialize(IClientAPI api) {
        adapter = new JourneyMapAdapter(api);
        ClientShroudDiscoveryState.INSTANCE.addListener(adapter::accept);
        ClientEventRegistry.MAPPING_EVENT.subscribe(Enshrouded.MOD_ID, this::onMappingEvent);
    }

    private void onMappingEvent(MappingEvent event) {
        if (adapter == null) {
            return;
        }
        if (event.getStage() == MappingEvent.Stage.MAPPING_STARTED) {
            adapter.mappingStarted();
        } else if (event.getStage() == MappingEvent.Stage.MAPPING_STOPPED) {
            adapter.mappingStopped();
        }
    }
}
