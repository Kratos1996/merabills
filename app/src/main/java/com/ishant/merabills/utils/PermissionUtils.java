package com.ishant.merabills.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    // Request code for permission requests
     public static final int PERMISSION_REQUEST_CODE = 1001;
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied(String[] deniedPermissions);
    }

    // Static callback (for simplicity)
    private static PermissionCallback permissionCallback;
    // Check if all permissions are granted
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context == null || permissions == null) return false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    // Request permissions
    public static void requestPermissions(Activity activity, String[] permissions, PermissionCallback callback) {
        if (activity == null || permissions == null) return;

        if (hasPermissions(activity, permissions)) {
            // All permissions are already granted.
            callback.onPermissionGranted();
        } else {
            // Store the callback and request the permissions.
            permissionCallback = callback;
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
        }
    }
    // Handle the result of the permission request
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && permissionCallback != null) {
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[i]);
                }
            }
            if (deniedList.isEmpty()) {
                permissionCallback.onPermissionGranted();
            } else {
                permissionCallback.onPermissionDenied(deniedList.toArray(new String[0]));
            }
            // Clear the callback to avoid memory leaks.
            permissionCallback = null;
        }
    }
}
