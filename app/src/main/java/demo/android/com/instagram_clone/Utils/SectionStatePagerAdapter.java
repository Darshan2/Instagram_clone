package demo.android.com.instagram_clone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 27-05-2018.
 */

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> mFragmentArrayList = new ArrayList<>();
    private final HashMap<Fragment, Integer> fragObjectToFragNum_map = new HashMap<>();
    private final HashMap<String, Integer> fragNameToFragNum_map = new HashMap<>();
    private final HashMap<Integer, String> fragNumToFragName_map = new HashMap<>();


    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentArrayList.size();
    }


    public void addFragment(Fragment fragment, String fragName) {
        mFragmentArrayList.add(fragment);

        fragObjectToFragNum_map.put(fragment, mFragmentArrayList.size()-1);
        fragNameToFragNum_map.put(fragName, mFragmentArrayList.size()-1);
        fragNumToFragName_map.put(mFragmentArrayList.size()-1, fragName);
    }


    /**
     * Return the Fragment number with @param
     *  @param fragment
     */
    public Integer getFragmentNumber(Fragment fragment) {
        if(fragObjectToFragNum_map.containsKey(fragment)) {
            return fragObjectToFragNum_map.get(fragment);

        } else {
            return null;
        }
    }


    /**
     * Return the Fragment number with name @param
     *  @param fragmentName
     */
    public Integer getFragmentNumber(String fragmentName) {
        if(fragNameToFragNum_map.containsKey(fragmentName)) {
            return fragNameToFragNum_map.get(fragmentName);

        } else {
            return null;
        }
    }


    /**
     * Return the Fragment name with @param
     *  @param fragmentNum
     */
    public Integer getFragmentName(Integer fragmentNum) {
        if(fragObjectToFragNum_map.containsKey(fragmentNum)) {
            return fragObjectToFragNum_map.get(fragmentNum);

        } else {
            return null;
        }
    }
}
