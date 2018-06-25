package demo.android.com.instagram_clone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Used to SetUp Viewpager to display the fragment, as selected by tab
 */

/**
 * If number of Fragments to be displayed is <5 or and has to associate with
 * Tab-layout then use FragmentPagerAdapter else use FragmentStatePagerAdapter
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //return fragment to be displayed in container.
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        //number of fragments FragmentPagerAdapter has to display
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentArrayList.add(fragment);
    }
}
