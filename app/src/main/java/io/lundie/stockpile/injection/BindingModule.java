package io.lundie.stockpile.injection;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.stocklist.ImageBindingAdapter;

@Module
public class BindingModule {

    @Provides
    @BindingScope
    ImageBindingAdapter providesImageBindingAdapter(Picasso picasso) {
        return new ImageBindingAdapter(picasso);
    }
}
