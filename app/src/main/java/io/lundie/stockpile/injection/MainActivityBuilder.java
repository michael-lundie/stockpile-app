package io.lundie.stockpile.injection;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.MainActivity;

@Module(subcomponents = { MainActivitySubComponent.class })
public abstract class MainActivityBuilder {
    @Binds
    @IntoMap
    @ClassKey(MainActivity.class)
    abstract AndroidInjector.Factory<?>
    bindHomeActivityInjectorFactory(MainActivitySubComponent.Factory factory);
}
//TODO: Remove SubComponent and use ContributesAndroidInjector if we don't need to do anything extra
// here
//@Module
//public abstract class MainActivityBuilder {
//    @ContributesAndroidInjector (
//            modules = { RepositoryProviderModule.class,
//                        HomeFragmentModule.class,
//                        AddItemFragmentModule.class})
//    abstract MainActivity bindMainActivity();
//}