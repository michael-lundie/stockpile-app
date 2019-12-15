package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.databinding.FragmentItemListBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

/**
 *
 */
public class ItemListFragment extends FeaturesBaseFragment {

    private static final String LOG_TAG = ItemListFragment.class.getSimpleName();

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

    public ItemListFragment() { /* Required empty constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("Viewmodel Factory is:%s", viewModelFactory);
        itemListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemListViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(inflater, container, false);

        restoreState(savedInstanceState);

        if (listTypeItems == null) {
            listTypeItems = new ArrayList<>();
        }

        String eventMessage = itemListViewModel.getMessageController().getEventMessage();
        if (eventMessage != null) {
            Toast.makeText(getContext(), eventMessage, Toast.LENGTH_SHORT).show();
        }

        if (getArguments() != null) {

            categoryName = ItemListFragmentArgs.fromBundle(getArguments()).getCategory();
            Timber.e("Category is:%s", categoryName);
            itemListViewModel.setCategory(categoryName);

//            String eventString = ItemListFragmentArgs.fromBundle(getArguments()).getEventString();
//            int eventId = ItemListFragmentArgs.fromBundle(getArguments()).getAppEventId();
//            Timber.d("mEventId is: %s . eventId is: %s", mEventId, eventId);
//            if (mEventId == eventId && eventString != null && !eventString.isEmpty()) {
//                Toast.makeText(getContext(), eventString, Toast.LENGTH_SHORT).show();
//                mEventId = AppUtils.generateEventId(mEventId);
//            }
        } else {
            //TODO: Handle this error on the front end.
            Timber.e("Error retrieving category to send to view model.");
        }

        setNavController(container);
        itemsRecyclerView = binding.listItemsRv;
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemListViewAdapter = new ItemListViewAdapter(getNavController());
        itemListViewAdapter.setListTypeItems(listTypeItems);
        itemsRecyclerView.setAdapter(itemListViewAdapter);
        initObservers();
        binding.setViewmodel(itemListViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();
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

//        itemListViewModel.getAddItemNavEvent().observe(this.getViewLifecycleOwner(),
//                categoryString -> {
//                    ItemListFragmentDirections.RelayItemListToAddItemAction relayItemListToAddItemAction =
//                            ItemListFragmentDirections.relayItemListToAddItemAction();
//                    relayItemListToAddItemAction.setCategory(categoryString);
//                    navController.navigate(relayItemListToAddItemAction);
//        });
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