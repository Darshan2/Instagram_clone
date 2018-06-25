package demo.android.com.instagram_clone.Search;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

import demo.android.com.instagram_clone.Profile.ProfileActivity;
import demo.android.com.instagram_clone.Profile.ProfileFragment;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.SearchListAdapter;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;
import demo.android.com.instagram_clone.model.UserMatched;

public class SearchActivity extends AppCompatActivity implements SearchListAdapter.OnSearchItemClickListener {

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    //FireBase Auth, DataBase
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private ArrayList<UserMatched> userMatchedArrayList;
    private SearchListAdapter adapter;

    //widgets
    private EditText mSearchParam;
    private RecyclerView mResultList;
    private ImageView mSearchClick;
    private ProgressBar mProgressBar;
    private RelativeLayout mContainerLayout;


    @Override
    public void onSearchItemClick(UserMatched userMatched) {
        //call ProfileFragment via ProfileActivity, to handle further processes

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
        intent.putExtra(getString(R.string.userId_intent), userMatched.getUser_id());
        intent.putExtra(getString(R.string.activity_num), ACTIVITY_NUM);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupFireBaseAuth();

        myRef = FirebaseDatabase.getInstance().getReference();
        userMatchedArrayList = new ArrayList<>();
        adapter = new SearchListAdapter(this, userMatchedArrayList, this);

        mSearchParam = findViewById(R.id.searchText);
        mResultList = findViewById(R.id.recyclerList);
        mSearchClick = findViewById(R.id.ivsearch);
        mProgressBar = findViewById(R.id.progressBar);
        mContainerLayout = findViewById(R.id.searchContainer);

        mProgressBar.setVisibility(View.GONE);

        setupWidgets();
        setupBottomNavigationView();
    }


    private void setupWidgets() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(linearLayoutManager);

        mResultList.setAdapter(adapter);

        mSearchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear SearchList
                userMatchedArrayList.clear();
                mProgressBar.setVisibility(View.VISIBLE);

                String searchText = mSearchParam.getText().toString();
                searchForMatch(searchText);
            }
        });
    }


    private void searchForMatch(String searchText) {
        Log.d(TAG, "searchForMatch: " +searchText);
        final Query query = myRef.child(getString(R.string.users_node))
                .orderByChild(getString(R.string.user_name_field))
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final User user = ds.getValue(User.class);

                    if (!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        //search result is not Current user

                        //query for profile_photo
                        Query queryPhoto = myRef.child(getString(R.string.user_account_settings_node))
                                .child(user.getUser_id())
                                .child(getString(R.string.profile_photo_field));

                        queryPhoto.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: profile photo" + dataSnapshot);
                                String profilePhoto = dataSnapshot.getValue().toString();

                                UserMatched userMatched = new UserMatched(
                                        user.getUser_id(),
                                        user.getUser_name(),
                                        profilePhoto,
                                        user.getEmail()
                                );
                                userMatchedArrayList.add(userMatched);
                                adapter.notifyItemChanged(userMatchedArrayList.size());
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }























    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);

        //To highlight the menu corresponding to the activity
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
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


}
