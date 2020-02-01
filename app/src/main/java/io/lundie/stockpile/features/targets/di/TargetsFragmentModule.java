package io.lundie.stockpile.features.targets.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.targets.ManageTargetsFragment;

@Module
public abstract class TargetsFragmentModule {

    @ContributesAndroidInjector(modules = TargetsViewModelModule.class)
    abstract ManageTargetsFragment contributesAddTargetsFragment();
}
