package demo.android.com.instagram_clone.Home;


import com.google.firebase.auth.FirebaseAuth;

import demo.android.com.instagram_clone.Utils.SearchListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.SearchListAdapter;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserMatched;


public class ShowFollowListFragment extends Fragment implements SearchListAdapter.OnSearchItemClickListener {

    private static final String TAG = "ShowFollowListFragment";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String currentUserId;

    private RecyclerView mFollowingList;
    private ArrayList<UserMatched> mFollowingUsers;
    private SearchListAdapter adapter;


    @Override
    public void onSearchItemClick(UserMatched userMatched) {
        Log.d(TAG, "onSearchItemClick: ");

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(getString(R.string.userMatched_intent), userMatched);
        startActivity(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_follow_list, container, false);

        mFollowingList = view.findViewById(R.id.recyclerList);
        mFollowingUsers = new ArrayList<>();

        setupFireBaseAuth();
        myRef = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFollowingList.setLayoutManager(linearLayoutManager);

        adapter = new SearchListAdapter(getActivity(), mFollowingUsers, this);
        mFollowingList.setAdapter(adapter);

        if(currentUserId != null) {
            getAllFollowingUsers();
        }

        return view;
    }


    private void getAllFollowingUsers() {
        Log.d(TAG, "getAllFollowingUsers: ");
        Query query = myRef.child(getString(R.string.user_account_settings_node))
                .child(currentUserId)
                .child(getString(R.string.following_field));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                   Log.d(TAG, "onDataChange: " + snapshot);
                   LikedUser user = snapshot.getValue(LikedUser.class);
                   getUserInfo(user);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserInfo(LikedUser likedUser) {
        Log.d(TAG, "getUserInfo: ");

        String userId = likedUser.getUser_id();
        Query query = myRef.child(getString(R.string.users_node))
                .child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                getProfilePhoto(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getProfilePhoto(final User user) {
        final String userId = user.getUser_id();

        Query query = myRef.child(getString(R.string.user_account_settings_node))
                .child(userId)
                .child(getString(R.string.profile_photo_field));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                String profilePhoto = dataSnapshot.getValue().toString();
                UserMatched userMatched = new UserMatched(
                        userId,
                        user.getUser_name(),
                        profilePhoto,
                        user.getEmail()
                );
                mFollowingUsers.add(userMatched);
                adapter.notifyItemInserted(mFollowingUsers.size() - 1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            Log.d(TAG, "setupFireBaseAuth: user not signed in");
        }
    }

}
