package io.lundie.stockpile.features.stocklist.additem.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.stocklist.additem.AddItemViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class AddItemViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddItemViewModel.class)
    abstract ViewModel bindAddItemViewModel(AddItemViewModel addItemViewModel);
}
