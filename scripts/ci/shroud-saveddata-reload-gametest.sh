#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUN_DIR="$ROOT/runs/gameTestServer"
BUILD_DIR="$ROOT/build"
FIRST_LOG="$BUILD_DIR/shroud-saveddata-first.log"
RELOAD_LOG="$BUILD_DIR/shroud-saveddata-reload.log"
BOOT_TIMEOUT_SECONDS=240
KILL_AFTER_SECONDS=15
EXPOSURE_PLAYER_UUID='71090101-0000-4000-8000-000000000003'

command -v timeout >/dev/null 2>&1 || {
  echo 'GNU timeout is required for SavedData reload verification' >&2
  exit 1
}

mkdir -p "$BUILD_DIR"
rm -rf "$RUN_DIR"
mkdir -p "$RUN_DIR/world/serverconfig"
printf 'eula=true\n' > "$RUN_DIR/eula.txt"

# The first boot intentionally persists a mid-purification sentinel. The GameTest server keeps
# ticking production runtimes after that individual test succeeds, so use a world-local SERVER
# config only in this two-boot harness to prevent the sentinel from completing before shutdown.
# This does not change production defaults or the ordinary GameTest-server CI gate. NeoForge may
# normalize the partial TOML file on load; the second-boot sentinel is the authoritative behavioral
# proof that the harness budget remained low enough for restart verification.
cat > "$RUN_DIR/world/serverconfig/enshrouded-server.toml" <<'EOF'
[shroudCore]
regressionWorkPerTick = 1
EOF

run_gametest_boot() {
  local log_file="$1"
  rm -f "$log_file"

  set +e
  (
    cd "$ROOT"
    timeout --kill-after="${KILL_AFTER_SECONDS}s" "${BOOT_TIMEOUT_SECONDS}s" \
      ./gradlew --no-daemon runGameTestServer
  ) 2>&1 | tee "$log_file"
  local status=${PIPESTATUS[0]}
  set -e

  if grep -Fq 'No test functions were given!' "$log_file"; then
    echo 'GameTest discovery failed during SavedData reload verification' >&2
    return 1
  fi
  if (( status != 0 )); then
    echo "GameTest boot failed with status $status" >&2
    return "$status"
  fi
}

run_gametest_boot "$FIRST_LOG"
grep -Fq 'ENSHROUDED_FLAME_PROGRESSION_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the Flame progression sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_STORY_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the Story State sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_SHROUD_SAVEDDATA_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the Shroud SavedData sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_GROWTH_BLOCK_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the Shroud growth block sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_PURIFICATION_MID_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the mid-purification sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_PURIFIED_LEFTOVER_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the PURIFIED visual-leftover sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_ENTITY_CORRUPTION_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the entity corruption sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_EXPANSION_MID_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the mid-expansion sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_EXPOSURE_MID_CREATED' "$FIRST_LOG" || {
  echo 'First GameTest boot did not create the player exposure sentinel' >&2
  exit 1
}

for unexpected in \
  ENSHROUDED_FLAME_PROGRESSION_RELOADED \
  ENSHROUDED_STORY_RELOADED \
  ENSHROUDED_SHROUD_SAVEDDATA_RELOADED \
  ENSHROUDED_GROWTH_BLOCK_RELOADED \
  ENSHROUDED_PURIFICATION_MID_RELOADED \
  ENSHROUDED_ENTITY_CORRUPTION_RELOADED \
  ENSHROUDED_EXPANSION_MID_RELOADED \
  ENSHROUDED_EXPOSURE_MID_RELOADED; do
  if grep -Fq "$unexpected" "$FIRST_LOG"; then
    echo "First GameTest boot unexpectedly reported reload marker: $unexpected" >&2
    exit 1
  fi
done

