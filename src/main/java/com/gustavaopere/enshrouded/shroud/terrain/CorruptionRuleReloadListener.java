package com.gustavaopere.enshrouded.shroud.terrain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Reloads immutable corruption rules from data/*/shroud_corruption/*.json. */
public final class CorruptionRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static volatile CorruptionRuleRegistry currentRegistry = new CorruptionRuleRegistry(List.of());

    public CorruptionRuleReloadListener() {
        super(GSON, "shroud_corruption");
    }

    public static CorruptionRuleRegistry currentRegistry() {
        return currentRegistry;
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> entries,
            ResourceManager resourceManager,
            ProfilerFiller profiler) {
        List<Map.Entry<ResourceLocation, JsonElement>> ordered = new ArrayList<>(entries.entrySet());
        ordered.sort(Comparator.comparing(entry -> entry.getKey().toString()));

        List<CorruptionRule> decoded = new ArrayList<>(ordered.size());
        for (Map.Entry<ResourceLocation, JsonElement> entry : ordered) {
            ResourceLocation resourceId = entry.getKey();
            CorruptionRule rule = CorruptionRule.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                    .getOrThrow(message -> new IllegalStateException(
                            "Invalid corruption rule " + resourceId + ": " + message
                    ));
            if (!resourceId.equals(rule.id())) {
                throw new IllegalStateException(
                        "Corruption rule id " + rule.id() + " must match datapack resource id " + resourceId
                );
            }
            decoded.add(rule);
        }

        currentRegistry = new CorruptionRuleRegistry(decoded);
    }
}
