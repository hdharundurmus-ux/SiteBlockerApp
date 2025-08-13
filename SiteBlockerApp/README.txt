SiteBlockerApp - Android Studio project (Java)
-------------------------------------------
What this is
- A minimal Android app that contains a WebView and a simple site-block list.
- Add domains (e.g. example.com) via "Add site to block list". The WebView will block navigations to
  those domains and show a dialog.
- This is NOT a system-wide blocker. It blocks pages opened inside this app's WebView.

How to use (minimum effort)
1) Download and unzip the project folder on your computer.
2) Open Android Studio -> Open an existing project -> select the folder 'SiteBlockerApp'.
3) Let Gradle sync. Android Studio will download the Gradle plugin & SDK components as needed.
4) Connect your Android device (or use emulator) and run the app, or Build > Build Bundle(s) / APK(s) to generate an APK.
5) Install the APK on your phone (allow install from unknown sources if needed).

Notes & limitations
- This app blocks only pages inside its own WebView. It cannot (without VPN/root) block other browsers or apps.
- For system-wide blocking, a VPN-based approach is required (more complex).
- If you want, I can extend this project: export/import block lists, password-lock settings, schedule, or build a signed APK for you.

Files included:
- build.gradle, settings.gradle
- app/ (Android Studio module)
  - src/main/java/com/example/siteblocker/MainActivity.java
  - src/main/res/layout/activity_main.xml
  - src/main/AndroidManifest.xml
