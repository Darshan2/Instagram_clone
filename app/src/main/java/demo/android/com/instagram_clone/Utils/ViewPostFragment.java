package demo.android.com.instagram_clone.Utils;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.utils.L;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private String userId;

    //vars
    private Photo receivedPhoto;
    private int CALLING_ACTIVITY_NUM = 0;
    private Heart mHeart;
    private String mLikeString = "";
    private OnCommentThreadSelectedListener onCommentThreadSelectedListener;

    //widgets
    private ImageView mBackArrow, mProfileMenus;
    private ImageView mPostedImage, mCommentBubble;
    private ImageView mWhiteHeart, mRedHeart;
    private TextView mPhotoLabel, mUserName ;
    private TextView mImageLikes, mImageComments, mImageCaption, mImagePostedDate;
    private CircleImageView mProfilePhoto;
    private BottomNavigationViewEx bottomNavigationViewEx;


    public interface OnCommentThreadSelectedListener {
        void onCommentThreadSelected(Photo photo);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        //Get arguments passed by calling activity
        getReceivedArgumentValues();

        myRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dataBaseHandler = new DataBaseHandler(getActivity(), null);

        mBackArrow = view.findViewById(R.id.backArrow);
        mPhotoLabel = view.findViewById(R.id.tvPhotolabel);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mUserName = view.findViewById(R.id.user_name);
        mPhotoLabel = view.findViewById(R.id.tvPhotolabel);
        mProfileMenus = view.findViewById(R.id.profileMenu);
        mPostedImage = view.findViewById(R.id.postImage);
        mWhiteHeart = view.findViewById(R.id.likeHeart_white);
        mRedHeart = view.findViewById(R.id.likeHeart_red);
        mCommentBubble = view.findViewById(R.id.commentBubble);
        mImageLikes = view.findViewById(R.id.image_likes);
        mImageCaption = view.findViewById(R.id.image_caption);
        mImageComments = view.findViewById(R.id.image_comments_link);
        mImagePostedDate = view.findViewById(R.id.image_postedTime);
        bottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);

        mWhiteHeart.setVisibility(View.VISIBLE);
        mRedHeart.setVisibility(View.GONE);
        mHeart = new Heart(mWhiteHeart, mRedHeart);


        setupFireBaseAuth();
        currentUserLikesPhoto();
        setupHeartToggle();
        setupFragmentWidgets();
        setupBottomNavigationView();

        return view;
    }


    private void getReceivedArgumentValues() {
        try {
            Bundle argBundle = getArguments();
            receivedPhoto = argBundle.getParcelable(getString(R.string.photo));
            CALLING_ACTIVITY_NUM = argBundle.getInt(getString(R.string.activity_num));
            Log.d(TAG, "getReceivedArgumentValues: received photo " + receivedPhoto);
        } catch (NullPointerException e) {
            Log.d(TAG, "getReceivedArgumentValues: "+ e.getMessage());
        }
    }


    private void setupFragmentWidgets() {
        UniversalImageLoader.setImage(receivedPhoto.getImage_path(), mPostedImage, null, "");
        String timeDiff = MyDateHandler.getTimeDiffernce(receivedPhoto.getDate_created());
        if(timeDiff.equals("0")) {
            mImagePostedDate.setText("Posted: TODAY");
        } else if(timeDiff.equals("1")) {
            mImagePostedDate.setText("Posted: YESTERDAY");
        } else {
            mImagePostedDate.setText("Posted: " + timeDiff + " DAYS AGO");
        }

        mImageCaption.setText(receivedPhoto.getCaption());

        String commentString = "";
        if(receivedPhoto.getComments() == null) {
            commentString = "No comments";
        } else {
            commentString = "View all comments";
        }
        mImageComments.setText(commentString);

        mImageComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to ViewCommentFragment via calling activity
                //Remove this fragment from backStack, so that i
                // can not come back from ViewCommentFragment
                onCommentThreadSelectedListener.onCommentThreadSelected(receivedPhoto);
            }
        });


        setProfilePhoto_UserName();
        getAllPhotoLikedUsers();

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to Calling activity
                Log.d(TAG, "onClick: go back to calling activity");
                try {
                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: " + e.getMessage());
                }
            }
        });

        mCommentBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Comment pressed");
                //go to ViewCommentFragment via calling activity
                //Remove this fragment from backStack, so that i
                // can not come back from ViewCommentFragment
                onCommentThreadSelectedListener.onCommentThreadSelected(receivedPhoto);
            }
        });


    }

    /**
     * Check to see if selected photo is liked by, current user
     * Query under photos -> photo_id -> likes
     */
    private void currentUserLikesPhoto() {
        Query queryLikes = myRef.child(getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(getString(R.string.likes_field))
                .orderByChild(getString(R.string.user_id_field))
                .equalTo(userId);
        queryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if dataSnapshot value exists, user liked selected photo
                if(dataSnapshot.getValue() != null) {
                    Log.d(TAG, "onDataChange: user liked this photo" + dataSnapshot);
                    mRedHeart.setVisibility(View.VISIBLE);
                    mWhiteHeart.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "onDataChange: user not liked this photo" + dataSnapshot);
                    mRedHeart.setVisibility(View.GONE);
                    mWhiteHeart.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: 1");
            }
        });
    }

    /**
     * Query under photos -> photo_id -> likes, to get user_ids of all the users who liked this photo
     * using this user_id, query 'users' node to get corresponding user name
     */
    private void getAllPhotoLikedUsers() {
        Query query = myRef.child(getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(getString(R.string.likes_field));
        //query to get user_ids of all liked users
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    Log.d(TAG, "onDataChange: no user liked this photo");
                    mLikeString = "No likes yet!";
                    mImageLikes.setText(mLikeString);
                }

                final StringBuilder mLikedUsers = new StringBuilder();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: all liked users ids ds " + ds);
                    final LikedUser likedUser = ds.getValue(LikedUser.class);
                    Log.d(TAG, "onDataChange: likedUser " + likedUser);

                    //here we have user_id of individual photo liked user,
                    //query 'users' to get user_name of each liked users, using user_id
                    Query query = myRef.child(getString(R.string.users_node))
                            .child(likedUser.getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            mLikedUsers.append(user.getUser_name() + ",");
                            Log.d(TAG, "onDataChange: liked user string = " + mLikedUsers);

                            String[] splitUsers = mLikedUsers.toString().split(",");
                            int length = splitUsers.length;
                            if(length == 1) {
                                mLikeString = "Liked by " + splitUsers[0];
                            } else if(length == 2) {
                                mLikeString = "Liked by " + splitUsers[0] +" and " +splitUsers[1];
                            } else if(length == 3) {
                                mLikeString = "Liked by " + splitUsers[0] +", "
                                        + splitUsers[1] + " and "
                                        + splitUsers[2];
                            } else if(length > 3) {
                                mLikeString = "Liked by " + splitUsers[0] +", "
                                        + splitUsers[1] + ", "
                                        + splitUsers[2] + " and "
                                        + (length - 3) +" others";
                            }

                            mImageLikes.setText(mLikeString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: 2");
                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: 3");
            }
        });

    }


    private void setupHeartToggle() {
        mWhiteHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeart.toggleLike();

                String newNodeKey = myRef.child(getString(R.string.photos_node)).push().getKey();
                LikedUser likedUser = new LikedUser(userId);

                //user likes this photo, update photos like field
                myRef.child(getString(R.string.photos_node))
                        .child(receivedPhoto.getPhoto_id())
                        .child(getString(R.string.likes_field))
                        .child(newNodeKey)
                        .setValue(likedUser);

                //user likes this photo, update user_photos like field
                myRef.child(getString(R.string.user_photos_node))
                        .child(receivedPhoto.getUser_id())
                        .child(receivedPhoto.getPhoto_id())
                        .child(getString(R.string.likes_field))
                        .child(newNodeKey)
                        .setValue(likedUser);
                
                getAllPhotoLikedUsers();
            }
        });


        mRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeart.toggleLike();
                //user unlikes this photo. Remove current user_id, from photos likes field.
                //I cannot remove this user_id from user_photos like field, because of different
                //generated list keys
                Query query = myRef.child(getString(R.string.photos_node))
                            .child(receivedPhoto.getPhoto_id())
                            .child(getString(R.string.likes_field))
                            .orderByChild(getString(R.string.user_id_field))
                            .equalTo(userId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: un-liked, remove user_id from list " + dataSnapshot);
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String listKey = ds.getKey();
                            //Remove entry with this key under photos likes field, to unlike the photo
                            myRef.child(getString(R.string.photos_node))
                                    .child(receivedPhoto.getPhoto_id())
                                    .child(getString(R.string.likes_field))
                                    .child(listKey)
                                    .removeValue();

                            //Remove entry with this key under user_photos likes field, to unlike the photo
                            myRef.child(getString(R.string.user_photos_node))
                                    .child(receivedPhoto.getUser_id())
                                    .child(receivedPhoto.getPhoto_id())
                                    .child(getString(R.string.likes_field))
                                    .child(listKey)
                                    .removeValue();

                            getAllPhotoLikedUsers();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: 4");
                    }
                });

            }
        });
    }

    

    private void setProfilePhoto_UserName() {
        Query query = myRef.child(getString(R.string.user_account_settings_node)).child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);

                UniversalImageLoader.setImage(
                        userAccountSettings.getProfile_photo(), mProfilePhoto, null, "");
                mUserName.setText(userAccountSettings.getUser_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: 5");
            }
        });
    }
    

    //To setup BottomNavigationView
    private void setupBottomNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(getActivity(), bottomNavigationViewEx);

        //To highlight the menu corresponding to the activity
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(CALLING_ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    //--------------------------------------- fire base setup -----------------------------------------//
    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFireBaseAuth: ");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBaseAuth: User is signed in");

        } else {

        }
    }


    //---------------------------------- life cycle methods --------------------------------------------//
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + getActivity().toString() +
                    " must implements OnCommentThreadSelectedListener" + e.getMessage());
        }
    }
}
