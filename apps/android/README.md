# ScanBug Android — read-only UI explorer POC

A native Kotlin Android companion to ScanBug. It inspects the accessibility tree exposed by a user-selected installed app directly on the phone.

## Implemented

- Lists launchable apps visible under Android package-visibility rules.
- Opens a user-selected app and scopes the ScanBug session to that package.
- Uses Android Accessibility Service, enabled explicitly by the user, to inspect the selected app's exposed UI tree.
- Shows a visible ScanBug overlay only while the selected target app is active; unrelated content-change events do not repeatedly recreate the overlay.
- **Explore screen** creates a grouped inventory of exposed inputs, buttons/actions, tabs, selections and other controls.
- Each inventory entry includes accessible label, inferred control type, Android resource ID (when exposed), class, screen bounds, hierarchy path and available state flags.
- Password values and editable field contents are never read for display or included in reports.
- **Share inventory** opens Android's system share sheet only after the user taps it. No inventory is automatically uploaded, saved or transmitted.
- **Manual test-flow recorder** captures only user-generated click events in the selected app while the user explicitly starts recording. It records labels and UI metadata, never enters text or invokes actions.
- The recorder omits clicks whose labels match the conservative sensitive/destructive-action policy. It keeps the flow in memory only until the session ends and shares it only after the user explicitly taps Share test flow.
- **Review assertions** lets the tester define an expected result, record the observed result, and mark each step PASS, FAIL, BLOCKED or NOT RUN. These are manual assessments, not automated assertions.
- **Save test case** stores one reusable test case locally on the phone; **Load saved** restores it for review or sharing. The saved case remains on the device until app data is cleared or the app is uninstalled.
- Shared test-case reports include each step, expected result, actual result and manual status.
- No playback/replay is implemented; recorded steps remain a human-reviewed test case.
- The overlay has no target-app tap, text-entry, scroll, gesture or replay function.
- A prominent in-app Accessibility disclosure appears before the user is sent to Android Accessibility Settings. The user must affirmatively accept; declining does not enable the service.

## Accessibility disclosure and privacy

The Accessibility Service can inspect UI content exposed by the selected app while the service is enabled. This is inherently sensitive access. ScanBug displays accessible labels and UI metadata locally. Editable field values and password values are excluded. A report is shared only when the user explicitly requests it through Android's share sheet.

Test only apps you own or are authorized to test. Do not use the POC with sensitive personal or financial apps. The user can disable the service at any time in Android Accessibility Settings.

This app is a QA/testing utility, not an accessibility tool designed to assist people with disabilities. It must not set `isAccessibilityTool=true`. For Google Play distribution, the developer must accurately complete the Accessibility API declaration, provide the required disclosure/demo evidence, and comply with the current policy. Approval is not guaranteed. A debug APK distributed outside Google Play may still trigger Play Protect warnings; these changes do not guarantee that warning will disappear.

## Limitations

This is not a complete mobile automation tester. It records user-performed click steps and supports manually authored expected/actual results with a tester-selected status. It does not automatically assert app behavior, replay journeys, take screenshots, perform actions, comprehensively detect crashes, or generate formal automated test reports. Only UI exposed through Android accessibility APIs can be discovered; custom-drawn, protected, or unlabeled controls may not appear. Resource IDs and bounds are provided only when Android exposes them.

## Build with Android Studio

1. Install Android Studio with Android SDK Platform 35 and JDK 17.
2. Open the `apps/android` folder as an Android Studio project.
3. Allow Gradle sync to download the Android Gradle Plugin and Kotlin plugin.
4. Connect an Android phone with USB debugging enabled, or use an emulator.
5. Build and run the app.
6. Read the in-app Accessibility disclosure and explicitly accept before enabling the service in Android Settings.
7. Return to ScanBug, select an app, and tap **Open selected app**.
8. In the target app, tap **Explore screen** on the ScanBug overlay.
9. Review the grouped inventory. Tap **Share inventory** to intentionally share the plain-text report.
10. Tap **Stop** on the overlay or **Stop ScanBug session** in ScanBug to end the selected-app session. Disable the service in Android Settings to stop Accessibility access completely.
