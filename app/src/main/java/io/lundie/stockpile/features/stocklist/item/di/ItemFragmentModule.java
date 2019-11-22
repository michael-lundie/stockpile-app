package io.lundie.stockpile.features.stocklist.item.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.stocklist.item.ItemFragment;

@Module
public abstract class ItemFragmentModule {

    @ContributesAndroidInjector(modules = {ItemViewModelModule.class})
    abstract ItemFragment contributesItemFragment();
}
