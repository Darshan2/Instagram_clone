package demo.android.com.instagram_clone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.Profile.AccountSettingsActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;
import demo.android.com.instagram_clone.model.UserAndSettings;

/**
 * Created by Admin on 30-05-2018.
 * Handle FireBase database and storage related stuff
 */

public class DataBaseHandler {
    private static final String TAG = "DataBaseHandler";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private String userId;

    //vars
    private Context mContext;
    private ProgressBar mProgressBar;
    private double mPreviousImageProgress = 0;

    public DataBaseHandler(Context context, @Nullable ProgressBar progressBar) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if(mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }
        if(progressBar != null) {
            mProgressBar = progressBar;
        }
    }


    /**
     * Count number of photos(reference to photo entries) under photos node of DataBase
     * Structure : photos - reference to photos
     * @param dataSnapshot
     * @return
     */
    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for(DataSnapshot ds : dataSnapshot
                    .child(mContext.getString(R.string.photos_node))
                    .getChildren()) {
            count ++ ;
        }
        return count;
    }


    /**
     * Inside FireBase storage i want directory structure to be photos/users/userId/...
     * @param photoType
     * @param caption
     * @param imageCount
     * @param imageURL is null if uploading picture is a photo taken by camera
     * @param imageBitmap is null if uploading picture is not a photo taken by camera
     */
    public void uploadNewPhoto(
            String photoType, final String caption, int imageCount, String imageURL, Bitmap imageBitmap) {
        if(photoType.equals(mContext.getString(R.string.new_photo))) {
            //trying to upload new photo to storage
            Log.d(TAG, "uploadNewPhoto: trying to upload new photo to fire-base storage");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference =  mStorageReference
                                                    .child(FilePaths.FIREBASE_IMAGE_STORAGE + "/")
                                                    .child(user_id + "/photo" + (imageCount+1));

            //convert image url to bitmap
            if(imageBitmap == null) {
                imageBitmap = ImageManager.getBitMap(imageURL);
            }
            byte[] bytes = ImageManager.getByteFromBitmap(imageBitmap, 100);

            UploadTask uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri fireBaseUploadedImageUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Photo upload success", Toast.LENGTH_SHORT).show();

                    //insert uploaded photo info under 'photos' and 'user_photos' nodes of database
                    addPhotoToDatabase(caption, fireBaseUploadedImageUri.toString());

                    //navigating to the main feed so that user can see their uploaded photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                //This listener is not mandatory, it is used here to display progress of image
                // upload to user
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onProgress: stared");
                   //Below lines helps avoiding multiple toast pop-ups, in case of multiple image upload scenario
                    double currentImageProgress =
                            (100 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));

                    if(currentImageProgress - 15 > mPreviousImageProgress) {
                        Log.d(TAG, "onProgress: " + currentImageProgress);
                        //The number of time the state_changed event is raised,
                        // depends on the size of the file you upload: it fires for each block of 256KB.
                        // so if size < 256kb only toast i get is at 0%
                        Toast.makeText(mContext,
                                "Photo upload progress:" + String.format("%.0f", currentImageProgress) + "%",
                                Toast.LENGTH_SHORT).show();
                        mPreviousImageProgress = currentImageProgress;
                    }
                }
            });

        } else if(photoType.equals(mContext.getString(R.string.profile_photo))) {
            //trying to update already existing profile photo
            Log.d(TAG, "uploadNewPhoto: trying to upload new profile_photo");

            //There can be only one profile photo
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference =  mStorageReference
                    .child(FilePaths.FIREBASE_IMAGE_STORAGE + "/")
                    .child(user_id + "/profile_photo");

            //convert image url to bitmap
            if(imageBitmap == null) {
                imageBitmap = ImageManager.getBitMap(imageURL);
            }
            byte[] bytes = ImageManager.getByteFromBitmap(imageBitmap, 100);

            UploadTask uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri fireBaseUploadedImageUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Profile photo upload success", Toast.LENGTH_SHORT).show();
                    //insert uploaded photo info under 'user_account_settings'->'profile_photo' field
                    updateDataBaseAccountSettingsField(
                            mContext.getString(R.string.profile_photo_field),
                            fireBaseUploadedImageUri.toString(),
                            false);

                    //on task completion,
                    //directly navigate to EditProfileFragment(through AccountSettingsActivity)
                    ((AccountSettingsActivity)mContext).setupViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Profile photo upload failed", Toast.LENGTH_SHORT).show();
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                //This listener is not mandatory, it is used here to display progress of image
                // upload to user
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onProgress: stared");
                    //Below lines helps avoiding multiple toast pop-ups, in case of multiple image upload scenario
                    double currentImageProgress =
                            (100 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));

                    if(currentImageProgress - 15 > mPreviousImageProgress) {
                        Log.d(TAG, "onProgress: " + currentImageProgress);
                        //The number of time the state_changed event is raised,
                        // depends on the size of the file you upload: it fires for each block of 256KB.
                        // so if size < 256kb only toast i get is at 0%
                        Toast.makeText(mContext,
                                "Photo upload progress:" + String.format("%.0f", currentImageProgress) + "%",
                                Toast.LENGTH_SHORT).show();
                        mPreviousImageProgress = currentImageProgress;
                    }
                }
            });
        }

    }


    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database");

        String newPhotoKey = myRef.child(mContext.getString(R.string.photos_node)).push().getKey();
        String tags = StringManipulation.getTags(caption);

        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setTags(tags);
        photo.setDate_created(MyDateHandler.getCurrentDate());
        photo.setImage_path(url);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);
        HashMap<String, LikedUser> likedUserHashMap = new HashMap<>();
        //likedUserHashMap.put("default", new LikedUser("0"));
        photo.setLikes(likedUserHashMap);

        //insert uploaded photo info under 'photos' and 'user_photos' nodes of database
        myRef.child(mContext.getString(R.string.user_photos_node))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey)
                .setValue(photo);

        myRef.child(mContext.getString(R.string.photos_node))
                .child(newPhotoKey)
                .setValue(photo);
        Log.d(TAG, "addPhotoToDatabase: photos");

    }


    private String getTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                ("dd-MM-yyyy 'at' hh:mm:ss a zzz", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return simpleDateFormat.format(new Date());

    }


    // -------------------------- fire-base DataBase ------------------------------------------ //

    /**
     * update user_name in 'users' and 'user_account_settings' node with
     * @param user_name
     */
    public void updateUserNameInDataBase(String user_name) {
        myRef.child(mContext.getString(R.string.user_account_settings_node))
                .child(userId)
                .child(mContext.getString(R.string.user_name_field))
                .setValue(user_name);

        myRef.child(mContext.getString(R.string.users_node))
                .child(userId)
                .child(mContext.getString(R.string.user_name_field))
                .setValue(user_name);
    }


    /**
     * Update database users' field
     * @param fieldName
     * with value
     * @param updateValue
     */
    public void updateDataBaseUsersField
                            (@NonNull String fieldName, @NonNull String updateValue, boolean isStringNum) {
        Object passValue;
        if(isStringNum) {
            //i.e passed value is number, numbers must be stored as Long in fire-base database
            passValue = new Long(updateValue);
        } else {
            //i.e passed value is String
            passValue = updateValue;
        }
        myRef.child(mContext.getString(R.string.users_node))
                .child(userId)
                .child(fieldName)
                .setValue(passValue);

        Log.d(TAG, "updateDataBaseAccountSettingsField:"+ fieldName + " success");
        Toast.makeText(mContext, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }


    /**
     * Update database user_account_settings' field
     * @param fieldName
     * with value
     * @param updateValue
     */
    public void updateDataBaseAccountSettingsField
            (@NonNull String fieldName, @NonNull String updateValue, boolean isStringNum) {
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        Object passValue;
        if(isStringNum) {
            //i.e passed value is number, numbers must be stored as Long in fire-base database
            passValue = Long.valueOf(updateValue);
        } else {
            //i.e passed value is String
            passValue = updateValue;
        }

        myRef.child(mContext.getString(R.string.user_account_settings_node))
                .child(userId)
                .child(fieldName)
                .setValue(passValue);

        Log.d(TAG, "updateDataBaseAccountSettingsField:"+ fieldName + " success");
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        Toast.makeText(mContext, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }


    /**
     * When user first signed up, add nodes under users, user_account_settings nodes
     * and set them to default values of
     * @param email
     * @param phone_number
     * @param user_id
     * @param userName
     */
    public void writeFireBase_UsersNode (String email, long phone_number, String user_id, String userName) {
        String user_name = StringManipulation.condenseUserName(userName);

        Log.d(TAG, "writeFireBase_UsersNode: user_id " + user_id + ", userName " + userName);

        //Add new node with name = userId under users node, and add the user info below this new node
        User user = new User(email, phone_number, user_id, user_name);
        DatabaseReference usersNode = myRef.child(mContext.getString(R.string.users_node)).child(user_id);
        usersNode.setValue(user);

        //Add new node with name = userId, under user_account_settings node and add the user info below this new node
        UserAccountSettings userAccountSettings =
                new UserAccountSettings("", "",
                        "none", user_name, "",null, null, 0);
        DatabaseReference accountSettingsNode =  myRef
                .child(mContext.getString(R.string.user_account_settings_node))
                .child(user_id);
        accountSettingsNode.setValue(userAccountSettings);

        Log.d(TAG, "writeFireBase_UsersNode: success");

    }


    /**
     * Must be used from inside onDataChange
     * DataBase has 2 nodes 1.users 2.user_account_settings
     * query users node to get User object, corresponding to currentUser in DataBase
     * query user_account_settings node to get UserAccountSettings object, corresponding to currentUser in DataBase
     */
    public UserAndSettings getUserDataFromFireBase(DataSnapshot dataSnapshot, @Nullable String userId) {
        User user = null;
        UserAccountSettings userAccountSettings = null;
        UserAndSettings userAndSettings;

        try {
            user = dataSnapshot
                    .child(mContext.getString(R.string.users_node))
                    .child(userId)
                    .getValue(User.class);

            userAccountSettings = dataSnapshot
                    .child(mContext.getString(R.string.user_account_settings_node))
                    .child(userId)
                    .getValue(UserAccountSettings.class);

        }catch (Exception e) {
            Log.d(TAG, "getUserDataFromFireBase: Exception " + e.getMessage());
        }

        userAndSettings = new UserAndSettings(user, userAccountSettings);

        return userAndSettings;
    }

    /**
     * likes is a list of LikedUser objects, modelled as hash maps with key = "random"
     * Update likes field under 'photos',
     */
    public void updateDataBase_PhotosLikes(String photo_id, String userId) {
        String newNodeKey = myRef.child(mContext.getString(R.string.photos_node))
                .child(photo_id).push().getKey();
        LikedUser likedUser = new LikedUser(userId);

        myRef.child(mContext.getString(R.string.photos_node))
                .child(photo_id)
                .child(mContext.getString(R.string.likes_field))
                .child(newNodeKey)
                .setValue(likedUser);
    }

    /**
     * Update likes field under 'user_photos' -> user_id -> likes,
     */
    public void updateDataBase_UserPhotosLikes(String photo_id, String userId) {
        String newNodeKey = myRef.child(mContext.getString(R.string.photos_node))
                .child(photo_id).push().getKey();
        LikedUser likedUser = new LikedUser(userId);

        myRef.child(mContext.getString(R.string.user_photos_node))
                .child(userId)
                .child(photo_id)
                .child(mContext.getString(R.string.likes_field))
                .child(newNodeKey)
                .setValue(likedUser);

    }

}
