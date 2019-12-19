package io.lundie.stockpile.features.stocklist.manageitem.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.stocklist.manageitem.ManageItemFragment;

@Module
public abstract class AddItemFragmentModule {

    @ContributesAndroidInjector(modules = { AddItemViewModelModule.class})
    abstract ManageItemFragment contributesAddItemFragment();
}
