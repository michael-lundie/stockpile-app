package io.lundie.stockpile.features.homeview.di;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.homeview.HomeRepository;

@Module
public class HomeViewProviderModule {

    @Provides
    HomeRepository provideHomeRepository() {
        return new HomeRepository();
    }
}
