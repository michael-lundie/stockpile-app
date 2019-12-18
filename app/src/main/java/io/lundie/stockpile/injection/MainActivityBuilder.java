package io.lundie.stockpile.injection;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.lundie.stockpile.MainActivity;

@Module(subcomponents = { MainActivitySubComponent.class })
abstract class MainActivityBuilder {
    @Binds
    @IntoMap
    @ClassKey(MainActivity.class)
    abstract AndroidInjector.Factory<?>
    bindHomeActivityInjectorFactory(MainActivitySubComponent.Factory factory);
}