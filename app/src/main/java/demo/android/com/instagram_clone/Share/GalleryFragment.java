package demo.android.com.instagram_clone.Share;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import demo.android.com.instagram_clone.Profile.AccountSettingsActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.FilePaths;
import demo.android.com.instagram_clone.Utils.FileSearch;
import demo.android.com.instagram_clone.Utils.GridImageAdapter;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;


//We are concentrating on PICTURES, and CAMERA directories of Device
public class GalleryFragment extends Fragment implements GridImageAdapter.OnRecyclerItemClickListener {

    private static final String TAG = "GalleryFragment";

    //widgets
    private Spinner mGallerySpinner;
    private RecyclerView mGridRecyclerView;
    private ProgressBar mProgressBar;
    private ImageView mGalleryImage;

    private String selectedImageURL = "";

    private ArrayList<String> directories;
    private String mFileAppend = "file://";

    @Override
    public void recyclerViewClickedItemURL(String clickedImageURL, int index) {
        Log.d(TAG, "recyclerViewClickedItemURL: imageURL  " + clickedImageURL);
        selectedImageURL = clickedImageURL;
        //set up clicked image @param clickedImageURL in mGalleryImage
        UniversalImageLoader.setImage(clickedImageURL, mGalleryImage, mProgressBar, "");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: started");

        mGalleryImage = view.findViewById(R.id.galleryImageView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mGallerySpinner = view.findViewById(R.id.spinner);
        mGridRecyclerView = view.findViewById(R.id.recyclerImageGrid);

        directories = new ArrayList<>();

        mProgressBar.setVisibility(View.GONE);

        ImageView closeFragmentIV = view.findViewById(R.id.ivCloseShare);
        closeFragmentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to calling activity
                Log.d(TAG, "onClick: Navigating back to calling activity");
                getActivity().finish();
            }
        });


        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRootTask()) {
                    Log.d(TAG, "onClick: Navigating final share screen");
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    //pass url of selected image in grid to NextActivity
                    intent.putExtra(getString(R.string.image_url), selectedImageURL);
                    startActivity(intent);
                } else {
                    //call is for changing profile_photo from EditProfileFragment
                    Log.d(TAG, "onClick:Navigating to AccountSettingsActivity");
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.image_url), selectedImageURL);
                    intent.putExtra(getString(R.string.calling_fragment), getString(R.string.gallery_fragment));
                    startActivity(intent);
                    //to avoid back stack navigation from EditProfileFragment to this fragment
                    getActivity().finish();

                }
            }
        });

        initSpinner();

        return view;
    }


    private boolean isRootTask() {
        if(((ShareActivity)getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }


    private void initSpinner() {
        //get all the folders under PICTURE folder
        if (FileSearch.getDirectoriesPath(FilePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoriesPath(FilePaths.PICTURES);
        }
        //adding CAMERA folder(generally it does not contains any sub directories)
        directories.add(FilePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        //directories list item are of form ex: storage/emulated/0/Pictures, i need only Pictures
        for(String directoryPath: directories) {
            int index = directoryPath.lastIndexOf("/");
            String directoryName = directoryPath.substring(index+1);
            directoryNames.add(directoryName);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_dropdown_item, directoryNames);
        mGallerySpinner.setAdapter(arrayAdapter);

        mGallerySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected directory  " + directories.get(position));

                //setup image grid for the selected directory images
                setupImageGrid(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupImageGrid(String directory) {
        ArrayList<String> imagesPathsArrayList = FileSearch.getFilesPath(directory);

        //In UniversalImageLoader pictures from Phone memory are to formatted as file://filepath
        ArrayList<String> imageURL_ArrayList = new ArrayList<>();
        for (String imagesPath : imagesPathsArrayList) {
            imageURL_ArrayList.add(mFileAppend + imagesPath);
        }

        //setting up default image of mGalleryImage, to first image in spinner selected directory
        if(imageURL_ArrayList.size() > 0) {
            UniversalImageLoader.setImage(imageURL_ArrayList.get(0), mGalleryImage, null, "");
            selectedImageURL = imageURL_ArrayList.get(0);
        } else {
            //if selected folder does not contain any image of mGalleryImage to default
            UniversalImageLoader.setImage("", mGalleryImage, null, "");
            selectedImageURL = "";
        }

        Log.d(TAG, "setupImageGrid: setting grid");
        //setting up RecyclerView as GridView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mGridRecyclerView.setLayoutManager(gridLayoutManager);

        //setting up an Adapter
        GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(), imageURL_ArrayList, this);
        mGridRecyclerView.setAdapter(gridImageAdapter);

    }

}
