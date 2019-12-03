package io.lundie.stockpile.features.stocklist.itemlist.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.stocklist.itemlist.ItemListFragment;

@Module
public abstract class ItemListFragmentModule {

    @ContributesAndroidInjector(modules = { ItemListViewModelModule.class})
    abstract ItemListFragment contributesItemListFragment();
}