# Command: run-tv-emulator

Scope: Android emulator runtime workflow only.

From the repository root:

```bash
./scripts/run-tv-emulator.sh
```

Optional:
- `./scripts/run-tv-emulator.sh <avd_name>`
- `RUNE_AVD=<avd_name> ./scripts/run-tv-emulator.sh`

If no emulator is running, set `ANDROID_HOME` or `ANDROID_SDK_ROOT` so `emulator` can be found.

In Cursor you can also use **Tasks: Run Task** and pick **RUNE: Run TV emulator** (`.vscode/tasks.json`).
