package io.lundie.stockpile.features.stocklist.categorylist.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryFragment;

@Module
public abstract class CategoryFragmentModule {

    @ContributesAndroidInjector(modules = { CategoryViewModelModule.class})
    abstract CategoryFragment contributesCategoryFragment();
}
