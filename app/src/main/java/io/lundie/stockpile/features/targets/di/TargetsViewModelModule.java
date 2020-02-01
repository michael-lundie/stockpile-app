package io.lundie.stockpile.features.targets.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.targets.TargetsViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class TargetsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TargetsViewModel.class)
    abstract ViewModel bindTargetsViewModel(TargetsViewModel targetsViewModel);
}
