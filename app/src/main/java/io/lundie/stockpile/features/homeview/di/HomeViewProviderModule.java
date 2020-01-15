package io.lundie.stockpile.features.homeview.di;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.homeview.HomeFragment;
import io.lundie.stockpile.features.homeview.HomeFragmentPagerAdapter;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

@Module
public class HomeViewProviderModule {

    @Provides
    HomeFragmentPagerAdapter provideHomeFragmentPagerAdapter(HomeFragment homeFragment) {
        return new HomeFragmentPagerAdapter(homeFragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }
}
