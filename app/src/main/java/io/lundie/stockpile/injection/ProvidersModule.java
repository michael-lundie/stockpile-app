package io.lundie.stockpile.injection;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.EventMessageController;

@Module
class ProvidersModule {
    // Note: Picasso functionality is provided through BindingComponent
    @ActivityScope
    @Provides
    EventMessageController providesEventMessageController() {
        return new EventMessageController();
    }
}
