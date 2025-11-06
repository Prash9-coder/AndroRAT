# Android 15 Compatibility Update

## ✅ Changes Made

This project has been updated to be compatible with Android 15 (API 35). Below are all the changes made:

### 1. SDK Version Updates (`app/build.gradle`)
- **compileSdkVersion**: Updated from 29 → **35**
- **targetSdkVersion**: Updated from 22 → **35**
- **minSdkVersion**: Updated from 16 → **21** (minimum for modern Android features)
- **buildToolsVersion**: Updated from 29.0.2 → **35.0.0**
- **Added namespace**: Added explicit namespace declaration

### 2. Gradle Configuration Updates (`build.gradle`)
- **Android Gradle Plugin**: Updated from 8.5.0 → **8.7.3**
- **Gradle Version**: Already at 8.7 (compatible)
- **Repository**: Replaced deprecated `jcenter()` → **`mavenCentral()`**

### 3. Dependency Updates (`app/build.gradle`)
Updated all AndroidX and test dependencies to latest versions:
- androidx.appcompat: 1.0.2 → **1.7.0**
- androidx.constraintlayout: 1.1.3 → **2.2.0**
- commons-io: 2.0.1 → **2.17.0**
- JUnit: 4.12 → **4.13.2**
- AndroidX Test extensions: Updated to latest

### 4. Manifest Permission Updates (`AndroidManifest.xml`)

#### Added Foreground Service Permissions (Required for Android 9+, 14+)
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
```

#### Updated Storage Permissions (Android 13+ Granular Permissions)
```xml
<!-- Legacy permissions with maxSdkVersion -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29" />

<!-- New granular media permissions -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

#### Added New Required Permissions
```xml
<uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### 5. Component Export Declarations (Required for Android 12+)
All activities, services, and receivers now have explicit `android:exported` attributes:

#### Activities
- `MainActivity`: `android:exported="true"` (has intent-filter)
- `controlPanel`: `android:exported="false"`

#### Services
- `mainService`: `android:exported="false"` + foregroundServiceType
- `videoRecorder`: `android:exported="false"` + foregroundServiceType="camera"
- `audioManager`: `android:exported="false"` + foregroundServiceType="microphone"
- `jobScheduler`: `android:exported="false"`

#### Broadcast Receivers
- `broadcastReceiver`: `android:exported="true"` (receives system broadcasts)
- `keypadListener`: `android:exported="true"` (receives telephony events)

### 6. Runtime Permission Handling

#### Created New `PermissionHandler.java`
A comprehensive permission management class that:
- Handles all required permissions
- Distinguishes between Android versions
- Requests appropriate permissions based on API level
- Provides utility methods to check permission status

#### Updated `MainActivity.java`
- Integrated runtime permission requests
- Handles permission results
- Ensures app starts only after permissions are handled

### 7. Foreground Service Types
All services that access sensitive resources now declare their foreground service type:
- **mainService**: `location|camera|microphone`
- **videoRecorder**: `camera`
- **audioManager**: `microphone`

---

## ⚠️ Important Notes for Android 15

### 1. **Runtime Permissions are Mandatory**
Starting with Android 6.0 (API 23), but strictly enforced in newer versions:
- All dangerous permissions must be requested at runtime
- Users can deny permissions at any time
- The app must handle permission denials gracefully

### 2. **Foreground Service Restrictions (Android 14+)**
- Services accessing location, camera, or microphone MUST declare foregroundServiceType
- Must call `startForeground()` with a notification within 5 seconds
- User must grant permission for the service type

### 3. **Storage Access Changes (Android 13+)**
- `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` are deprecated
- Must use granular media permissions: READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO
- For file access, use Scoped Storage or MediaStore APIs

### 4. **Notification Permission (Android 13+)**
- Apps must request `POST_NOTIFICATIONS` permission
- Without this, notifications won't be displayed

### 5. **Exact Alarm Permission (Android 14+)**
If your app uses exact alarms, you may need:
```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### 6. **Background Location Access**
For background location access, you need:
```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```
And request it separately from foreground location permission.

---

## 🔧 Additional Code Changes Required

### Services Must Start as Foreground Services
Update your service classes to start as foreground services. Example for `mainService.java`:

```java
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class mainService extends Service {
    private static final String CHANNEL_ID = "MainServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Background service is active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
                
        startForeground(NOTIFICATION_ID, notification);
        
        // Your existing code here...
        
        return START_STICKY;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Main Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
```

Apply similar changes to `videoRecorder.java` and `audioManager.java`.

---

## 🧪 Testing Checklist

Before deploying to Android 15 devices:

- [ ] Test permission requests on first launch
- [ ] Test app behavior when permissions are denied
- [ ] Verify foreground services show notifications
- [ ] Test storage access (read/write media files)
- [ ] Test location access (foreground and background)
- [ ] Test camera and microphone access
- [ ] Test SMS and call log reading
- [ ] Verify boot receiver works
- [ ] Test notification display on Android 13+
- [ ] Check battery optimization restrictions

---

## 📱 Device Testing Recommendations

Test on multiple Android versions:
- ✅ Android 15 (API 35) - Latest
- ✅ Android 14 (API 34) - Foreground service types
- ✅ Android 13 (API 33) - Granular storage permissions
- ✅ Android 12 (API 31-32) - Exported components
- ✅ Android 11 (API 30) - Scoped storage
- ✅ Android 10 (API 29) - Storage changes

---

## 🚨 Known Issues & Limitations

### 1. **Special Permissions**
Some permissions require special handling:
- `SYSTEM_ALERT_WINDOW`: Must request via Settings intent
- `WRITE_SETTINGS`: Must request via Settings intent
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Requires Play Store approval

### 2. **Background Restrictions**
Android 15 heavily restricts background activities:
- Services can be killed by the system
- JobScheduler may be throttled
- WorkManager is recommended for background tasks

### 3. **Privacy Dashboard**
Users can see all permission usage in Privacy Dashboard:
- Be transparent about why permissions are needed
- Minimize permission requests
- Provide rationale before requesting permissions

---

## 🔐 Security Considerations

### Play Store Policy Compliance
- Dangerous permissions require justification in Play Store listing
- Some permissions may trigger manual review
- Background location requires disclosure
- SMS/Call log access has strict policies

### Best Practices
1. Request permissions only when needed (not all at once)
2. Explain why each permission is needed
3. Handle permission denials gracefully
4. Test with restricted permissions
5. Use the least privileged permission possible

---

## 📚 Additional Resources

- [Android 15 Behavior Changes](https://developer.android.com/about/versions/15/behavior-changes-all)
- [Foreground Services Guide](https://developer.android.com/develop/background-work/services/foreground-services)
- [App Permissions Best Practices](https://developer.android.com/training/permissions/requesting)
- [Storage Access Guide](https://developer.android.com/training/data-storage)

---

## ✨ Summary

Your app is now configured for Android 15 compatibility! However, you'll need to:

1. **Update service implementations** to properly start as foreground services with notifications
2. **Test thoroughly** on Android 15 devices
3. **Handle edge cases** where permissions are denied
4. **Consider using WorkManager** for better background task management
5. **Review Play Store policies** before publishing

The configuration changes are complete, but runtime behavior and service implementations may need additional refinement based on your specific use case.