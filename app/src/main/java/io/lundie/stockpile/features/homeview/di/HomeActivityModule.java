package io.lundie.stockpile.features.homeview;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeActivityModule {

    @Provides
    HomeRepository provideHomeRepository() {
        return new HomeRepository();
    };
}
