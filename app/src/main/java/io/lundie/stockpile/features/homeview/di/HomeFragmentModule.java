package io.lundie.stockpile.features.homeview;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class HomeFragmentModule {

    @ContributesAndroidInjector
    abstract HomeFragment contributesHomeFragment();
}
