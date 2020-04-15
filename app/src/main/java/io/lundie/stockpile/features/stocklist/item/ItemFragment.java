package io.lundie.stockpile.features.stocklist.item;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.general.AlertDialogFragment;
import timber.log.Timber;

/**
 * Fragment responsible for the display of an item
 */
public class ItemFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ItemViewModel itemViewModel;

    private ItemDateListViewAdapter datesListViewAdapter;
    private ArrayList<Date> dateListItems;
    private int originatingFragmentID;

    public ItemFragment() { setHasOptionsMenu(true); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        if (getArguments() != null) {
            String itemPileName = ItemFragmentArgs.fromBundle(getArguments()).getItemName();
            itemViewModel.setItem(itemPileName);
            originatingFragmentID = ItemFragmentArgs.fromBundle(getArguments()).getOriginatingFragment();
        } else {
            //TODO: Handle this error on the front end.
            Timber.e("Error retrieving category to send to view model.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        FragmentItemBinding binding = FragmentItemBinding.inflate(inflater, container, false);
        datesListViewAdapter = new ItemDateListViewAdapter();
        datesListViewAdapter.setExpiryItems(dateListItems);
        RecyclerView datesRecyclerView = binding.pileDatesRv;
        datesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        datesRecyclerView.setAdapter(datesListViewAdapter);
        initObservers();
        binding.setViewmodel(itemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_item_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.manage_item_fragment_dest:
                itemViewModel.setItemPileBus();
                getNavController().navigate(
                        ItemFragmentDirections.itemToManageFragmentNavAction().setIsEditMode(true));
                break;
            case R.id.action_delete:
                showConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        AlertDialogFragment alertDialogFragment =
                AlertDialogFragment.newInstance(
                        getResources().getString(R.string.dialog_title_confirm_delete),
                        getResources().getString(R.string.dialog_label_delete_item),
                        getResources().getString(R.string.action_yes),
                        getResources().getString(R.string.action_no),
                        this::onDeleteItemConfirmed);
        alertDialogFragment.show(getChildFragmentManager(), "AlertDialog");
    }

    private void initObservers() {
        itemViewModel.getPileExpiryList().observe(this.getViewLifecycleOwner(), datesArrayList -> {
            if(datesArrayList != null) {
                this.dateListItems = datesArrayList;
                datesListViewAdapter.setExpiryItems(dateListItems);
                datesListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void onDeleteItemConfirmed() {
        itemViewModel.onDeleteClicked();
        popNavigation();
    }

    private void popNavigation() {
        NavOptions navOptions;
        switch (originatingFragmentID) {
            case R.id.expiring_items_fragment:
                navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.home_fragment_dest, false)
                        .build();
                getNavController().navigate(ItemFragmentDirections.popToHomeFragment(), navOptions);
                break;
            case R.id.item_list_fragment:
            default:
                navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.item_list_fragment, false)
                        .build();
                getNavController().navigate(ItemFragmentDirections.popToItemListFragment(), navOptions);
                break;
        }
    }
}