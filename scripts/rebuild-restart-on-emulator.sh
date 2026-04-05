#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

ADB="${ADB:-adb}"
GRADLEW="${GRADLEW:-./gradlew}"
APP_ID="${APP_ID:-rune.enhanced.tv.debug}"
ACTIVITY="${ACTIVITY:-org.jellyfin.androidtv.ui.startup.StartupActivity}"

if ! command -v "$ADB" >/dev/null 2>&1; then
	echo "adb not found. Install platform-tools and ensure adb is on PATH, or set ADB to the full path." >&2
	exit 1
fi

DEVICE_COUNT=$("$ADB" devices | awk 'NR > 1 && $2 == "device" { c++ } END { print c + 0 }')

if [ "$DEVICE_COUNT" -eq 0 ]; then
	echo "No connected emulator/device found. Start an emulator first." >&2
	exit 1
fi

if [ "$DEVICE_COUNT" -gt 1 ] && [ -z "${ANDROID_SERIAL:-}" ]; then
	echo "Multiple devices are connected. Pick one and run:" >&2
	echo "  ANDROID_SERIAL=<serial> $0" >&2
	echo "Connected devices:" >&2
	"$ADB" devices -l >&2
	exit 1
fi

if [ -z "${ANDROID_SERIAL:-}" ] && [ "$DEVICE_COUNT" -eq 1 ]; then
	export ANDROID_SERIAL=$("$ADB" devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')
fi

ADB_FLAGS=()
if [ -n "${ANDROID_SERIAL:-}" ]; then
	ADB_FLAGS=(-s "$ANDROID_SERIAL")
fi

"$GRADLEW" installDebug
"$ADB" "${ADB_FLAGS[@]}" shell am force-stop "$APP_ID"
"$ADB" "${ADB_FLAGS[@]}" shell am start -n "${APP_ID}/${ACTIVITY}"

echo "Rebuilt, reinstalled, and restarted RUNE on ${ANDROID_SERIAL:-default device}."
