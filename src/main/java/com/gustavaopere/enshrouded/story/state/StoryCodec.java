package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Deterministic NBT codec for server-global Lich/story state. */
public final class StoryCodec {
    private StoryCodec() {
    }

    public static CompoundTag encode(LichStoryState state) {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", state.schemaVersion());

        ListTag manifestations = new ListTag();
        state.manifestations().entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().stableKey()))
                .map(entry -> encodeManifestation(entry.getValue()))
                .forEach(manifestations::add);
        root.put("manifestations", manifestations);

        ListTag encounters = new ListTag();
        state.encounters().values().stream()
                .sorted(Comparator.comparing(record -> record.encounterId().toString()))
                .map(StoryCodec::encodeEncounter)
                .forEach(encounters::add);
        root.put("encounters", encounters);
        return root;
    }

    public static LichStoryState decode(CompoundTag root) {
        int schemaVersion = root.getInt("schema_version");
        StorySchema.requireSupported(schemaVersion);

        LinkedHashMap<ProgressionOwner, ManifestationRecord> manifestations = new LinkedHashMap<>();
        ListTag encodedManifestations = root.getList("manifestations", CompoundTag.TAG_COMPOUND);
        for (int index = 0; index < encodedManifestations.size(); index++) {
            CompoundTag tag = encodedManifestations.getCompound(index);
            ProgressionOwner owner = parseOwner(tag.getString("owner"));
            ManifestationRecord record = new ManifestationRecord(
                    owner,
                    tag.getInt("current_manifestation_index"),
                    decodeDefeated(tag.getList("defeated_manifestations", CompoundTag.TAG_INT))
            );
            if (manifestations.put(owner, record) != null) {
                throw new IllegalArgumentException("duplicate story progression owner: " + owner.stableKey());
            }
        }

        LinkedHashMap<UUID, EncounterRecord> encounters = new LinkedHashMap<>();
        ListTag encodedEncounters = root.getList("encounters", CompoundTag.TAG_COMPOUND);
        for (int index = 0; index < encodedEncounters.size(); index++) {
            EncounterRecord record = decodeEncounter(encodedEncounters.getCompound(index));
            if (encounters.put(record.encounterId(), record) != null) {
                throw new IllegalArgumentException("duplicate story encounter id: " + record.encounterId());
            }
        }

        return new LichStoryState(StorySchema.CURRENT_VERSION, manifestations, encounters);
    }

    private static CompoundTag encodeManifestation(ManifestationRecord record) {
        CompoundTag tag = new CompoundTag();
        tag.putString("owner", record.owner().stableKey());
        tag.putInt("current_manifestation_index", record.currentManifestationIndex());
        ListTag defeated = new ListTag();
        record.defeatedManifestationIndices().stream()
                .sorted()
                .map(IntTag::valueOf)
                .forEach(defeated::add);
        tag.put("defeated_manifestations", defeated);
        return tag;
    }

    private static CompoundTag encodeEncounter(EncounterRecord record) {
        CompoundTag tag = new CompoundTag();
        tag.putString("encounter_id", record.encounterId().toString());
        tag.putInt("manifestation_index", record.manifestationIndex());
        tag.putString("owner", record.owner().stableKey());
        tag.putString("outcome", record.outcome().id());
        tag.putBoolean("reward_issued", record.rewardIssued());
        record.entityId().ifPresent(entityId -> tag.putString("entity_id", entityId.toString()));
        return tag;
    }

    private static EncounterRecord decodeEncounter(CompoundTag tag) {
        UUID encounterId = parseUuid(tag.getString("encounter_id"), "encounter_id");
        int manifestationIndex = tag.getInt("manifestation_index");
        ProgressionOwner owner = parseOwner(tag.getString("owner"));
        EncounterOutcome outcome = EncounterOutcome.fromId(tag.getString("outcome"))
                .orElseThrow(() -> new IllegalArgumentException("invalid encounter outcome: " + tag.getString("outcome")));
        Optional<UUID> entityId = tag.contains("entity_id", CompoundTag.TAG_STRING)
                ? Optional.of(parseUuid(tag.getString("entity_id"), "entity_id"))
                : Optional.empty();
        return new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                outcome,
                tag.getBoolean("reward_issued"),
                entityId
        );
    }

    private static Set<Integer> decodeDefeated(ListTag list) {
        LinkedHashSet<Integer> defeated = new LinkedHashSet<>();
        for (int index = 0; index < list.size(); index++) {
            int manifestationIndex = list.getInt(index);
            StorySchema.requireManifestationIndex(manifestationIndex);
            if (!defeated.add(manifestationIndex)) {
                throw new IllegalArgumentException("duplicate defeated manifestation index: " + manifestationIndex);
            }
        }
        return defeated;
    }

    private static ProgressionOwner parseOwner(String stableKey) {
        return ProgressionOwner.parse(stableKey)
                .orElseThrow(() -> new IllegalArgumentException("invalid progression owner: " + stableKey));
    }

    private static UUID parseUuid(String raw, String field) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("invalid " + field + " UUID: " + raw, exception);
        }
    }
}
