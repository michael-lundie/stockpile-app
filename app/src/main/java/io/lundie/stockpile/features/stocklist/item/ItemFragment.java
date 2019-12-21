package io.lundie.stockpile.features.stocklist.item;


import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

/**
 * A simple {@link DaggerFragment} subclass.
 */
public class ItemFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ItemViewModel itemViewModel;

    private ItemDateListViewAdapter datesListViewAdapter;
    private ArrayList<Date> dateListItems;
    private RecyclerView datesRecyclerView;

    public ItemFragment() { setHasOptionsMenu(true); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        FragmentItemBinding binding = FragmentItemBinding.inflate(inflater, container, false);

        datesListViewAdapter = new ItemDateListViewAdapter();
        datesListViewAdapter.setExpiryItems(dateListItems);
        datesRecyclerView = binding.pileDatesRv;
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
        Timber.i("ON OPTION SELECTED! %s, ", item);
        if(item.getItemId() == R.id.manage_item_fragment_dest) {
            getNavController().navigate(ItemFragmentDirections.itemToManageFragmentNavAction());
        }
        return super.onOptionsItemSelected(item);
    }

    private void initObservers() {
        itemViewModel.getPileExpiryList().observe(this.getViewLifecycleOwner(), datesArrayList -> {
            if(datesArrayList != null) {
                Timber.i("ADAPTER --> UPDATING");
                this.dateListItems = datesArrayList;
                datesListViewAdapter.setExpiryItems(dateListItems);
                datesListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}