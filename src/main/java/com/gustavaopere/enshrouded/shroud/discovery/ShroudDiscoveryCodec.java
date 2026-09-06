package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.datafix.EnshroudedDataFixer;
import com.gustavaopere.enshrouded.datafix.PersistentDataValidation;
import com.gustavaopere.enshrouded.datafix.PersistentSubsystem;
import com.gustavaopere.enshrouded.datafix.UnsupportedPersistentSchemaException;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Strict deterministic NBT codec for owner-scoped Shroud discovery knowledge. */
public final class ShroudDiscoveryCodec {
    private static final Comparator<UUID> UUID_ORDER = Comparator.comparing(UUID::toString);

    private ShroudDiscoveryCodec() {
    }

    public static CompoundTag encode(ShroudDiscoveryState state) {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", ShroudDiscoverySchema.CURRENT_VERSION);

        ListTag owners = new ListTag();
        state.byOwnerStableKey().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(ShroudDiscoveryCodec::encodeOwner)
                .forEach(owners::add);
        root.put("owners", owners);
        return root;
    }

    public static ShroudDiscoveryState decode(CompoundTag root) {
        final CompoundTag migrated;
        try {
            migrated = EnshroudedDataFixer.migrate(PersistentSubsystem.SHROUD_DISCOVERY, root);
        } catch (UnsupportedPersistentSchemaException exception) {
            throw new UnsupportedShroudDiscoverySchemaException(exception.schemaVersion());
        }

        LinkedHashMap<String, Map<UUID, DiscoveredCore>> byOwner = new LinkedHashMap<>();
        ListTag owners = PersistentDataValidation.requireList(
                migrated,
                "owners",
                CompoundTag.TAG_COMPOUND,
                PersistentSubsystem.SHROUD_DISCOVERY
        );
        for (int index = 0; index < owners.size(); index++) {
            CompoundTag ownerTag = owners.getCompound(index);
            String ownerKey = ownerTag.getString("owner");
            if (ProgressionOwner.parse(ownerKey).isEmpty()) {
                throw new IllegalArgumentException("invalid progression owner stable key: " + ownerKey);
            }
            LinkedHashMap<UUID, DiscoveredCore> cores = decodeCores(PersistentDataValidation.requireList(
                    ownerTag,
                    "cores",
                    CompoundTag.TAG_COMPOUND,
                    PersistentSubsystem.SHROUD_DISCOVERY
            ));
            if (byOwner.put(ownerKey, cores) != null) {
                throw new IllegalArgumentException("duplicate discovery owner: " + ownerKey);
            }
        }
        return ShroudDiscoveryState.fromStableKeys(byOwner);
    }

    private static CompoundTag encodeOwner(Map.Entry<String, Map<UUID, DiscoveredCore>> entry) {
        CompoundTag tag = new CompoundTag();
        tag.putString("owner", entry.getKey());
        ListTag cores = new ListTag();
        entry.getValue().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(UUID_ORDER))
                .map(Map.Entry::getValue)
                .map(ShroudDiscoveryCodec::encodeCore)
                .forEach(cores::add);
        tag.put("cores", cores);
        return tag;
    }

    private static CompoundTag encodeCore(DiscoveredCore core) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", core.coreId().toString());
        tag.putString("dimension", core.dimensionId());
        tag.putInt("x", core.pos().getX());
        tag.putInt("y", core.pos().getY());
        tag.putInt("z", core.pos().getZ());
        tag.putString("lifecycle", core.lifecycle().id());
        return tag;
    }

    private static LinkedHashMap<UUID, DiscoveredCore> decodeCores(ListTag list) {
        LinkedHashMap<UUID, DiscoveredCore> cores = new LinkedHashMap<>();
        for (int index = 0; index < list.size(); index++) {
            CompoundTag tag = list.getCompound(index);
            UUID coreId = parseUuid(tag.getString("id"));
            String lifecycleId = tag.getString("lifecycle");
            CoreLifecycleState lifecycle = CoreLifecycleState.fromId(lifecycleId)
                    .orElseThrow(() -> new IllegalArgumentException("unknown discovered core lifecycle: " + lifecycleId));
            DiscoveredCore core = new DiscoveredCore(
                    coreId,
                    tag.getString("dimension"),
                    new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
                    lifecycle);
            if (cores.put(coreId, core) != null) {
                throw new IllegalArgumentException("duplicate discovered core id: " + coreId);
            }
        }
        return cores;
    }

    private static UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("invalid discovered core id: " + raw, exception);
        }
    }
}
