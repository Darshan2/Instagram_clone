package demo.android.com.instagram_clone.Share;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import demo.android.com.instagram_clone.Profile.AccountSettingsActivity;
import demo.android.com.instagram_clone.R;

//I want to reuse this fragment in many different Activities
public class PhotoFragment extends Fragment {

    private static final String TAG = "PhotoFragment";

    //consts
    private static final int PHOTO_FRAGMENT_NUMBER = 1;
    private static final int GALLERY_FRAGMENT_NUMBER = 2;
    private static final int CAMERA_REQUEST_CODE = 5;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started");

        Button launchCameraBrn = view.findViewById(R.id.launch_cameraBtn);
        launchCameraBrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera");
                if(((ShareActivity)getActivity()).getCurrentTabNum() == PHOTO_FRAGMENT_NUMBER) {
                    //double checking whether the app has Camera permission
                    if (((ShareActivity) getActivity()).checkPermission(Manifest.permission.CAMERA)) {
                        Log.d(TAG, "onClick: starting camera");
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    } else {
                        //user had denied camera permission to app, Go to ShareActivity, where we handle it
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            }
        });

        return view;
    }


    private boolean isRootTask() {
        if(((ShareActivity)getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
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

        if(isRootTask()) {
            //navigate to final share screen
            Intent intent = new Intent(getActivity(), NextActivity.class);
            intent.putExtra(getString(R.string.image_bitmap), bm);
            startActivity(intent);


        } else {
            //call is for changing profile_photo, from EditProfileFragment
            Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
            intent.putExtra(getString(R.string.calling_fragment), getString(R.string.photo_fragment));
            intent.putExtra(getString(R.string.image_bitmap) , bm);
            startActivity(intent);
            if(getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}
