package io.lundie.stockpile.features.stocklist.manageitem.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.stocklist.manageitem.ManageItemViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class AddItemViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ManageItemViewModel.class)
    abstract ViewModel bindAddItemViewModel(ManageItemViewModel manageItemViewModel);
}
