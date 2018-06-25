package demo.android.com.instagram_clone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.Profile.ProfileActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Search.SearchActivity;
import demo.android.com.instagram_clone.Share.ShareActivity;

/**
 * To override default animation of BottomNavigationViewEx
 */


public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHelper";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {

        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);


    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx bottomNavigationViewEx) {

        bottomNavigationViewEx.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Start the new activity based on the menu item clicked
                Intent intent = null;

                switch (item.getItemId()) {

                    case R.id.ic_house : {
                        intent = new Intent(context, HomeActivity.class); //ACTIVITY_NUM = 0
                        break;
                    }

                    case R.id.ic_search : {
                        intent = new Intent(context, SearchActivity.class); //ACTIVITY_NUM = 1
                        break;
                    }

                    case R.id.ic_circle : {
                        intent = new Intent(context, ShareActivity.class); //ACTIVITY_NUM = 2
                        break;
                    }


                    case R.id.ic_android : {
                        intent = new Intent(context, ProfileActivity.class); //ACTIVITY_NUM = 3
                        break;
                    }
                }

                if(intent != null) {
                    context.startActivity(intent);
                }

                return false;
            }
        });
    }
}
