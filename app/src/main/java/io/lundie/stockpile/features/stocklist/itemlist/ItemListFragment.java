package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.data.ItemPile;
import io.lundie.stockpile.databinding.FragmentItemListBinding;
import io.lundie.stockpile.utils.data.FakeDataUtil;

/**
 *
 */
public class ItemListFragment extends DaggerFragment {

    private static final String LOG_TAG = ItemListFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseFirestore firestore;

    private ItemListViewModel itemListViewModel;
    private ItemListFirestorePagingAdapter itemListViewAdapter;

    private ArrayList<ItemPile> listTypeItems;
    private RecyclerView itemsRecyclerView;
    private String categoryName;

    public ItemListFragment() { /* Required empty constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "Viewmodel Factory is:" + viewModelFactory);
        Log.e(LOG_TAG, "Firestore is:" + firestore);
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
    public void onStart() {
        super.onStart();
        itemListViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemListViewAdapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentItemListBinding binding = FragmentItemListBinding.inflate(inflater, container, false);
        itemsRecyclerView = binding.listItemsRv;


        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(6)
                .build();

        CollectionReference itemsReference = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
                .collection("items");



        Query itemsQuery = itemsReference.orderBy("itemName", Query.Direction.ASCENDING).limit(15);

        FirestorePagingOptions options = new FirestorePagingOptions.Builder<ItemPile>()
                .setLifecycleOwner(this)
                .setQuery(itemsQuery, config, ItemPile.class)
                .build();

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //itemListViewAdapter = new Item(Navigation.findNavController(container));
        itemListViewAdapter = new ItemListFirestorePagingAdapter(options);

        itemsRecyclerView.setAdapter(itemListViewAdapter);

        //setObserver();
//        setupRecycleListPager();
        binding.setViewmodel(itemListViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());

        return binding.getRoot();
    }

//    private void setupRecycleListPager() {
//
//        ArrayList<ItemPile> itemPileArrayList = new ArrayList<>();
//
//        CollectionReference itemsReference = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
//                .collection("items");
//        Query itemsQuery = itemsReference.orderBy("itemName", Query.Direction.ASCENDING).limit(15);
//
//        itemsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        ItemPile itemPile = document.toObject(ItemPile.class);
//                        listTypeItems.add(itemPile);
//                    }
//                    itemListViewAdapter.notifyDataSetChanged();
//                    lastViewVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
//
//                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                            super.onScrollStateChanged(recyclerView, newState);
//                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                                isScrolling = true;
//                            }
//                        }
//                    }
//                }
//            }
//        })
//    }

//    private void setObserver() {
//        itemListViewModel.getListTypeItems().observe(this.getViewLifecycleOwner(),
//                listTypeItems -> {
//                    if(listTypeItems != null) {
//                        Log.e(LOG_TAG, "List type items:" + listTypeItems);
//                        this.listTypeItems = listTypeItems;
//                        itemListViewAdapter.setItemPiles(listTypeItems);
//                        itemListViewAdapter.notifyDataSetChanged();
//                    }
//                });
//    }
}