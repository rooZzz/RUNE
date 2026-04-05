#!/usr/bin/env bash
set -euo pipefail

ADB="${ADB:-adb}"
case "${1:-}" in
	up) KEY=19 ;;
	down) KEY=20 ;;
	left) KEY=21 ;;
	right) KEY=22 ;;
	ok | enter) KEY=23 ;;
	back) KEY=4 ;;
	home) KEY=3 ;;
	*)
		echo "Usage: $0 up|down|left|right|ok|back|home" >&2
		echo "Uses adb shell input keyevent. Set ANDROID_SERIAL if multiple devices." >&2
		exit 1
		;;
esac

"$ADB" ${ANDROID_SERIAL:+-s "$ANDROID_SERIAL"} shell input keyevent "$KEY"
