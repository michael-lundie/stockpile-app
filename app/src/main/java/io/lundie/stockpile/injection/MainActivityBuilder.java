package io.lundie.stockpile.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.data.repository.RepositoryProviderModule;
import io.lundie.stockpile.features.homeview.di.HomeFragmentModule;
import io.lundie.stockpile.features.stocklist.categorylist.di.CategoryFragmentModule;

//@Module(subcomponents = { HomeActivitySubComponent.class })
//public abstract class MainActivityBuilder {
//    @Binds
//    @IntoMap
//    @ClassKey(HomeActivity.class)
//    abstract AndroidInjector.Factory<?>
//    bindHomeActivityInjectorFactory(HomeActivitySubComponent.Factory factory);
//}
@Module
public abstract class MainActivityBuilder {
    @ContributesAndroidInjector (
            modules = { RepositoryProviderModule.class,
                        HomeFragmentModule.class,
                        CategoryFragmentModule.class})
    abstract MainActivity bindMainActivity();
}