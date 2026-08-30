package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Enshrouded.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<FlameAltarMenu>> FLAME_ALTAR = MENUS.register(
            "flame_altar",
            () -> new MenuType<>(FlameAltarMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    private ModMenus() {
    }
}
