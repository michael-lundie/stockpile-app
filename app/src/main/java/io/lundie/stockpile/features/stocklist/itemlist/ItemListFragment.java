package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.databinding.FragmentItemListBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.TransactionStatusController;
import io.lundie.stockpile.features.TransactionUpdateIdType;
import timber.log.Timber;

/**
 *
 */
public class ItemListFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private static String SAVED_STATE_KEY = "item_list_state";
    private static String CATEGORY_KEY = "category";

    private ItemListViewModel itemListViewModel;
    private ItemListViewAdapter itemListViewAdapter;
    private ArrayList<ItemPile> listTypeItems;
    private RecyclerView itemsRecyclerView;
    private String categoryName;
    private Bundle savedState = null;

    public ItemListFragment() { /* Required clear constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("Viewmodel Factory is:%s", viewModelFactory);
        itemListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(inflater, container, false);

        restoreState(savedInstanceState);

        if (listTypeItems == null) {
            listTypeItems = new ArrayList<>();
        }

        if (getArguments() != null) {
            categoryName = ItemListFragmentArgs.fromBundle(getArguments()).getCategory();
            Timber.e("Category is:%s", categoryName);
            itemListViewModel.setCategory(categoryName);
        } else {
            //TODO: Handle this error on the front end.
            Timber.e("Error retrieving category to send to view model.");
        }

        setNavController(container);
        itemsRecyclerView = binding.listItemsRv;
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        itemListViewAdapter = new ItemListViewAdapter(this::navigateToRequestedItem);
        itemListViewAdapter.setListTypeItems(listTypeItems);
        itemsRecyclerView.setAdapter(itemListViewAdapter);
        initObservers();
        binding.setViewmodel(itemListViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        displaySimpleEventMessages(itemListViewModel);
        startObservingTransactionEvents(itemListViewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopObservingTransactionEvents(itemListViewModel);
    }

    private void navigateToRequestedItem(View view, Object itemPile) {
        if(itemPile instanceof ItemPile) {
            getNavController().navigate(ItemListFragmentDirections.relayItemListToItemAction(
                            ((ItemPile) itemPile).getItemName(), R.id.item_list_fragment));
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle(SAVED_STATE_KEY);
        }

        if (savedState != null) {
            categoryName = savedState.getString(CATEGORY_KEY);
            Timber.e("On RestoreState: %s", categoryName);
        }

        savedState = null;
    }

    @Override
    public void setFabAction() {
        enableFab();
        getFab().setOnClickListener(view -> {
            Timber.e("Relaying Category Name: %s", categoryName);
            ItemListFragmentDirections.RelayItemListToAddItemAction relayItemListToAddItemAction =
                    ItemListFragmentDirections.relayItemListToAddItemAction();
            relayItemListToAddItemAction.setCategory(categoryName);
            getNavController().navigate(relayItemListToAddItemAction);
        });
    }

    private void initObservers() {
        itemListViewModel.getItemPilesLiveData().observe(this.getViewLifecycleOwner(),
                itemPileArrayList -> {
                    if (itemPileArrayList != null) {
                        listTypeItems = itemPileArrayList;
                        itemListViewAdapter.setListTypeItems(listTypeItems);
                        itemListViewAdapter.notifyDataSetChanged();
                    }
                    Timber.e("List type items is null");
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("On Destroy mEvent: %s", categoryName);
        savedState = saveState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.e("On Save Instance: %s", categoryName);
        outState.putBundle(SAVED_STATE_KEY, (savedState != null) ? savedState : saveState());
    }

    private Bundle saveState() {
        Timber.e("On SaveState: %s", categoryName);
        Bundle newSaveState = new Bundle();
        newSaveState.putString(CATEGORY_KEY, categoryName);
        return newSaveState;
    }
}