package io.lundie.stockpile.features.targets;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import io.lundie.stockpile.databinding.FragmentTargetsAddBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;

/**
 * A simple {@link FeaturesBaseFragment} subclass.
 */
public class ManageTargetsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TargetsViewModel targetsViewModel;

    public ManageTargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentTargetsAddBinding binding = FragmentTargetsAddBinding.inflate(inflater, container, false);
        initAdapter(Navigation.findNavController(container));
        initObservers();
        binding.setViewmodel(targetsViewModel);
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
        targetsViewModel = ViewModelProviders.of(this, viewModelFactory).get(TargetsViewModel.class);
    }
}
