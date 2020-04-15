package io.lundie.stockpile.features.authentication.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.authentication.AuthViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel.class)
    abstract ViewModel bindAuthViewModel(AuthViewModel authViewModel);
}
