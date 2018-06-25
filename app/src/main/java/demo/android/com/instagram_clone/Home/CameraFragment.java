package demo.android.com.instagram_clone.Home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import demo.android.com.instagram_clone.Profile.AccountSettingsActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Share.NextActivity;
import demo.android.com.instagram_clone.model.Permissions;

/**
 * Created by Admin on 25-05-2018.
 */

public class CameraFragment extends Fragment {

    private static final String TAG = "PhotoFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 100;
    private static final int CAMERA_REQUEST_CODE = 50;

    private Button mLaunchCamera;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);

        mLaunchCamera = view.findViewById(R.id.launch_cameraBtn);

        if(checkPermissionsArray(Permissions.PERMISSIONS)) {
            //all requested permissions are granted by user
            Log.d(TAG, "onCreateView: permission granted");
            //startCamera();
        } else {
            //at-least one permission is not granted by user
            verifyPermissions(Permissions.PERMISSIONS);
        }

        mLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionsArray(Permissions.PERMISSIONS)) {
                    //all requested permissions are granted by user
                    Log.d(TAG, "onCreateView: permission granted");
                    startCamera();
                } else {
                    //at-least one permission is not granted by user
                    verifyPermissions(Permissions.PERMISSIONS);
                }
            }
        });

        return view;

    }


    private void startCamera() {
        Log.d(TAG, "onClick: starting camera");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }


    private boolean checkPermissionsArray(String[] permissions) {
        for(int i = 0; i <  permissions.length; i++) {
            String permission = permissions[i];
            if(!checkPermission(permission)) {
                return false;
            }
        }
        return true;
    }


    private boolean checkPermission(String permission) {
            Log.d(TAG, "checkPermission: checking permission: " + permission);

            int permissionGranted = ActivityCompat.checkSelfPermission(getActivity(), permission);

            if(permissionGranted != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermission: permission not granted for: " + permission);
                return false;
            } else {
                Log.d(TAG, "checkPermission: permission granted for: " + permission);
                return true;
            }
        }


    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: permissions" );
        //Requesting user to allow access, to all the requested permissions, by pooping up request
        ActivityCompat.requestPermissions(getActivity(), permissions, VERIFY_PERMISSIONS_REQUEST );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: Done taking photo");
            Log.d(TAG, "onActivityResult: Attempting navigation back to caliing activity");
        }

        Bitmap bm = null;
        //hard code("data" is a key, must be same all the time)
        if(data != null ) {
            bm = (Bitmap) data.getExtras().get("data");
        }
        Log.d(TAG, "onActivityResult: camera photo bitmap " + bm);

        //navigate to final share screen
        Intent intent = new Intent(getActivity(), NextActivity.class);
        intent.putExtra(getString(R.string.image_bitmap), bm);
        startActivity(intent);

    }

}
