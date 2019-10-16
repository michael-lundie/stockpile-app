package io.lundie.stockpile.features.stocklist.itemlist.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.stocklist.itemlist.ItemListViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class ItemListViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ItemListViewModel.class)
    abstract ViewModel bindItemListViewModel(ItemListViewModel itemListViewModel);
}