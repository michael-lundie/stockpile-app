package io.lundie.stockpile.features.authentication.di;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.features.authentication.UserViewModel;
import io.lundie.stockpile.injection.ViewModelKey;

@Module
public abstract class UserViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel bindUserViewModel(UserViewModel userViewModel);
}
