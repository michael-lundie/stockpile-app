package io.lundie.stockpile.features.homeview;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.Target;
import io.lundie.stockpile.databinding.FragmentHomeTargetsBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.targets.ManageTargetsRecycleAdapter;
import timber.log.Timber;

/**
 * A simple {@link FeaturesBaseFragment} subclass.
 */
public class HomeTargetsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;
    private RecyclerView targetsRecycleView;
    private HomeTargetsRecycleAdapter targetsRecycleAdapter;

    private ArrayList<Target> targetItems;

    public HomeTargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentHomeTargetsBinding binding = FragmentHomeTargetsBinding.inflate(inflater, container, false);
        targetsRecycleView = binding.targetItemsRv;
        initAdapter();
        initObservers();
        setNavController(container);
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

    private void initAdapter() {
        targetsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        targetsRecycleAdapter = new HomeTargetsRecycleAdapter((view, object) -> {
        });
        targetsRecycleAdapter.setTargetItems(targetItems);
        targetsRecycleView.setAdapter(targetsRecycleAdapter);
    }


    @Override
    public void setExFabAction() {
        if(enableExFab()) {
            Timber.e("Enabling extended fab");
            getExFab().setOnClickListener(view -> {
                getNavController().navigate(HomeFragmentDirections.homeFragmentDestToAddTargetAction());
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        disableExFab();
    }

    private void initObservers() {
        homeViewModel.getTargetsLiveData().observe(this.getViewLifecycleOwner(), targets -> {
            if (targets != null) {
                this.targetItems = targets;
                targetsRecycleAdapter.setTargetItems(targetItems);
                targetsRecycleAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViewModels() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

}
