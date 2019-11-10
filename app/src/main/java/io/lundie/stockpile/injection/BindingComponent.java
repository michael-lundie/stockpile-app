package io.lundie.stockpile.injection;

import androidx.databinding.DataBindingComponent;

import com.squareup.picasso.Picasso;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Assistance creating binding component from:
 * https://philio.me/using-android-data-binding-adapters-with-dagger-2/
 */
@BindingScope
@Component(modules = BindingModule.class)
public interface BindingComponent extends DataBindingComponent {

    @Component.Factory
    interface Factory {
        BindingComponent create(@BindsInstance Picasso picasso);
    }
}