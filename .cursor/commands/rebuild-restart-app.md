# Rebuild and restart app on open emulator

From the repository root, run in a terminal:

```bash
./scripts/rebuild-restart-on-emulator.sh
```

This command:
- builds and installs the latest debug app (`installDebug`)
- force-stops the app
- starts `StartupActivity` again on the connected emulator/device

If multiple devices are connected, set `ANDROID_SERIAL`:

```bash
ANDROID_SERIAL=<serial> ./scripts/rebuild-restart-on-emulator.sh
```
