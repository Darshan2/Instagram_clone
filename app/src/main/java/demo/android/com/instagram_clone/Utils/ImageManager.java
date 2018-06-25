package demo.android.com.instagram_clone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Darshan B.S on 07-06-2018.
 */

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitMap(String imageURL) {
        File imageFile = new File(imageURL);
        FileInputStream fis = null;
        Bitmap bitmap = null;

        try {
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "getBitMap: FileNotFoundException " + e.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException | NullPointerException e) {
                Log.d(TAG, "getBitMap: IOException " + e.getMessage());
            }
        }
       return bitmap;
    }


    public static byte[] getByteFromBitmap(Bitmap bitmap, int pictureQuality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, pictureQuality, baos);
        byte[] data = baos.toByteArray();

        return data;
    }


}
