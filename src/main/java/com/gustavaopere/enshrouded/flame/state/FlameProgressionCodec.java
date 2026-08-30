package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Deterministic NBT codec for server-global Flame progression. */
public final class FlameProgressionCodec {
    private FlameProgressionCodec() {
    }

    public static CompoundTag encode(FlameProgressionState state) {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", state.schemaVersion());

        ListTag owners = new ListTag();
        state.owners().entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().stableKey()))
                .map(FlameProgressionCodec::encodeOwner)
                .forEach(owners::add);
        root.put("owners", owners);
        return root;
    }

    public static FlameProgressionState decode(CompoundTag root) {
        int schemaVersion = root.getInt("schema_version");
        if (schemaVersion < FlameProgressionSchema.FIRST_VERSION
                || schemaVersion > FlameProgressionSchema.CURRENT_VERSION) {
            throw new UnsupportedFlameProgressionSchemaException(schemaVersion);
        }

        LinkedHashMap<ProgressionOwner, FlameProgressionState.OwnerProgression> owners = new LinkedHashMap<>();
        ListTag encodedOwners = root.getList("owners", CompoundTag.TAG_COMPOUND);
        for (int index = 0; index < encodedOwners.size(); index++) {
            CompoundTag tag = encodedOwners.getCompound(index);
            String ownerKey = tag.getString("owner");
            ProgressionOwner owner = ProgressionOwner.parse(ownerKey)
                    .orElseThrow(() -> new IllegalArgumentException("invalid progression owner: " + ownerKey));
            boolean nextLevelReady = schemaVersion >= 2 && tag.getBoolean("next_level_ready");
            FlameProgressionState.OwnerProgression progression = new FlameProgressionState.OwnerProgression(
                    tag.getInt("flame_level"),
                    tag.getInt("passage_level"),
                    nextLevelReady,
                    decodeRituals(tag.getList("completed_rituals", CompoundTag.TAG_STRING))
            );
            if (owners.put(owner, progression) != null) {
                throw new IllegalArgumentException("duplicate progression owner: " + owner.stableKey());
            }
        }
        // Supported legacy schemas are migrated eagerly to the current in-memory representation.
        return new FlameProgressionState(FlameProgressionSchema.CURRENT_VERSION, owners);
    }

    private static CompoundTag encodeOwner(Map.Entry<ProgressionOwner, FlameProgressionState.OwnerProgression> entry) {
        CompoundTag tag = new CompoundTag();
        tag.putString("owner", entry.getKey().stableKey());
        tag.putInt("flame_level", entry.getValue().flameLevel());
        tag.putInt("passage_level", entry.getValue().passageLevel());
        tag.putBoolean("next_level_ready", entry.getValue().nextLevelReady());

        ListTag rituals = new ListTag();
        entry.getValue().completedRituals().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .map(StringTag::valueOf)
                .forEach(rituals::add);
        tag.put("completed_rituals", rituals);
        return tag;
    }

    private static Set<ResourceLocation> decodeRituals(ListTag list) {
        LinkedHashSet<ResourceLocation> rituals = new LinkedHashSet<>();
        for (int index = 0; index < list.size(); index++) {
            String raw = list.getString(index);
            ResourceLocation ritual = ResourceLocation.tryParse(raw);
            if (ritual == null) {
                throw new IllegalArgumentException("invalid ritual id: " + raw);
            }
            if (!rituals.add(ritual)) {
                throw new IllegalArgumentException("duplicate completed ritual: " + ritual);
            }
        }
        return rituals;
    }
}
