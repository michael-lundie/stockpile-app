package io.lundie.stockpile.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.features.homeview.di.HomeFragmentModule;

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
    @ContributesAndroidInjector (modules = { HomeFragmentModule.class})
    abstract MainActivity bindMainActivity();
}