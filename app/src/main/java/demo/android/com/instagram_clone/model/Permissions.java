package demo.android.com.instagram_clone.model;


import android.Manifest;

/**
 * Created by Admin on 04-06-2018.
 */

public class Permissions {
    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
}
