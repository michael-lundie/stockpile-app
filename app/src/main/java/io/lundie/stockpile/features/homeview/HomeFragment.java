package io.lundie.stockpile.features.homeview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.databinding.FragmentHomeBinding;

/**
 *
 */
public class HomeFragment extends DaggerFragment {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;

    public HomeFragment() { /* Required empty constructor */ }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Initiating HomeFragment onCreateView.");

        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        //TODO: Confirm how this data-binding method is actually getting our layout id
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
