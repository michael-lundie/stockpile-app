package io.lundie.stockpile.injection;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.utils.bindingadapters.PicassoImageBindingAdapter;

@Module
public class BindingModule {

    @Provides
    @BindingScope
    PicassoImageBindingAdapter providesImageBindingAdapter(Picasso picasso) {
        return new PicassoImageBindingAdapter(picasso);
    }
}
