package com.gustavaopere.enshrouded.datafix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Central owner for explicit Enshrouded persistence migrations.
 *
 * <p>Migration never catches malformed/future data and substitutes defaults. Callers either receive
 * a validated current-version copy or a controlled diagnostic exception.</p>
 */
public final class EnshroudedDataFixer {
    public static final String SCHEMA_VERSION_TAG = "schema_version";

    private static final Logger LOGGER = LoggerFactory.getLogger(EnshroudedDataFixer.class);
    private static final Map<PersistentSubsystem, Map<Integer, MigrationStep>> MIGRATIONS = buildMigrations();

    private EnshroudedDataFixer() {
    }

    /** Returns a migrated copy. The caller's input tag is never modified. */
    public static CompoundTag migrate(PersistentSubsystem subsystem, CompoundTag input) {
        Objects.requireNonNull(subsystem, "subsystem");
        Objects.requireNonNull(input, "input");
        if (!input.contains(SCHEMA_VERSION_TAG, Tag.TAG_INT)) {
            PersistentDataFormatException failure = new PersistentDataFormatException(
                    subsystem,
                    "missing integer " + SCHEMA_VERSION_TAG
            );
            LOGGER.error("Refusing to load malformed Enshrouded persistence", failure);
            throw failure;
        }

        int version = input.getInt(SCHEMA_VERSION_TAG);
        if (version < subsystem.oldestMigratableVersion() || version > subsystem.currentVersion()) {
            UnsupportedPersistentSchemaException failure = new UnsupportedPersistentSchemaException(subsystem, version);
            LOGGER.error("Refusing to reset unsupported Enshrouded persistence: {}", failure.getMessage());
            throw failure;
        }

        CompoundTag current = input.copy();
        while (version < subsystem.currentVersion()) {
            MigrationStep step = MIGRATIONS.getOrDefault(subsystem, Map.of()).get(version);
            if (step == null) {
                PersistentDataFormatException failure = new PersistentDataFormatException(
                        subsystem,
                        "no migration registered from schema " + version + " to current " + subsystem.currentVersion()
                );
                LOGGER.error("Refusing to skip missing Enshrouded migration step", failure);
                throw failure;
            }
            current = Objects.requireNonNull(step.migration().migrate(current), "migration result");
            int producedVersion = current.getInt(SCHEMA_VERSION_TAG);
            if (producedVersion != step.toVersion()) {
                throw new IllegalStateException(
                        "migration for " + subsystem.id() + " produced schema " + producedVersion
                                + " instead of " + step.toVersion()
                );
            }
            LOGGER.info("Migrated Enshrouded {} persistence schema {} -> {}",
                    subsystem.id(), version, producedVersion);
            version = producedVersion;
        }
        return current;
    }

    private static Map<PersistentSubsystem, Map<Integer, MigrationStep>> buildMigrations() {
        EnumMap<PersistentSubsystem, Map<Integer, MigrationStep>> bySubsystem =
                new EnumMap<>(PersistentSubsystem.class);
        register(bySubsystem, PersistentSubsystem.SHROUD, 1, 2, EnshroudedDataFixer::versionBumpToTwo);
        register(bySubsystem, PersistentSubsystem.SHROUD_DISCOVERY, 1, 2, EnshroudedDataFixer::versionBumpToTwo);
        register(bySubsystem, PersistentSubsystem.EXPOSURE, 1, 2, EnshroudedDataFixer::versionBumpToTwo);
        register(bySubsystem, PersistentSubsystem.ENTITY_CORRUPTION, 1, 2, EnshroudedDataFixer::versionBumpToTwo);
        register(bySubsystem, PersistentSubsystem.FLAME_PROGRESSION, 1, 2, EnshroudedDataFixer::flameOneToTwo);
        register(bySubsystem, PersistentSubsystem.STORY, 1, 2, EnshroudedDataFixer::versionBumpToTwo);
        return Map.copyOf(bySubsystem);
    }

    private static void register(
            EnumMap<PersistentSubsystem, Map<Integer, MigrationStep>> bySubsystem,
            PersistentSubsystem subsystem,
            int fromVersion,
            int toVersion,
            SchemaMigration migration) {
        Map<Integer, MigrationStep> mutable = new LinkedHashMap<>(bySubsystem.getOrDefault(subsystem, Map.of()));
        if (mutable.put(fromVersion, new MigrationStep(toVersion, migration)) != null) {
            throw new IllegalStateException("duplicate migration for " + subsystem.id() + " schema " + fromVersion);
        }
        bySubsystem.put(subsystem, Map.copyOf(mutable));
    }

    private static CompoundTag versionBumpToTwo(CompoundTag input) {
        CompoundTag migrated = SchemaMigration.copyOf(input);
        migrated.putInt(SCHEMA_VERSION_TAG, 2);
        return migrated;
    }

    private static CompoundTag flameOneToTwo(CompoundTag input) {
        CompoundTag migrated = SchemaMigration.copyOf(input);
        if (!migrated.contains("owners", Tag.TAG_LIST)) {
            throw new PersistentDataFormatException(PersistentSubsystem.FLAME_PROGRESSION, "missing owners list");
        }
        ListTag owners = migrated.getList("owners", Tag.TAG_COMPOUND);
        for (int index = 0; index < owners.size(); index++) {
            CompoundTag owner = owners.getCompound(index);
            if (owner.contains("next_level_ready") && !owner.contains("next_level_ready", Tag.TAG_BYTE)) {
                throw new PersistentDataFormatException(
                        PersistentSubsystem.FLAME_PROGRESSION,
                        "owner next_level_ready is not a boolean/byte at index " + index
                );
            }
            if (!owner.contains("next_level_ready")) {
                owner.putBoolean("next_level_ready", false);
            }
        }
        migrated.putInt(SCHEMA_VERSION_TAG, 2);
        return migrated;
    }

    private record MigrationStep(int toVersion, SchemaMigration migration) {
        private MigrationStep {
            if (toVersion < 1) {
                throw new IllegalArgumentException("toVersion must be >= 1");
            }
            Objects.requireNonNull(migration, "migration");
        }
    }
}
