package io.lundie.stockpile.features.stocklist.categorylist.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
public abstract class CategoryViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    abstract ViewModel bindCategoryViewModel(CategoryViewModel categoryViewModel);
}
