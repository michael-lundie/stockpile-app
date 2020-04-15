package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.databinding.FragmentItemListBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.utils.Prefs;
import io.lundie.stockpile.utils.views.RecycleViewWithSetEmpty;
import timber.log.Timber;

/**
 *
 */
public class ItemListFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Prefs prefs;

    private static String SAVED_STATE_KEY = "item_list_state";
    private static String CATEGORY_KEY = "category";

    private ItemListViewModel itemListViewModel;
    private ItemListViewRecycleAdapter itemListViewRecycleAdapter;
    private TextView emptyRecyclerView;
    private ArrayList<ItemPile> listTypeItems;
    private RecycleViewWithSetEmpty itemsRecyclerView;
    private String categoryName;
    private Bundle savedState = null;

    public ItemListFragment() { /* Required clear constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemListViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(inflater, container, false);

        restoreState(savedInstanceState);
        setNavController(container);

        if (listTypeItems == null) {
            listTypeItems = new ArrayList<>();
        }

        if (getArguments() != null) {
            categoryName = ItemListFragmentArgs.fromBundle(getArguments()).getCategory();
            itemListViewModel.setCategory(categoryName);
        } else {
            Timber.e("Error retrieving category to send to view model.");
        }

        initRecyclerView(binding);
        initObservers();
        binding.setViewmodel(itemListViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
    }

    private void initRecyclerView(FragmentItemListBinding binding) {
        itemsRecyclerView = binding.listItemsRv;
        emptyRecyclerView = binding.emptyView;
        int spanCount;
        if(getIsLandscape()) {
            spanCount = 3;
        } else {
            spanCount = 2;
        }
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        itemListViewRecycleAdapter = new ItemListViewRecycleAdapter(this::navigateToRequestedItem);
        itemsRecyclerView.setEmptyView(emptyRecyclerView);
        itemListViewRecycleAdapter.setListTypeItems(listTypeItems);
        itemsRecyclerView.setAdapter(itemListViewRecycleAdapter);
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
        }
        savedState = null;
    }

    @Override
    public void setFabAction() {
        enableFab();
        getFab().setOnClickListener(view -> {
            getNavController().navigate(ItemListFragmentDirections
                    .relayItemListToAddItemAction()
                    .setCategory(categoryName)
                    .setIsEditMode(false));
        });
    }

    private void initObservers() {
        itemListViewModel.getItemPilesLiveData().observe(this.getViewLifecycleOwner(),
                itemPileArrayList -> {
                    if (itemPileArrayList != null) {
                        listTypeItems = itemPileArrayList;
                        itemListViewRecycleAdapter.setListTypeItems(listTypeItems);
                        itemListViewRecycleAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(SAVED_STATE_KEY, (savedState != null) ? savedState : saveState());
    }

    private Bundle saveState() {
        Bundle newSaveState = new Bundle();
        newSaveState.putString(CATEGORY_KEY, categoryName);
        return newSaveState;
    }
}