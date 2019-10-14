package io.lundie.stockpile.data.repository;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryProviderModule {

    @Provides
    UserRepository provideUserRepository() { return  new UserRepository(); }

    @Provides
    CategoryRepository provideCategoryRepository() { return  new CategoryRepository(); }
}