mapfile -t FLAME_DATA_FILES < <(find "$RUN_DIR" -type f -name 'enshrouded_flame_progression.dat' -print)
if (( ${#FLAME_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one server-global enshrouded_flame_progression.dat after first boot, found ${#FLAME_DATA_FILES[@]}" >&2
  printf '%s\n' "${FLAME_DATA_FILES[@]:-}"
  exit 1
fi

mapfile -t STORY_DATA_FILES < <(find "$RUN_DIR" -type f -name 'enshrouded_story.dat' -print)
if (( ${#STORY_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one server-global enshrouded_story.dat after first boot, found ${#STORY_DATA_FILES[@]}" >&2
  printf '%s\n' "${STORY_DATA_FILES[@]:-}"
  exit 1
fi

mapfile -t SHROUD_DATA_FILES < <(find "$RUN_DIR" -type f -name 'enshrouded_shroud.dat' -print)
if (( ${#SHROUD_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one dimension-local enshrouded_shroud.dat after first boot, found ${#SHROUD_DATA_FILES[@]}" >&2
  printf '%s\n' "${SHROUD_DATA_FILES[@]:-}"
  exit 1
fi

mapfile -t EXPOSURE_PLAYER_DATA_FILES < <(find "$RUN_DIR" -type f -name "${EXPOSURE_PLAYER_UUID}.dat" -print)
if (( ${#EXPOSURE_PLAYER_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one exposure playerdata file after first boot, found ${#EXPOSURE_PLAYER_DATA_FILES[@]}" >&2
  printf '%s\n' "${EXPOSURE_PLAYER_DATA_FILES[@]:-}"
  exit 1
fi

run_gametest_boot "$RELOAD_LOG"
grep -Fq 'ENSHROUDED_FLAME_PROGRESSION_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reload the persisted Flame progression sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_STORY_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reconcile and reload the persisted Story State sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_LICH_REWARD_RELOADED_NO_REPLAY' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not prove exactly-once Lich reward replay protection' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_SHROUD_SAVEDDATA_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reload the persisted Shroud SavedData sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_GROWTH_BLOCK_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reload the persisted Shroud growth block sentinel' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_PURIFICATION_MID_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not resume persisted mid-purification state' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_PURIFIED_LEFTOVER_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not preserve PURIFIED terminal state with visual leftovers' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_ENTITY_CORRUPTION_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reload the persisted entity corruption attachment' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_EXPANSION_MID_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not rebuild and resume the persisted expansion frontier' >&2
  exit 1
}
grep -Fq 'ENSHROUDED_EXPOSURE_MID_RELOADED' "$RELOAD_LOG" || {
  echo 'Second GameTest boot did not reload and continue the player exposure reserve' >&2
  exit 1
}

for unexpected in \
  ENSHROUDED_FLAME_PROGRESSION_CREATED \
  ENSHROUDED_STORY_CREATED \
  ENSHROUDED_SHROUD_SAVEDDATA_CREATED \
  ENSHROUDED_GROWTH_BLOCK_CREATED \
  ENSHROUDED_PURIFICATION_MID_CREATED \
  ENSHROUDED_ENTITY_CORRUPTION_CREATED \
  ENSHROUDED_EXPANSION_MID_CREATED \
  ENSHROUDED_EXPOSURE_MID_CREATED; do
  if grep -Fq "$unexpected" "$RELOAD_LOG"; then
    echo "Second GameTest boot recreated persisted state instead of loading it: $unexpected" >&2
    exit 1
  fi
done

mapfile -t FLAME_RELOAD_DATA_FILES < <(find "$RUN_DIR" -type f -name 'enshrouded_flame_progression.dat' -print)
if (( ${#FLAME_RELOAD_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one server-global enshrouded_flame_progression.dat after reload, found ${#FLAME_RELOAD_DATA_FILES[@]}" >&2
  printf '%s\n' "${FLAME_RELOAD_DATA_FILES[@]:-}"
  exit 1
fi
if [[ "${FLAME_RELOAD_DATA_FILES[0]}" != "${FLAME_DATA_FILES[0]}" ]]; then
  echo 'Flame progression data file moved between boots instead of reloading in place' >&2
  printf 'first: %s\nsecond: %s\n' "${FLAME_DATA_FILES[0]}" "${FLAME_RELOAD_DATA_FILES[0]}" >&2
  exit 1
fi

mapfile -t STORY_RELOAD_DATA_FILES < <(find "$RUN_DIR" -type f -name 'enshrouded_story.dat' -print)
if (( ${#STORY_RELOAD_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one server-global enshrouded_story.dat after reload, found ${#STORY_RELOAD_DATA_FILES[@]}" >&2
  printf '%s\n' "${STORY_RELOAD_DATA_FILES[@]:-}"
  exit 1
fi
if [[ "${STORY_RELOAD_DATA_FILES[0]}" != "${STORY_DATA_FILES[0]}" ]]; then
  echo 'Story State data file moved between boots instead of reloading in place' >&2
  printf 'first: %s\nsecond: %s\n' "${STORY_DATA_FILES[0]}" "${STORY_RELOAD_DATA_FILES[0]}" >&2
  exit 1
fi

mapfile -t EXPOSURE_RELOAD_PLAYER_DATA_FILES < <(find "$RUN_DIR" -type f -name "${EXPOSURE_PLAYER_UUID}.dat" -print)
if (( ${#EXPOSURE_RELOAD_PLAYER_DATA_FILES[@]} != 1 )); then
  echo "Expected exactly one exposure playerdata file after reload, found ${#EXPOSURE_RELOAD_PLAYER_DATA_FILES[@]}" >&2
  printf '%s\n' "${EXPOSURE_RELOAD_PLAYER_DATA_FILES[@]:-}"
  exit 1
fi
if [[ "${EXPOSURE_RELOAD_PLAYER_DATA_FILES[0]}" != "${EXPOSURE_PLAYER_DATA_FILES[0]}" ]]; then
  echo 'Exposure playerdata file moved between boots instead of reloading in place' >&2
  printf 'first: %s\nsecond: %s\n' "${EXPOSURE_PLAYER_DATA_FILES[0]}" "${EXPOSURE_RELOAD_PLAYER_DATA_FILES[0]}" >&2
  exit 1
fi

echo "Flame progression, Story State, exactly-once Lich reward, Shroud SavedData, growth block, mid-expansion frontier, player exposure, purification and entity corruption two-boot reload GameTest: PASS (Flame: ${FLAME_DATA_FILES[0]}; Story: ${STORY_DATA_FILES[0]}; Shroud: ${SHROUD_DATA_FILES[0]}; Exposure playerdata: ${EXPOSURE_PLAYER_DATA_FILES[0]})"
