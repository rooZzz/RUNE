#!/usr/bin/env bash
set -euo pipefail

AVD_NAME="${1:-${RUNE_AVD:-rune_tv}}"
CONFIG="${HOME}/.android/avd/${AVD_NAME}.avd/config.ini"

if [ ! -f "$CONFIG" ]; then
	echo "Missing AVD config: $CONFIG" >&2
	echo "Pass your AVD name as the first argument, or set RUNE_AVD." >&2
	exit 1
fi

cp "$CONFIG" "${CONFIG}.bak.$(date +%s)"
TMP=$(mktemp)
sed \
	-e 's/^hw\.keyboard=.*/hw.keyboard=yes/' \
	-e 's/^hw\.dPad=.*/hw.dPad=yes/' \
	-e 's/^hw\.mainKeys=.*/hw.mainKeys=yes/' \
	"$CONFIG" >"$TMP"
mv "$TMP" "$CONFIG"
grep -q '^hw.keyboard=' "$CONFIG" || echo 'hw.keyboard=yes' >>"$CONFIG"
grep -q '^hw.dPad=' "$CONFIG" || echo 'hw.dPad=yes' >>"$CONFIG"
grep -q '^hw.mainKeys=' "$CONFIG" || echo 'hw.mainKeys=yes' >>"$CONFIG"

echo "Updated $CONFIG. Cold-boot the emulator for changes to apply."
