package io.lundie.stockpile.features.stocklist.additem.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.stocklist.additem.AddItemFragment;

@Module
public abstract class AddItemFragmentModule {

    @ContributesAndroidInjector(modules = { AddItemViewModelModule.class})
    abstract AddItemFragment contributesAddItemFragment();
}
