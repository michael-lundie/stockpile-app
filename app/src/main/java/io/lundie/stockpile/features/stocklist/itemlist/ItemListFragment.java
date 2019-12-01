package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.databinding.FragmentItemListBinding;

/**
 *
 */
public class ItemListFragment extends DaggerFragment {

    private static final String LOG_TAG = ItemListFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ItemListViewModel itemListViewModel;
    private ItemListViewAdapter itemListViewAdapter;
    private NavController navController;
    private ArrayList<ItemPile> listTypeItems = new ArrayList<>();
    private RecyclerView itemsRecyclerView;
    private String categoryName;

    public ItemListFragment() { /* Required empty constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "Viewmodel Factory is:" + viewModelFactory);
        itemListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemListViewModel.class);
        if(getArguments() != null) {
            //Set the list category in view model so we can pre-fetch.
            //TODO: Is it better just to page items? It's easier than managing normalised array lists.
            categoryName = ItemListFragmentArgs.fromBundle(getArguments()).getCategory();
            Log.e(LOG_TAG, "Category is:" + categoryName);
            itemListViewModel.setCategory(categoryName);
        } else {
            //TODO: Handle this error on the front end.
            Log.e(LOG_TAG, "Error retrieving category to send to view model.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(inflater, container, false);
        navController = Navigation.findNavController(container);
        itemsRecyclerView = binding.listItemsRv;
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemListViewAdapter = new ItemListViewAdapter(navController);
        itemListViewAdapter.setListTypeItems(listTypeItems);
        itemsRecyclerView.setAdapter(itemListViewAdapter);
        initObservers();
        binding.setViewmodel(itemListViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
    }

    private void initObservers() {
        itemListViewModel.getItemPilesLiveData().observe(this.getViewLifecycleOwner(),
                itemPileArrayList -> {
                    if(itemPileArrayList != null) {
                        Log.d(LOG_TAG, "Current data " + itemPileArrayList);
                        listTypeItems = itemPileArrayList;
                        itemListViewAdapter.setListTypeItems(listTypeItems);
                        itemListViewAdapter.notifyDataSetChanged();
                    }
                    Log.e(LOG_TAG, "List type items is null");
                });

        itemListViewModel.getAddItemNavEvent().observe(this.getViewLifecycleOwner(),
                categoryString -> {
                    ItemListFragmentDirections.RelayItemListToAddItemAction relayItemListToAddItemAction =
                            ItemListFragmentDirections.relayItemListToAddItemAction();
                    relayItemListToAddItemAction.setCategory(categoryString);
                    navController.navigate(relayItemListToAddItemAction);
        });
    }
}