package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

/** Canonical SoundEvent registry for original Enshrouded audio cues. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Enshrouded.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SHROUD_AMBIENT = register("shroud_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEADLY_SHROUD_AMBIENT = register("deadly_shroud_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MADNESS_WHISPER = register("madness_whisper");

    private ModSounds() {}

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(Objects.requireNonNull(modBus, "modBus"));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
