package io.lundie.stockpile.features;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import timber.log.Timber;

public abstract class FeaturesBaseFragment extends DaggerFragment {

    private static final String LOG_TAG = FeaturesBaseFragment.class.getSimpleName();

    private FloatingActionButton universalFAB;
    private NavController navController;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        universalFAB = Objects.requireNonNull(getActivity()).findViewById(R.id.activity_main_fab);
        setFabAction();
    }

    @Override
    public void onStart() {
        super.onStart();
        setFabAction();
    }

    public void setFabAction() {
        disableFab();
    }

    @SuppressLint("RestrictedApi")
    protected void enableFab() {
        if(universalFAB != null) {
            universalFAB.setVisibility(View.VISIBLE);
        } else {
            Timber.e("Cannot set FAB visibility. View is null. Try setting in onActivityCreated");
        }
    }

    @SuppressLint("RestrictedApi")
    protected void disableFab() {
        if(universalFAB != null) {
            universalFAB.setVisibility(View.GONE);
        } else {
            Timber.e("Cannot set FAB visibility. View is null. Try setting in onActivityCreated");
        }
    }

    protected FloatingActionButton getFab() {
        if(universalFAB != null) {
            return universalFAB;
        } else {
            Timber.e("Fab is null. Try accessing in onActivityCreated.");
        } return null;
    }

    protected void setNavController(ViewGroup container) {
        this.navController = Navigation.findNavController(container);
    }

    protected NavController getNavController() {
        if(navController != null) {
            return this.navController;
        }
        Timber.e("Nav Controller is null. Make sure to setNavController in onCreateView");
        return null;
    }
}
