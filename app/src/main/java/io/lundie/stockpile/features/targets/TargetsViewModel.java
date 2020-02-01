package io.lundie.stockpile.features.targets;

import android.app.Application;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.lundie.stockpile.features.FeaturesBaseViewModel;

public class TargetsViewModel extends FeaturesBaseViewModel {

    @Inject
    public TargetsViewModel(@NonNull Application application) {
        super(application);

    }
}
