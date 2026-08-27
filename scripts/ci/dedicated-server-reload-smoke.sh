#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUN_DIR="$ROOT/runs/server"
BUILD_DIR="$ROOT/build"
FIFO="$BUILD_DIR/server-smoke.stdin"
FIRST_LOG="$BUILD_DIR/server-smoke-first.log"
RELOAD_LOG="$BUILD_DIR/server-smoke-reload.log"
SERVER_PID=""
SERVER_LOG=""

mkdir -p "$BUILD_DIR"
rm -rf "$RUN_DIR"
mkdir -p "$RUN_DIR"
printf 'eula=true\n' > "$RUN_DIR/eula.txt"

cleanup() {
  if [[ -n "${SERVER_PID:-}" ]] && kill -0 "$SERVER_PID" 2>/dev/null; then
    printf 'stop\n' >&3 2>/dev/null || true
    sleep 2
    kill "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
  exec 3>&- 2>/dev/null || true
  rm -f "$FIFO"
}
trap cleanup EXIT

wait_for_regex() {
  local log_file="$1"
  local pattern="$2"
  local timeout_seconds="$3"
  local elapsed=0

  while (( elapsed < timeout_seconds )); do
    if [[ -f "$log_file" ]] && grep -Eq "$pattern" "$log_file"; then
      return 0
    fi
    if [[ -n "${SERVER_PID:-}" ]] && ! kill -0 "$SERVER_PID" 2>/dev/null; then
      cat "$log_file" 2>/dev/null || true
      echo "Server exited before log pattern appeared: $pattern" >&2
      return 1
    fi
    sleep 1
    ((elapsed += 1))
  done

  cat "$log_file" 2>/dev/null || true
  echo "Timed out waiting for log pattern: $pattern" >&2
  return 1
}

wait_for_fixed() {
  local log_file="$1"
  local text="$2"
  local timeout_seconds="$3"
  local elapsed=0

  while (( elapsed < timeout_seconds )); do
    if [[ -f "$log_file" ]] && grep -Fq "$text" "$log_file"; then
      return 0
    fi
    if [[ -n "${SERVER_PID:-}" ]] && ! kill -0 "$SERVER_PID" 2>/dev/null; then
      cat "$log_file" 2>/dev/null || true
      echo "Server exited before log marker appeared: $text" >&2
      return 1
    fi
    sleep 1
    ((elapsed += 1))
  done

  cat "$log_file" 2>/dev/null || true
  echo "Timed out waiting for log marker: $text" >&2
  return 1
}

start_server() {
  SERVER_LOG="$1"
  rm -f "$FIFO" "$SERVER_LOG"
  mkfifo "$FIFO"
  exec 3<> "$FIFO"
  (
    cd "$ROOT"
    ./gradlew --no-daemon runServer < "$FIFO" > "$SERVER_LOG" 2>&1
  ) &
  SERVER_PID=$!

  wait_for_regex "$SERVER_LOG" 'Done \([0-9.]+s\)! For help, type "help"' 180
  wait_for_fixed "$SERVER_LOG" 'Enshrouded bootstrap complete' 30
}

graceful_stop() {
  printf 'stop\n' >&3

  local elapsed=0
  while kill -0 "$SERVER_PID" 2>/dev/null; do
    if (( elapsed >= 60 )); then
      cat "$SERVER_LOG" 2>/dev/null || true
      echo 'Timed out waiting for dedicated server to stop gracefully' >&2
      kill "$SERVER_PID" 2>/dev/null || true
      wait "$SERVER_PID" 2>/dev/null || true
      exec 3>&-
      rm -f "$FIFO"
      SERVER_PID=""
      return 1
    fi
    sleep 1
    ((elapsed += 1))
  done

  local status=0
  wait "$SERVER_PID" || status=$?
  exec 3>&-
  rm -f "$FIFO"
  SERVER_PID=""

  if (( status != 0 )); then
    cat "$SERVER_LOG" 2>/dev/null || true
    echo "Dedicated server did not stop cleanly (status $status)" >&2
    return "$status"
  fi
}

start_server "$FIRST_LOG"
printf 'scoreboard objectives add ensh_reload dummy\n' >&3
printf 'scoreboard players set ensh_sentinel ensh_reload 1\n' >&3
printf 'save-all flush\n' >&3
wait_for_fixed "$FIRST_LOG" 'Saved the game' 60
graceful_stop

test -f "$RUN_DIR/world/level.dat" || {
  echo 'First boot did not produce world/level.dat' >&2
  exit 1
}

start_server "$RELOAD_LOG"
printf 'execute if score ensh_sentinel ensh_reload matches 1 run say ENSHROUDED_RELOAD_SENTINEL_OK\n' >&3
wait_for_fixed "$RELOAD_LOG" 'ENSHROUDED_RELOAD_SENTINEL_OK' 60
graceful_stop

echo 'Dedicated-server save/reload smoke test: PASS'
