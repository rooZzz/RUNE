# Run TV emulator

From the repository root, run in a terminal:

```bash
./scripts/run-tv-emulator.sh
```

Optional: `./scripts/run-tv-emulator.sh <avd_name>` or set `RUNE_AVD` if more than one AVD exists. Set `ANDROID_HOME` or `ANDROID_SDK_ROOT` so `emulator` is found when no device is connected.

In Cursor you can also use **Tasks: Run Task** and pick **RUNE: Run TV emulator** (`.vscode/tasks.json`).
