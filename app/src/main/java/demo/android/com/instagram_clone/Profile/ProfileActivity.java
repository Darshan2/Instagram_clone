package demo.android.com.instagram_clone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.GridImageAdapter;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;
import demo.android.com.instagram_clone.Utils.ViewCommentFragment;
import demo.android.com.instagram_clone.Utils.ViewPostFragment;
import demo.android.com.instagram_clone.model.Photo;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 3;

    //vars
    private String currentUserId;


    private ProgressBar mProgressBar;
    private ImageView mProfilePhoto;


    // ------------------------------- Interface methods ------------------------------------------//
    @Override
    public void onCommentThreadSelected(Photo photo) {
        //received photo from ViewPostFragment, now navigate to ViewCommentFragment
        Log.d(TAG, "onCommentThreadSelected: coming from ViewPostFragment photo " +photo);
        
        ViewCommentFragment viewCommentFragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        viewCommentFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewCommentFragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();
    }


    @Override
    public void onGridImageSelected(Photo photo, int activityNum) {
        //received Photo from ProfileFragment, now navigate to ViewPostFragment
        Log.d(TAG, "onGridImageSelected: Photo selected in ProfileFragment" + photo.toString());

        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_num), activityNum);
        viewPostFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewPostFragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    // ------------------------------------------------------------------------------------------ //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: ");

        setupFireBaseAuth();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(getString(R.string.calling_activity))) {
            if(intent.getStringExtra(getString(R.string.calling_activity)).equals(getString(R.string.search_activity))) {

                Log.d(TAG, "onCreate: Received intent from SearchActivity");

                String userId = intent.getStringExtra(getString(R.string.userId_intent));
                int callingActivityNum = intent.getIntExtra(getString(R.string.activity_num), 0);

                //navigate to ProfileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.userId_intent), userId);
                bundle.putInt(getString(R.string.activity_num), callingActivityNum);
                profileFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, profileFragment);
                transaction.commit();

            }
        } else {
            init();
        }
    }

    private void init() {
        Log.d(TAG, "init: inflating fragment" + getString(R.string.profile_fragment));
        ProfileFragment profileFragment = new ProfileFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, profileFragment);
        //transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


    //--------------------------------------- fire base setup -----------------------------------------//
    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFireBaseAuth: ");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBaseAuth: User is signed in");
            currentUserId = currentUser.getUid();

        } else {
            Log.d(TAG, "setupFireBaseAuth: Authentification failed");
        }
    }

}
