package demo.android.com.instagram_clone.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


import demo.android.com.instagram_clone.Profile.ProfileFragment;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.GridImageAdapter;
import demo.android.com.instagram_clone.Utils.MainFeedListAdapter;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.UserAccountSettings;

/**
 * Created by Admin on 25-05-2018.
 */

public class HomeFragment extends Fragment implements MainFeedListAdapter.OnMainFeedListItemClickListener {

    private static final String TAG = "HomeFragment";
    private static final int PAGE_SIZE_LIMIT = 10;

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private String currentUserId;

    //widgets
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ImageView mLoadMore;

    //vars
    private ArrayList<Photo> mMainFeedItemsList;
    private OnHomeFragmentClickListener onHomeFragmentClickListener;
    private MainFeedListAdapter adapter;
    private int usersCount = 0;
    private int totalUsersCount = 0;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Photo> mPaginatedPhotos;
    private int mResults = 0;
    private int mCurrentRecyclerItemsCount = 0;

    // ------------------------------- Interface linkups -----------------------------------------//
    public interface OnHomeFragmentClickListener {
        void onViewCommentClick(Photo photo);
        void onProfileMenuClick(String photoUploadedUserId);
    }


    @Override
    public void onLoadMoreItems(int currentItemsCount) {
        // paginate, add next batch of photos to RecyclerView
        Log.d(TAG, "onLoadMoreItems: " + currentItemsCount);

        //to avoid displaying mLoadMore, in any other times than that of the PageLoading time.
        if(mCurrentRecyclerItemsCount < mMainFeedItemsList.size() &&
                (currentItemsCount % 10) == 0) {
            Log.d(TAG, "onLoadMoreItems: mod " + (mCurrentRecyclerItemsCount % 10));
            mLoadMore.setVisibility(View.VISIBLE);
            mCurrentRecyclerItemsCount = currentItemsCount;
        }
        //displayMorePhotos(currentItemsCount);

    }

    @Override
    public void onMainFeedItemCommentBubbleClick(Photo photo) {
        //received Photo from MainFeedListAdapter, navigate to ViewCommentFragment via HomeActivity
        onHomeFragmentClickListener.onViewCommentClick(photo);

    }

    @Override
    public void onMainFeedItemMenuClick(String photoUploadedUserId) {
        //received photoUploadedUserId from MainFeedListAdapter, navigate to ProfileFragment via HomeActivity
        Log.d(TAG, "onMainFeedItemMenuClick: " + photoUploadedUserId);
        onHomeFragmentClickListener.onProfileMenuClick(photoUploadedUserId);
    }

    //---------------------------------------------------------------------------------------------//

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        setupFireBaseAuth();

        myRef = FirebaseDatabase.getInstance().getReference();
        dataBaseHandler = new DataBaseHandler(getActivity(), null);
        mMainFeedItemsList = new ArrayList<>();

        mProgressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.recyclerList);
        mLoadMore = view.findViewById(R.id.load_more);

        mProgressBar.setVisibility(View.GONE);
        mLoadMore.setVisibility(View.GONE);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(currentUserId != null) {
            getAllUsers();
        }


        mLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load more photos in RecyclerView
                displayMorePhotos();
                mLoadMore.setVisibility(View.GONE);
            }
        });

        return view;
    }


    private void setupMainFeedList() {

        if(usersCount == totalUsersCount) {
            Collections.sort(mMainFeedItemsList, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                }
            });

            mProgressBar.setVisibility(View.GONE);

            //first send 10 photos to Recycler view, then 10 and so on.....
            mPaginatedPhotos = new ArrayList<>();
            int iterations = mMainFeedItemsList.size();

            mResults = 10;
            if(iterations > 10) {
                iterations = 10;
            }

            for (int i = 0; i < iterations; i++) {
                mPaginatedPhotos.add(mMainFeedItemsList.get(i));
            }

            for(int i = 0; i < mMainFeedItemsList.size(); i++) {
                Log.d(TAG, "setupMainFeedList: date " + mMainFeedItemsList.get(i).getDate_created());
            }

            adapter = new MainFeedListAdapter(getActivity(), mPaginatedPhotos, this);
            mRecyclerView.setAdapter(adapter);

        }

    }


    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: ");

        try {
            int iterations;
            if (mMainFeedItemsList.size() > mCurrentRecyclerItemsCount + 10) {
                Log.d(TAG, "displayMorePhotos: there are more than 10 more photos");
                iterations = 10;
            } else {
                Log.d(TAG, "displayMorePhotos: there are less than 10 more photos");
                iterations = mMainFeedItemsList.size() - mCurrentRecyclerItemsCount;
            }

            //add next 10 or <10 photos to RecyclerView
            for (int i = mCurrentRecyclerItemsCount; i < mCurrentRecyclerItemsCount + iterations; i++) {
                mPaginatedPhotos.add(mMainFeedItemsList.get(i));
            }
            adapter.notifyItemRangeChanged(mCurrentRecyclerItemsCount, iterations);
//            mResults += 10;

        } catch (Exception e) {
            Log.d(TAG, "displayMorePhotos: " + e.getMessage());
        }


    }


    /*
        Get users which are followed by current user
     */
    private void getAllUsers() {
        mProgressBar.setVisibility(View.VISIBLE);
        Query followingQuery = myRef.child(getString(R.string.user_account_settings_node))
                .child(currentUserId);

        followingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: following user ");
                UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                getPhotosOfAllUsers(userAccountSettings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
        Get all the photos posted by Current user, and by all the users followed by Current user
     */
    private void getPhotosOfAllUsers(final UserAccountSettings userAccountSettings) {
        Log.d(TAG, "getPhotosOfAllFollowingUsers: ");

        ArrayList<LikedUser> allUsersList = new ArrayList<>();
        allUsersList.add(new LikedUser(currentUserId));// adding Current user to allUsers list

        if(userAccountSettings.getFollowing() != null) {
            //add the users followed by Current user to allUsersList
            HashMap<String, LikedUser> following = userAccountSettings.getFollowing();
            Collection<LikedUser> likedUserCollection = following.values();
            allUsersList.addAll(likedUserCollection);
        }

        totalUsersCount = allUsersList.size();

        //here get all the photos posted by each users
        for (LikedUser likedUser : allUsersList) {
            final String likedUserId = likedUser.getUser_id();
            Query query = myRef.child(getString(R.string.user_photos_node))
                    .child(likedUserId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Photo photo = snapshot.getValue(Photo.class);
                        if(photo.getDate_created() != null || photo.getDate_created() != "") {

                            mMainFeedItemsList.add(photo);
                        }
                    }
                    usersCount++;
                    setupMainFeedList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    //--------------------------------------- fire base setup -----------------------------------------//
    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFireBaseAuth: ");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBaseAuth: User is signed in");
            currentUserId = currentUser.getUid();

        } else {

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onHomeFragmentClickListener = (OnHomeFragmentClickListener) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: HomeActivity, must implement OnHomeFragmentClickListner" + e.getMessage());
        }
    }
}
