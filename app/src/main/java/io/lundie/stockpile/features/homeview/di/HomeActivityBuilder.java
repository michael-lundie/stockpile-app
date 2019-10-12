package io.lundie.stockpile.features.homeview;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//@Module(subcomponents = { HomeActivitySubComponent.class })
//public abstract class HomeActivityBuilder {
//    @Binds
//    @IntoMap
//    @ClassKey(HomeActivity.class)
//    abstract AndroidInjector.Factory<?>
//    bindHomeActivityInjectorFactory(HomeActivitySubComponent.Factory factory);
//}
@Module
public abstract class HomeActivityBuilder {
    @ContributesAndroidInjector (modules = { HomeViewModelModule.class, HomeFragmentModule.class })
    abstract HomeActivity bindHomeActivity();
}