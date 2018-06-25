package demo.android.com.instagram_clone.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.SectionStatePagerAdapter;
import demo.android.com.instagram_clone.Utils.StringManipulation;

/**
 * Created by Admin on 27-05-2018.
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 3;

    //widgets
    private ProgressBar mProgressBar;
    private RelativeLayout relativeLayout;
    private ViewPager viewPager;

    //vars
    public SectionStatePagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        viewPager = findViewById(R.id.container);
        relativeLayout = findViewById(R.id.relLayout1);
        mProgressBar = findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.GONE);

        //set up back arrow to go back to ProfileActivity
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On Clicking back arrow, have to navigate back from AccountSettingsActivity to ProfileActivity
                Intent intent = new Intent(AccountSettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        setUpAccountSettingsList();
        setupFragment();
        setupBottomNavigationView();
        getIncomingIntent();
    }


    private void getIncomingIntent() {
        Intent intent = getIntent();

        //if there is an image_UrlOrBitmap attached to intent then it is called from gallery/photo fragment
        if (intent.hasExtra(getString(R.string.image_url)) || intent.hasExtra(getString(R.string.image_bitmap))) {
            //call is from GalleryFragment
            mProgressBar.setVisibility(View.VISIBLE);
            if(intent.getStringExtra(getString(R.string.calling_fragment)).equals(getString(R.string.gallery_fragment))) {
                Log.d(TAG, "getIncomingIntent: called by GalleryFragment");

                //upload the profile_picture to FireBase storage
                String imageURL = StringManipulation
                        .getStorageCompatibleFilepath(intent.getStringExtra(getString(R.string.image_url)));
                DataBaseHandler dataBaseHandler = new DataBaseHandler(this, null);
                dataBaseHandler.uploadNewPhoto(
                                getString(R.string.profile_photo), "", 0, imageURL , null);
            }
            //call is from PhotoFragment
            else if (intent.getStringExtra(getString(R.string.calling_fragment)).equals(getString(R.string.photo_fragment))) {
                Log.d(TAG, "getIncomingIntent: called by PhotoFragment");

                Bitmap imageBitmap = intent.getParcelableExtra(getString(R.string.image_bitmap));
                DataBaseHandler dataBaseHandler = new DataBaseHandler(this, null);
                dataBaseHandler.uploadNewPhoto(
                        getString(R.string.profile_photo), "", 0, null , imageBitmap);
            }
        }



        if(intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: Called by ProfileFragment");
            //This activity is being called by ProfileFragment, navigate to EditProfileFragment
            setupViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void setUpAccountSettingsList() {
        ListView mListView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> settingsList = new ArrayList<>();
        settingsList.add(getString(R.string.edit_profile_fragment));//fragmentNumber = 0
        settingsList.add(getString(R.string.sign_out_fragment));//fragmentNumber = 1

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, settingsList);

        mListView.setAdapter(arrayAdapter);
        
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navaigating to fragment # " +position);
                setupViewPager(position);
            }
        });
    }


    private void setupFragment() {
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));
    }


    public void setupViewPager(int fragmentNumber) {
        Log.d(TAG, "setupViewPager: navigating to fragment # " + fragmentNumber);
        relativeLayout.setVisibility(View.GONE);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
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
}
