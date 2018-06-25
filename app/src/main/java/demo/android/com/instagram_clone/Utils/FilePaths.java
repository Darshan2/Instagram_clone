package demo.android.com.instagram_clone.Utils;

import android.os.Environment;

/**
 * Created by Admin on 05-06-2018.
 */

public class FilePaths {

    //"/storage/emulated/0"
    public static String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

    public static String PICTURES = ROOT_DIRECTORY + "/Pictures";
    public static String CAMERA = ROOT_DIRECTORY + "/DCIM/Camera";

    public static String FIREBASE_IMAGE_STORAGE = "photos/users";
}

