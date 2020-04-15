package io.lundie.stockpile.features;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setNavController(container);
        return null;
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

    protected boolean getIsLandscape() { return getResources().getBoolean(R.bool.isLandscape); }

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

    protected void displaySimpleEventMessages(FeaturesBaseViewModel viewModel) {
        String eventMessage = viewModel.getStatusController().getEventMessage();
        if(eventMessage != null) {
            Toast.makeText(getContext(), eventMessage, Toast.LENGTH_SHORT).show();
        }
        viewModel.getStatusController().clearEventMessage();
    }

    protected boolean getEventPacketAndToast(@TransactionUpdateIdType.TransactionUpdateIdTypeDef int updateID,
                                       FeaturesBaseViewModel viewModel) {
        TransactionStatusController.EventPacket eventPacket =
                viewModel.getStatusController().getEventPacket(updateID);
        if (eventPacket != null) {
            Toast.makeText(getContext(), eventPacket.getEventMessage(), Toast.LENGTH_SHORT).show();
            eventPacket.clear();
            return true;
        } return false;
    }

    protected NavController getNavController() {
        if(navController != null) {
            return this.navController;
        }
        return null;
    }

    protected void setNavController(ViewGroup container) {
        this.navController = Navigation.findNavController(container);
    }

    protected void startObservingTransactionEvents(FeaturesBaseViewModel viewModel) {
        viewModel.getTransactionEvent().observe(getViewLifecycleOwner(), eventPacket -> {
            if(eventPacket != null) {
                Toast.makeText(getContext(), eventPacket.getEventMessage(), Toast.LENGTH_SHORT).show();
                eventPacket.clear();
                viewModel.getStatusController().clearEventPacket();
            }
        });
    }

    protected void stopObservingTransactionEvents(FeaturesBaseViewModel viewModel) {
        viewModel.getTransactionEvent().removeObservers(getViewLifecycleOwner());
    }
}
