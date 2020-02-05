package io.lundie.stockpile.features.targets;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.CategoryCheckListItem;
import io.lundie.stockpile.databinding.FragmentTargetsAddBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

/**
 * A simple {@link FeaturesBaseFragment} subclass.
 */
public class ManageTargetsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TargetsViewModel targetsViewModel;

    private RecyclerView catItemsRecyclerView;
    private ManageTargetsRecycleAdapter recycleAdapter;
    private ArrayList<CategoryCheckListItem> categoryCheckListItems;
    public ManageTargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentTargetsAddBinding binding = FragmentTargetsAddBinding.inflate(inflater, container, false);
        initObservers();
        catItemsRecyclerView = binding.catItemsRv;
        initAdapter();
        binding.setViewmodel(targetsViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    private void initAdapter() {
        catItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleAdapter = new ManageTargetsRecycleAdapter((view, catName) -> {
            if(((CompoundButton) view).isChecked()) {

            } else {

            }
        });
        recycleAdapter.setCategoryItems(categoryCheckListItems);
        catItemsRecyclerView.setAdapter(recycleAdapter);
    }

    private void initObservers() {
        targetsViewModel.getCategoryCheckList().observe(this.getViewLifecycleOwner(), checkListItems -> {
            if(checkListItems != null) {
                this.categoryCheckListItems = checkListItems;
                recycleAdapter.setCategoryItems(categoryCheckListItems);
                recycleAdapter.notifyDataSetChanged();
                Timber.e("Populating adapter. Total: %s", checkListItems.size());
            }
        });
    }

    private void initViewModels() {
        targetsViewModel = ViewModelProviders.of(this, viewModelFactory).get(TargetsViewModel.class);
    }
}
