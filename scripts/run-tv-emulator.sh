#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

ADB="${ADB:-adb}"
GRADLEW="${GRADLEW:-./gradlew}"

usage() {
	echo "Usage: $0 [avd_name]" >&2
	echo "  Boots a TV emulator if none is connected, then installDebug and start RUNE." >&2
	echo "  If you have several AVDs, pass the name or set RUNE_AVD." >&2
	exit 1
}

if [ "${1:-}" = "-h" ] || [ "${1:-}" = "--help" ]; then
	usage
fi

if ! command -v "$ADB" >/dev/null 2>&1; then
	echo "adb not found. Install platform-tools and ensure adb is on PATH, or set ADB to the full path." >&2
	exit 1
fi

find_emulator_bin() {
	if [ -n "${ANDROID_HOME:-}" ] && [ -x "${ANDROID_HOME}/emulator/emulator" ]; then
		echo "${ANDROID_HOME}/emulator/emulator"
		return 0
	fi
	if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -x "${ANDROID_SDK_ROOT}/emulator/emulator" ]; then
		echo "${ANDROID_SDK_ROOT}/emulator/emulator"
		return 0
	fi
	if command -v emulator >/dev/null 2>&1; then
		command -v emulator
		return 0
	fi
	return 1
}

pick_avd() {
	local em_bin="$1"
	local requested="${2:-}"
	local all
	all=$("$em_bin" -list-avds 2>/dev/null | sed '/^$/d' || true)
	if [ -z "$all" ]; then
		echo "No AVDs found. Create a TV system image AVD (avdmanager or Device Manager), then retry." >&2
		exit 1
	fi
	if [ -n "$requested" ]; then
		if echo "$all" | grep -qxF "$requested"; then
			echo "$requested"
			return 0
		fi
		echo "Unknown AVD: $requested" >&2
		echo "Available AVDs:" >&2
		echo "$all" >&2
		exit 1
	fi
	if [ -n "${RUNE_AVD:-}" ]; then
		if echo "$all" | grep -qxF "$RUNE_AVD"; then
			echo "$RUNE_AVD"
			return 0
		fi
		echo "RUNE_AVD=$RUNE_AVD is not in the AVD list." >&2
		echo "$all" >&2
		exit 1
	fi
	local count
	count=$(echo "$all" | wc -l | tr -d ' ')
	if [ "$count" -eq 1 ]; then
		echo "$all" | head -n1
		return 0
	fi
	echo "Several AVDs exist. Pass one by name or set RUNE_AVD:" >&2
	echo "$all" >&2
	exit 1
}

wait_for_boot_completed() {
	local serial="$1"
	echo "Waiting for boot (sys.boot_completed)..."
	local boot=""
	while true; do
		boot=$("$ADB" -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n' || true)
		if [ "$boot" = "1" ]; then
			break
		fi
		sleep 2
	done
}

DEVICE_COUNT=$("$ADB" devices | awk 'NR > 1 && $2 == "device" { c++ } END { print c + 0 }')

if [ "$DEVICE_COUNT" -eq 0 ]; then
	EMULATOR_BIN=$(find_emulator_bin) || {
		echo "emulator binary not found. Set ANDROID_HOME or ANDROID_SDK_ROOT to your Android SDK, or add emulator to PATH." >&2
		exit 1
	}
	AVD_NAME=$(pick_avd "$EMULATOR_BIN" "${1:-}")
	if [ -f "$ROOT/scripts/patch-avd-tv-input.sh" ]; then
		"$ROOT/scripts/patch-avd-tv-input.sh" "$AVD_NAME"
	fi
	echo "Starting emulator AVD: $AVD_NAME"
	# shellcheck disable=SC2086
	"$EMULATOR_BIN" -avd "$AVD_NAME" ${RUNE_EMULATOR_ARGS:-} >/dev/null 2>&1 &
	echo "Waiting for adb device..."
	"$ADB" wait-for-device
	export ANDROID_SERIAL=$("$ADB" devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')
	if [ -z "${ANDROID_SERIAL:-}" ]; then
		echo "adb wait-for-device returned but no serial found." >&2
		exit 1
	fi
	wait_for_boot_completed "$ANDROID_SERIAL"
fi

if [ "$DEVICE_COUNT" -gt 1 ] && [ -z "${ANDROID_SERIAL:-}" ]; then
	echo "Multiple devices are connected. Pick one and run:" >&2
	echo "  ANDROID_SERIAL=<serial> $0" >&2
	echo "Connected devices:" >&2
	"$ADB" devices -l >&2
	exit 1
fi

if [ -z "${ANDROID_SERIAL:-}" ] && [ "${DEVICE_COUNT:-0}" -eq 1 ]; then
	export ANDROID_SERIAL=$("$ADB" devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')
fi

ADB_FLAGS=()
if [ -n "${ANDROID_SERIAL:-}" ]; then
	ADB_FLAGS=(-s "$ANDROID_SERIAL")
fi

"$GRADLEW" installDebug

"$ADB" "${ADB_FLAGS[@]}" shell am start -n rune.enhanced.tv.debug/org.jellyfin.androidtv.ui.startup.StartupActivity

echo "Started RUNE (debug) on ${ANDROID_SERIAL:-default device}."
