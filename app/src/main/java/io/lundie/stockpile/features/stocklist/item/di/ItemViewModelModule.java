package io.lundie.stockpile.features.stocklist.item.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.stocklist.item.ItemViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class ItemViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ItemViewModel.class)
    abstract ViewModel bindItemViewModel(ItemViewModel itemViewModel);
}
