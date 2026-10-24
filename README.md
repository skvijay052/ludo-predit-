# Ludo-Predict (starter)

Android Kotlin starter for a floating Ludo strategy assistant.

## Current starter features
- App screen explaining setup
- User-controlled Android overlay permission
- Draggable floating `L` bubble
- Strategy checklist and manual situation-based suggestions
- GitHub Actions workflow to build and upload a debug APK

## Important limitations
This version does **not** read the Ludo King screen, recognize board pieces, or predict dice results. Suggestions are generic strategy guidance until board recognition and legal-move modeling are implemented and tested against the exact Ludo King rules/version. Do not use it to automate taps or bypass the game’s protections.

## Build APK on GitHub
1. Open **Actions** and run **Build Ludo-Predict APK** (or push a commit).
2. Open the successful workflow run and download the `ludo-predict-debug-apk` artifact ZIP.
3. Extract `app-debug.apk` on your Android phone and install it. Android may ask you to allow installs from the app used to open the APK.

## Android permission
The app requests `SYSTEM_ALERT_WINDOW` only so you can explicitly enable the floating bubble. Android sends you to its special app-access settings screen; the permission is not silently enabled. The starter does not request contacts, location, microphone, camera, or internet access.

## Next phase
Implement user-triggered screenshot import/capture, board detection, token-position confirmation, rule configuration, and a legal-move scoring engine. Any screen-capture feature should use Android's system consent flow and explain what is captured.
