package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Owns the immutable compiled materialization rule set used by loaded-world reconciliation. */
public final class CorruptionRuleRegistry {
    private final List<CorruptionRule> rules;
    private final Map<ResourceLocation, CorruptionRule> byId;

    public CorruptionRuleRegistry(Collection<CorruptionRule> rules) {
        Objects.requireNonNull(rules, "rules");

        List<CorruptionRule> ordered = new ArrayList<>(rules.size());
        Map<ResourceLocation, CorruptionRule> indexed = new HashMap<>();
        for (CorruptionRule rule : rules) {
            CorruptionRule nonNullRule = Objects.requireNonNull(rule, "rule");
            CorruptionRule previous = indexed.putIfAbsent(nonNullRule.id(), nonNullRule);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate corruption rule id: " + nonNullRule.id());
            }
            ordered.add(nonNullRule);
        }

        this.rules = List.copyOf(ordered);
        this.byId = Map.copyOf(indexed);
    }

    public Optional<CorruptionRule> rule(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(byId.get(id));
    }

    public List<CorruptionRule> all() {
        return rules;
    }
}
