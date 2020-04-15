package io.lundie.stockpile.features.homeview;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.Target;
import io.lundie.stockpile.databinding.FragmentHomeTargetsBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.utils.views.RecycleViewWithSetEmpty;
import timber.log.Timber;

/**
 * Fragment responsible for the display of targets created by the user.
 */
public class HomeTargetsFragment extends FeaturesBaseFragment {

    @Inject ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;
    private RecycleViewWithSetEmpty targetsRecycleView;
    private View emptyRecyclerView;
    private HomeTargetsRecycleAdapter targetsRecycleAdapter;

    private ArrayList<Target> targetItems;

    public HomeTargetsFragment() {
        // Required clear public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentHomeTargetsBinding binding = FragmentHomeTargetsBinding.inflate(inflater, container, false);
        targetsRecycleView = binding.targetItemsRv;
        emptyRecyclerView = binding.emptyView;
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
        targetsRecycleView.setEmptyView(emptyRecyclerView);
        targetsRecycleAdapter = new HomeTargetsRecycleAdapter((view, object) -> {
            homeViewModel.getTargetBus().setTarget((Target) object);
            navToManageTarget(true);
        });
        targetsRecycleAdapter.setTargetItems(targetItems);
        targetsRecycleView.setAdapter(targetsRecycleAdapter);
    }

    @Override
    public void setExFabAction() {
        if(enableExFab()) {
            Timber.e("Enabling extended fab");
            getExFab().setOnClickListener(view -> {
                navToManageTarget(false);
            });
        }
    }

    private void navToManageTarget(boolean isEdit) {
        homeViewModel.setTargetListBus();
        String title;
        if(isEdit) {
            title = getResources().getString(R.string.title_manage_target_frag_edit);
        } else {
            title = getResources().getString(R.string.title_manage_target_frag_add);
        }
        getNavController().navigate(HomeFragmentDirections.homeFragmentDestToAddTargetAction()
                .setTitle(title));
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
