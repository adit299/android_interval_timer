## Android Interval Timer: Privacy policy

Welcome to the Android Interval Timer app for Android!

This is an open source Android app developed by Adit Krishnan and Adam Rust. The source code is available on GitHub under the MIT License.


### Data collected by the app

We hereby state, to the best of our knowledge and belief, that we have not programmed this app to collect any personally identifiable information. All data (app preferences (like theme) and alarms) created by the you (the user) is stored locally in your device only, and can be simply erased by clearing the app's data or uninstalling it. No analytics software is present in the app either.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

https://github.com/adit299/android_interval_timer/blob/main/app/src/main/AndroidManifest.xml#L4
<br/>

| Permission                              | Why it is required |
| --------------------------------------- | ------------------ |
| `android.permission.POST_NOTIFICATIONS` | Required by the app to post notifications. Has to be granted by the user manually; can be revoked by the system or the user at any time. It is highly recommended that you allow this permission so that the app can post notifications when an interval has finished |

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by us, or have any question regarding how the app protects your privacy, please send us an email or post a discussion on GitHub, and we will surely try to fix it/help you.

Yours sincerely,  
Adit Krishnan and Adam Rust.  
