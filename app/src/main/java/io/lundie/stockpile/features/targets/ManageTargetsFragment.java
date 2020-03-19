package io.lundie.stockpile.features.targets;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.internal.CategoryCheckListItem;
import io.lundie.stockpile.databinding.FragmentTargetsAddBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.general.AlertDialogFragment;

/**
 * A simple {@link FeaturesBaseFragment} subclass.
 */
public class ManageTargetsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ManageTargetsViewModel targetsViewModel;
    private RecyclerView catItemsRecyclerView;
    private ManageTargetsRecycleAdapter recycleAdapter;
    private ArrayList<CategoryCheckListItem> categoryCheckListItems;

    public ManageTargetsFragment() { /* Required clear public constructor */ }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentTargetsAddBinding binding = FragmentTargetsAddBinding.inflate(inflater, container, false);
        setNavController(container);
        initObservers();
        catItemsRecyclerView = binding.catItemsRv;
        initAdapter();
        binding.setViewmodel(targetsViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String title = ManageTargetsFragmentArgs.fromBundle(getArguments()).getTitle();
            if (title != null &&
                    title.equals(getResources().getString(R.string.title_manage_target_frag_edit))) {
                setHasOptionsMenu(true);
            }
        }
        initViewModels();
    }

    private void initAdapter() {
        catItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleAdapter = new ManageTargetsRecycleAdapter((view, catName) -> {
            if (view instanceof LinearLayout) {
                view = ((LinearLayout) view).getChildAt(0);
            }
            targetsViewModel.onCheckClicked(catName.toString(), ((CompoundButton) view).isChecked());
        });
        recycleAdapter.setCategoryItems(categoryCheckListItems);
        catItemsRecyclerView.setAdapter(recycleAdapter);
    }

    private void initObservers() {
        targetsViewModel.getCategoryCheckList().observe(this.getViewLifecycleOwner(), checkListItems -> {
            if (checkListItems != null) {
                this.categoryCheckListItems = checkListItems;
                recycleAdapter.setCategoryItems(categoryCheckListItems);
                recycleAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViewModels() {
        targetsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ManageTargetsViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        AlertDialogFragment alertDialogFragment =
                AlertDialogFragment.newInstance(
                        getResources().getString(R.string.dialog_title_confirm_delete),
                        getResources().getString(R.string.dialog_label_delete_target),
                        getResources().getString(R.string.action_yes),
                        getResources().getString(R.string.action_no),
                        this::onDeleteTargetConfirmed);
        alertDialogFragment.show(getChildFragmentManager(), "AlertDialog");
    }

    public void onUpdateTargetClicked() {
        targetsViewModel.getIsUpdateSuccessful().observe(getViewLifecycleOwner(), isSuccessful -> {
            if (isSuccessful) {
                popNavigation();
            } else {
                //TODO: validation error
            }
        });
        targetsViewModel.onAddTargetClicked();
    }

    private void onDeleteTargetConfirmed() {
        targetsViewModel.onDeleteClicked();
        ManageTargetsFragment.this.popNavigation();
    }

    private void popNavigation() {
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.home_fragment_dest, false).build();
        getNavController().navigate(ManageTargetsFragmentDirections.popToHomeFragment(), navOptions);
    }
}