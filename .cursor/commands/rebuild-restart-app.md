# Command: rebuild-restart-app

Scope: Android emulator runtime workflow only.

From the repository root:

```bash
./scripts/rebuild-restart-on-emulator.sh
```

This command:
- builds and installs the latest debug app (`installDebug`)
- force-stops the app
- starts `StartupActivity` again on the connected emulator/device

If multiple devices are connected:

```bash
ANDROID_SERIAL=<serial> ./scripts/rebuild-restart-on-emulator.sh
```
