package io.lundie.stockpile.features;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import timber.log.Timber;

public abstract class FeaturesBaseFragment extends DaggerFragment {

    private FloatingActionButton universalFAB;
    private ExtendedFloatingActionButton extendedFAB;
    private NavController navController;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        universalFAB = Objects.requireNonNull(getActivity()).findViewById(R.id.activity_main_fab);
        extendedFAB = Objects.requireNonNull(getActivity().findViewById(R.id.activity_extended_fab));

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        // There methods called here will re-enable the FABs, if an extended fragment
        // overrides the default behavior
        setFabAction();
        setExFabAction();
    }

    public void setFabAction() {
        disableFab();
    }
    public void setExFabAction() { disableExFab(); }

    @SuppressLint("RestrictedApi")
    protected void enableFab() {
        if(universalFAB != null) {
            universalFAB.setVisibility(View.VISIBLE);
        } else {
            Timber.e("Cannot set FAB visibility. View is null. Try setting in onActivityCreated");
        }
    }

    @SuppressLint("RestrictedApi")
    private void disableFab() {
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

    @SuppressLint("RestrictedApi")
    protected boolean enableExFab() {
        if(extendedFAB != null) {
            extendedFAB.setVisibility(View.VISIBLE);
            Timber.e("Setting fab visibility true");
            return true;
        } else {
            Timber.e("Cannot set extended FAB visibility. View is null. Try setting in onActivityCreated");
        } return false;
    }

    @SuppressLint("RestrictedApi")
    protected void disableExFab() {
        if(extendedFAB != null) {
            extendedFAB.setVisibility(View.GONE);
        } else {
            Timber.e("Cannot set extended FAB visibility. View is null. Try setting in onActivityCreated");
        }
    }

    protected ExtendedFloatingActionButton getExFab() {
        if(extendedFAB != null) {
            return extendedFAB;
        } else {
            Timber.e("Fab is null. Try accessing in onActivityCreated.");
        } return null;
    }


    //TODO: Replace setting Nav Controller in this manner. It is causing problems
    // when popping fragments.
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
