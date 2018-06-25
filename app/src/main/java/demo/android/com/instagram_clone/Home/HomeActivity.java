package demo.android.com.instagram_clone.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.transition.Slide;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import demo.android.com.instagram_clone.LogIn.LoginActivity;
import demo.android.com.instagram_clone.Profile.ProfileActivity;
import demo.android.com.instagram_clone.Profile.ProfileFragment;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.SectionsPagerAdapter;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;
import demo.android.com.instagram_clone.Utils.ViewCommentFragment;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.User;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentClickListener {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;// tab number
    private FirebaseAuth mAuth;

    //widgets
    private ViewPager mViewPager;
    private RelativeLayout mParentRelativeLayout;
    private FrameLayout mFrameLayout;



    @Override
    public void onViewCommentClick(Photo photo) {
        //Invoked in order to handle MainFeedListAdapter's CommentBubble click called via HomeFragment
        hideLayout();

        Log.d(TAG, "onViewCommentClick: going to ViewCommentFragment");

        ViewCommentFragment viewCommentFragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.calling_activity), getString(R.string.home_activity));
        viewCommentFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, viewCommentFragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();
    }

    @Override
    public void onProfileMenuClick(String photoUploadedUserId) {
        hideLayout();
        //navigate to ProfileFragment
        Log.d(TAG, "onProfileMenuClick: navigating to ProfileFragment");

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.userId_intent), photoUploadedUserId);
        args.putString(getString(R.string.calling_activity), getString(R.string.home_activity));
        args.putInt(getString(R.string.activity_num), ACTIVITY_NUM);
        profileFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.replace(R.id.frameContainer, profileFragment);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mViewPager = findViewById(R.id.container);
        mParentRelativeLayout = findViewById(R.id.parent_relLayout);
        mFrameLayout = findViewById(R.id.frameContainer);

        initImageLoader();
        setupViewPager();
        setupBottomNavigationView();
        setupFireBaseAuth();
    }

    /**
     * Initialize ImageLoader globally for the app
     */
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);

        ImageLoader.getInstance().init(universalImageLoader.getImageLoaderConfig());
    }

    /**
     * Responsible for adding 3 fragments camera, home, messages, and displaying the tab selected fragment
     */
    private void setupViewPager() {

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new CameraFragment());//index 0
        sectionsPagerAdapter.addFragment(new HomeFragment());//index 1
        sectionsPagerAdapter.addFragment(new ShowFollowListFragment());//index 2

        mViewPager.setAdapter(sectionsPagerAdapter);

        /**
         * Following code, add 3 tabs(= number of fragments that has to be displayed by ViewPager)
         * to TabLayout
        */
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //set up tab icons
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);

    }


    public void hideLayout() {
        mFrameLayout.setVisibility(View.VISIBLE);
        mParentRelativeLayout.setVisibility(View.GONE);
    }

    public void showLayout() {
        mFrameLayout.setVisibility(View.GONE);
        mParentRelativeLayout.setVisibility(View.VISIBLE);
    }


    //To setup BottomNavigationView
    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);

        //To highlight the menu corresponding to the activity
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    // ------------------------- FireBase Authentication ----------------------------------------- //

    private void setupFireBaseAuth() {
       mAuth = FirebaseAuth.getInstance();
//       mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//           @Override
//           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//               FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//               if(currentUser != null) {
//                   Log.d(TAG, "setupFireBaseAuth: User is signed in");
//               } else {
//                   //If user is not signed in direct them to LogInActivity
//                   Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                   startActivity(intent);
//               }
//           }
//       };

    }

    @Override
    public void onStart() {
        super.onStart();
        //to display HomeFragment first when HomeActivity inflated
        mViewPager.setCurrentItem(HOME_FRAGMENT);

        // Check if user is signed in (non-null) and update UI accordingly.
        setupFireBaseAuth();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "onStart: User is signed in");
        } else {
            //If user is not signed in direct them to LogInActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }
}
