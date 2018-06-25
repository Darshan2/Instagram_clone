package demo.android.com.instagram_clone.Profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.GridImageAdapter;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;
import demo.android.com.instagram_clone.model.UserAndSettings;


public class ProfileFragment extends Fragment implements GridImageAdapter.OnRecyclerItemClickListener {
    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 3;

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private String mCurrentUserId;

    //widgets
    private TextView mPosts, mFollowers, mFollowing, mUserName;
    private TextView mEditProfileLink, mFollow, mUnFollow;
    private CircleImageView mCircleProfilePhoto;
    private TextView mDisplayName, mDescription, mWebsite;
    private RecyclerView mRecyclerImageGrid;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    private Toolbar mToolbar;
    private ImageView mProfileMenu;
    private ProgressBar mProgressBar;

    //vars
    private Context mContext;
    private String gridSelectedImageUrl;
    private ArrayList<Photo> allUserSharedPhotos;
    private OnGridImageSelectedListener onGridImageSelectedListener;
    private String userID;
    private String mFollowKey;



    // -------------------------- Interface link-ups --------------------------------------//
    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNum);
    }


    @Override
    public void recyclerViewClickedItemURL(String imageURL, int index) {
        Photo clickedPhoto = allUserSharedPhotos.get(index);
        //Pass this photo to calling activity
        onGridImageSelectedListener.onGridImageSelected(clickedPhoto, ACTIVITY_NUM);
    }
    // ----------------------------------------------------------------------------------------//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mContext = getActivity();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        dataBaseHandler = new DataBaseHandler(mContext, null);

        setupFireBaseAuth();
        initWidgets(view);

        Bundle argBundle = getArguments();
        int activityNum;
        if(argBundle != null && argBundle.containsKey(getString(R.string.userId_intent))) {
            //called from Other activities than ProfileActivity
            Log.d(TAG, "onCreateView: called from SearchActivity or HomeActivity");
            String user_Id = argBundle.getString(getString(R.string.userId_intent));
            int callingActivityNum = argBundle.getInt(getString(R.string.activity_num));
            userID = user_Id;
            activityNum = callingActivityNum;

            mEditProfileLink.setVisibility(View.GONE);
            mProfileMenu.setVisibility(View.GONE);
            handleDatabase(user_Id);

            Log.d(TAG, "onCreateView: from Home userId " + user_Id);

            if(userID.equals(mCurrentUserId)) {
                mEditProfileLink.setVisibility(View.VISIBLE);
            } else {
                handleFollowToggle();
            }

        } else {
            //normal flow
            activityNum = ACTIVITY_NUM;
            userID = mCurrentUserId;
            setupToolBar();
        }

        setupBottomNavigationView(activityNum);
        setupImageGrid();

        mEditProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to AccountSettingsActivity, from there directly go to EditProfile fragment
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });

        return view;
    }


    private void handleFollowToggle() {
        Log.d(TAG, "handleFollowToggle: ");

        isCurrentUserFollowsSearchedUser();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: follow");

                //add Current user id to follower field of Searched user
                //and add Searched user id to following field of Current user,
                mFollow.setVisibility(View.GONE);
                mUnFollow.setVisibility(View.VISIBLE);

                String newKey = myRef.child(getString(R.string.user_account_settings_node))
                        .push().getKey();
                mFollowKey = newKey;

                //Add Searched user id to following field of Current user
                myRef.child(getString(R.string.user_account_settings_node))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.following_field))
                        .child(newKey)
                        .setValue(new LikedUser(userID)); //userID here is id of searched user

                //Add Current user id to followers field of Searched user
                myRef.child(getString(R.string.user_account_settings_node))
                        .child(userID)
                        .child(getString(R.string.followers_field))
                        .child(newKey)
                        .setValue(new LikedUser(FirebaseAuth.getInstance().getCurrentUser().getUid()));


            }
        });

        mUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: unFollow");

                mFollow.setVisibility(View.VISIBLE);
                mUnFollow.setVisibility(View.GONE);

                Log.d(TAG, "onClick: followKey " + mFollowKey);

                //Remove Searched user id from following field of Current user
                myRef.child(getString(R.string.user_account_settings_node))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.following_field))
                        .child(mFollowKey)
                        .removeValue();

                //Remove Current user id from followers field of Searched user
                myRef.child(getString(R.string.user_account_settings_node))
                        .child(userID)
                        .child(getString(R.string.followers_field))
                        .child(mFollowKey)
                        .removeValue();

            }
        });
    }


    private void isCurrentUserFollowsSearchedUser() {
        Log.d(TAG, "isCurrentUserFollowsSearchedUser: ");
        Query query = myRef.child(getString(R.string.user_account_settings_node))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.following_field))
                .orderByChild(getString(R.string.user_id_field))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                mProgressBar.setVisibility(View.GONE);

                if(dataSnapshot.getValue() == null) {
                    //Current user not follows the Searched user
                    Log.d(TAG, "onDataChange: Current user not follows Searched user");
                    mFollow.setVisibility(View.VISIBLE);
                    mUnFollow.setVisibility(View.GONE);
                } else {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        mFollowKey = snapshot.getKey();
                        Log.d(TAG, "onDataChange: " +mFollowKey);
                    }
                    Log.d(TAG, "onDataChange: Current user follows Searched user");
                    mFollow.setVisibility(View.GONE);
                    mUnFollow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initWidgets(View view) {
        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);

        mEditProfileLink = view.findViewById(R.id.textEditProfile);
        mFollow = view.findViewById(R.id.follow);
        mUnFollow = view.findViewById(R.id.unFollow);

        mCircleProfilePhoto = view.findViewById(R.id.profile_photo);
        mDisplayName = view.findViewById(R.id.display_name);
        mDescription = view.findViewById(R.id.description);
        mWebsite = view.findViewById(R.id.website);

        mRecyclerImageGrid = view.findViewById(R.id.recyclerImageGrid);
        mBottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);
        mToolbar = view.findViewById(R.id.profileToolBar);
        mProfileMenu = view.findViewById(R.id.profileMenu);
        mUserName = view.findViewById(R.id.userName);
        mProgressBar = view.findViewById(R.id.progressBar);
    }


    private void setupImageGrid() {
        //Get all the shared photos from databases under user_photos -> user_id -> .....
        allUserSharedPhotos = new ArrayList<>();
        Query query = myRef
                .child(getString(R.string.user_photos_node))
                .child(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Photo photo = ds.getValue(Photo.class);
                    allUserSharedPhotos.add(photo);
                }

                //setting up our ImageGrid
                ArrayList<String> photosURL = new ArrayList<>();
                for (Photo photo: allUserSharedPhotos) {
                    photosURL.add(photo.getImage_path());
                }

                //setup RecyclerView as GridView with 3 columns
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
                mRecyclerImageGrid.setLayoutManager(gridLayoutManager);

                GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(), photosURL, ProfileFragment.this);
                mRecyclerImageGrid.setAdapter(gridImageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: shared images retrieve from database got cancelled");
            }
        });
    }


    private void setupToolBar() {
        // Sets the Toolbar to act as the ActionBar for this fragment,
        //Activity must contain Coordinator layout to do that
        ((ProfileActivity)getActivity()).setSupportActionBar(mToolbar);

        mProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setupBottomNavigationView(int activityNum) {
        BottomNavigationViewHelper.setupBottomNavigationView(mBottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, mBottomNavigationViewEx);

        //To highlight the menu corresponding to the activity
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(activityNum);
        menuItem.setChecked(true);
    }


    private void setupFragmentWidgets(UserAndSettings userAndSettings) {
        mProgressBar.setVisibility(View.GONE);

        User user = userAndSettings.getUser();
        UserAccountSettings accountSettings = userAndSettings.getUserAccountSettings();
        Log.d(TAG, "setupFragmentWidgets: accountSettings " + accountSettings);

        //used if condition to avoid app crash, when this fragment get inflated by HomeActivity
        if (getContext() != null) {
            initPosts();
        }

        if(accountSettings.getFollowers() == null) {
            mFollowers.setText("0");
        } else {
            mFollowers.setText(String.valueOf(accountSettings.getFollowers().size()));
        }

        if(accountSettings.getFollowing() == null) {
            mFollowing.setText("0");
        } else {
            Log.d(TAG, "setupFragmentWidgets: following " + accountSettings.getFollowing().size());
            mFollowing.setText(String.valueOf(accountSettings.getFollowing().size()));
        }

        mDisplayName.setText(accountSettings.getDisplay_name());
        mDescription.setText(accountSettings.getDescription());
        mWebsite.setText(accountSettings.getWebsite());
        mUserName.setText(user.getUser_name());

        String imageUrl = accountSettings.getProfile_photo();
        UniversalImageLoader.setImage(imageUrl, mCircleProfilePhoto, null, "");

    }


    private void initPosts() {
        //no of posts = number of Photos user posted
        Query query = myRef.child(getString(R.string.user_photos_node))
                .child(userID);
        
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    //no posts yet
                    Log.d(TAG, "onDataChange: no posts yet");
                    mPosts.setText("0");
                    
                } else {
                    long numPosts = dataSnapshot.getChildrenCount();
                    Log.d(TAG, "onDataChange: numPosts " + numPosts);
                    mPosts.setText(String.valueOf(numPosts));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //--------------------fire base setup -----------------------------//
    private void setupFireBaseAuth() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBase: user is signed in");
            //get user related data from FireBase
            mCurrentUserId = currentUser.getUid();
            handleDatabase(currentUser.getUid());

        } else {
            Toast.makeText(mContext, "User is signed out", Toast.LENGTH_SHORT).show();
        }
   }


   private void handleDatabase(final String userId) {
        //Triggers whenever database content get changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Read new User data from Database
                UserAndSettings userAndSettings = dataBaseHandler.getUserDataFromFireBase(dataSnapshot , userId);
                Log.d(TAG, "onDataChange: data received from fire base" +
                        userAndSettings.getUser()+userAndSettings.getUserAccountSettings());

                setupFragmentWidgets(userAndSettings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Communication with DataBase encounters a problem");
            }
        });
   }

   //------------------------ life cycle method --------------------------------------//


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onGridImageSelectedListener = (OnGridImageSelectedListener)getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ProfileActivity should implements OnGridImageSelectedListener");
        }
    }


}
