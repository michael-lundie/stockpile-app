package io.lundie.stockpile.features.homeview.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.homeview.ExpiringItemsFragment;
import io.lundie.stockpile.features.homeview.HomeFragment;
import io.lundie.stockpile.features.homeview.HomeTargetsFragment;

@Module
public abstract class HomeFragmentModule {

    @ContributesAndroidInjector(modules = { HomeViewProviderModule.class, HomeViewModelModule.class })
    abstract HomeFragment contributesHomeFragment();

    @ContributesAndroidInjector(modules = { HomeViewProviderModule.class, HomeViewModelModule.class })
    abstract ExpiringItemsFragment contributesExpiringItemsFragment();

    @ContributesAndroidInjector(modules = { HomeViewProviderModule.class, HomeViewModelModule.class })
    abstract HomeTargetsFragment contributesHomeTargetsFragment();
}
