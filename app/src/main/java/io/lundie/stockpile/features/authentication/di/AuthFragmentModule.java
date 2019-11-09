package io.lundie.stockpile.features.authentication.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.authentication.AuthFragment;

@Module
public abstract class AuthFragmentModule {

    @ContributesAndroidInjector(modules = {})
    abstract AuthFragment contributesAuthFragment();
}
