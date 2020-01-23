package io.lundie.stockpile.features.authentication.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.authentication.AuthRegisterFragment;

@Module
public abstract class AuthFragmentModule {

    @ContributesAndroidInjector(modules = AuthViewModelModule.class)
    abstract AuthRegisterFragment contributesAuthRegisterFragment();
}