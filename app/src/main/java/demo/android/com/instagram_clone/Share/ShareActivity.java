package demo.android.com.instagram_clone.Share;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.BottomNavigationViewHelper;
import demo.android.com.instagram_clone.Utils.SectionsPagerAdapter;
import demo.android.com.instagram_clone.model.Permissions;

public class ShareActivity extends AppCompatActivity {

    private static final String TAG = "ShareActivity";
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Log.d(TAG, "onCreate: ");

        //To check device permissions for app
        if(checkPermissionsArray(Permissions.PERMISSIONS)) {
            //All requested permissions, has been granted by user, proceed to next step
            setupViewPager();
        }else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }

    public int getTask() {
        Log.d(TAG, "getTask: Task" + getIntent().getFlags()+"");
        return getIntent().getFlags();
    }

    public int getCurrentTabNum() {
        return mViewPager.getCurrentItem();
    }

    private void setupViewPager() {
        mViewPager = findViewById(R.id.container);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }


    /**
     * verify all permissions
     * @param permissions
     */
    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: permissions" );
        //Requesting user to allow access, to all the requested permissions
        ActivityCompat.requestPermissions(this, permissions, VERIFY_PERMISSIONS_REQUEST );
    }


    /**
     * Check a Array of permissions
     * @param permissions
     * @return
     */
    private boolean checkPermissionsArray(String[] permissions) {
        for(int i = 0; i <  permissions.length; i++) {
            String permission = permissions[i];
            if(!checkPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been granted
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission) {
        Log.d(TAG, "checkPermission: checking permission: " + permission);

        int permissionGranted = ActivityCompat.checkSelfPermission(this, permission);

        if(permissionGranted != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: permission not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermission: permission granted for: " + permission);
            return true;
        }
    }


}
