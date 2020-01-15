package io.lundie.stockpile.features.homeview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private final FragmentManager fragmentManager;
    private final ArrayList<Fragment> fragments;
    private final ArrayList<String> titles;

    public HomeFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.fragmentManager = fm;
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    void addFragment(Fragment fragment, String tabTitle) {
        boolean hasFragmentInstance = false;
        for (String title: titles) {
            if(title.equals(tabTitle)) {
                hasFragmentInstance = true;
            }
        }
        if(!hasFragmentInstance) {
            fragments.add(fragment);
            titles.add(tabTitle);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        if (!fragments.contains(object)) {
            fragmentManager.beginTransaction().remove((Fragment) object).commit();
            fragments.remove(position);
            titles.remove(position);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
