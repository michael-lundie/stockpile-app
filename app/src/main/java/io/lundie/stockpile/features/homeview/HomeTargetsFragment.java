package io.lundie.stockpile.features.homeview;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import io.lundie.stockpile.databinding.FragmentHomeTargetsBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

/**
 * A simple {@link FeaturesBaseFragment} subclass.
 */
public class HomeTargetsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;

    public HomeTargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentHomeTargetsBinding binding = FragmentHomeTargetsBinding.inflate(inflater, container, false);
        setNavController(container);
        initAdapter(Navigation.findNavController(container));
        initObservers();
        binding.setHandler((HomeFragment) this.getParentFragment());
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    private void initAdapter(NavController navController) {

    }

    private void initObservers() {

    }

    private void initViewModels() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

}
