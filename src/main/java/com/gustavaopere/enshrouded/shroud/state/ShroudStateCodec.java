package com.gustavaopere.enshrouded.shroud.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ShroudStateCodec {
    private static final Comparator<UUID> UUID_ORDER = Comparator.comparing(UUID::toString);

    private ShroudStateCodec() {
    }

    public static CompoundTag encode(ShroudWorldState state) {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", state.schemaVersion());

        ListTag cores = new ListTag();
        state.cores().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(UUID_ORDER))
                .map(Map.Entry::getValue)
                .map(ShroudStateCodec::encodeCore)
                .forEach(cores::add);
        root.put("cores", cores);

        ListTag regions = new ListTag();
        state.regions().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(UUID_ORDER))
                .map(Map.Entry::getValue)
                .map(ShroudStateCodec::encodeRegion)
                .forEach(regions::add);
        root.put("regions", regions);
        return root;
    }

    public static ShroudWorldState decode(CompoundTag root) {
        int schemaVersion = root.getInt("schema_version");
        if (schemaVersion != ShroudSchema.CURRENT_VERSION) {
            throw new UnsupportedShroudSchemaException(schemaVersion);
        }

        Map<UUID, ShroudCoreState> cores = decodeCores(root.getList("cores", CompoundTag.TAG_COMPOUND));
        Map<UUID, ShroudRegionState> regions = decodeRegions(root.getList("regions", CompoundTag.TAG_COMPOUND));
        return new ShroudWorldState(schemaVersion, cores, regions);
    }

    private static CompoundTag encodeCore(ShroudCoreState core) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", core.id().toString());
        tag.putInt("center_x", core.center().getX());
        tag.putInt("center_y", core.center().getY());
        tag.putInt("center_z", core.center().getZ());
        tag.putInt("tier", core.tier());
        tag.putString("lifecycle_state", core.lifecycleState());
        tag.putInt("max_influence_radius", core.maxInfluenceRadius());
        tag.putLong("expansion_seed", core.expansionSeed());
        tag.putLong("expansion_epoch", core.expansionEpoch());
        tag.putString("region_id", core.regionId().toString());
        return tag;
    }

    private static Map<UUID, ShroudCoreState> decodeCores(ListTag list) {
        LinkedHashMap<UUID, ShroudCoreState> result = new LinkedHashMap<>();
        for (int index = 0; index < list.size(); index++) {
            CompoundTag tag = list.getCompound(index);
            UUID id = parseUuid(tag.getString("id"), "core id");
            UUID regionId = parseUuid(tag.getString("region_id"), "core region id");
            ShroudCoreState state = new ShroudCoreState(
                    id,
                    new BlockPos(tag.getInt("center_x"), tag.getInt("center_y"), tag.getInt("center_z")),
                    tag.getInt("tier"),
                    tag.getString("lifecycle_state"),
                    tag.getInt("max_influence_radius"),
                    tag.getLong("expansion_seed"),
                    tag.getLong("expansion_epoch"),
                    regionId
            );
            if (result.put(id, state) != null) {
                throw new IllegalArgumentException("duplicate core id: " + id);
            }
        }
        return result;
    }

    private static CompoundTag encodeRegion(ShroudRegionState region) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", region.id().toString());
        tag.putString("core_id", region.coreId().toString());

        ListTag cells = new ListTag();
        List<ShroudCellState> orderedCells = new ArrayList<>(region.cells().values());
        orderedCells.sort(Comparator.comparing(ShroudCellState::position));
        orderedCells.stream().map(ShroudStateCodec::encodeCell).forEach(cells::add);
        tag.put("cells", cells);
        return tag;
    }

    private static Map<UUID, ShroudRegionState> decodeRegions(ListTag list) {
        LinkedHashMap<UUID, ShroudRegionState> result = new LinkedHashMap<>();
        for (int index = 0; index < list.size(); index++) {
            CompoundTag tag = list.getCompound(index);
            UUID id = parseUuid(tag.getString("id"), "region id");
            UUID coreId = parseUuid(tag.getString("core_id"), "region core id");
            Map<ShroudCellPos, ShroudCellState> cells = decodeCells(tag.getList("cells", CompoundTag.TAG_COMPOUND));
            ShroudRegionState state = new ShroudRegionState(id, coreId, cells);
            if (result.put(id, state) != null) {
                throw new IllegalArgumentException("duplicate region id: " + id);
            }
        }
        return result;
    }

    private static CompoundTag encodeCell(ShroudCellState cell) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", cell.position().x());
        tag.putInt("y", cell.position().y());
        tag.putInt("z", cell.position().z());
        tag.putDouble("intensity", cell.intensity());
        tag.putString("severity", cell.severity().id());
        return tag;
    }

    private static Map<ShroudCellPos, ShroudCellState> decodeCells(ListTag list) {
        LinkedHashMap<ShroudCellPos, ShroudCellState> result = new LinkedHashMap<>();
        for (int index = 0; index < list.size(); index++) {
            CompoundTag tag = list.getCompound(index);
            ShroudCellPos position = new ShroudCellPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            ShroudSeverity severity = ShroudSeverity.fromId(tag.getString("severity"))
                    .orElseThrow(() -> new IllegalArgumentException("unknown Shroud severity: " + tag.getString("severity")));
            ShroudCellState state = new ShroudCellState(position, tag.getDouble("intensity"), severity);
            if (result.put(position, state) != null) {
                throw new IllegalArgumentException("duplicate cell position: " + position);
            }
        }
        return result;
    }

    private static UUID parseUuid(String raw, String field) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("invalid " + field + ": " + raw, exception);
        }
    }
}
