package io.lundie.stockpile.features.homeview;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.adapters.PagingAdapterListener;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.databinding.FragmentExpiringItemsBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.utils.RecycleViewWithSetEmpty;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpiringItemsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;

    private ExpiringItemsViewNavAdapter navAdapter;
    private RecycleViewWithSetEmpty expiringItemsRecyclerView;
    private View emptyRecyclerView;
    private ArrayList<ItemPile> expiringItemsList;

    private boolean isLoading;
    private boolean hasStoppedPaging = false;

    public ExpiringItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentExpiringItemsBinding binding = FragmentExpiringItemsBinding.inflate(inflater, container, false);
        expiringItemsRecyclerView = binding.expiringItemsRv;
        emptyRecyclerView = binding.emptyView;
        initAdapter(Navigation.findNavController(container));
        initObservers();
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //initScrollListener();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    private void initAdapter(NavController navController) {
        expiringItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        expiringItemsRecyclerView.setEmptyView(emptyRecyclerView);
        navAdapter = new ExpiringItemsViewNavAdapter(navController);
        navAdapter.setExpiringItemsList(expiringItemsList);
        navAdapter.setPagingListener(new PagingAdapterListener() {
            @Override
            public void onObjectClicked(ItemPile itemPile) {
                homeViewModel.updateItemPileBus(itemPile);

            }

            @Override
            public void onLoadMore() {
                if(!isLoading || !hasStoppedPaging) {
                    homeViewModel.loadNextExpiryListPage();
                    isLoading = true;
                }
            }
        });
        expiringItemsRecyclerView.setAdapter(navAdapter);
    }

    private void initObservers() {
        homeViewModel.getPagingExpiryList().observe(this.getViewLifecycleOwner(),
                expiringItemsList -> {
                    if(expiringItemsList != null) {
                        this.expiringItemsList = expiringItemsList;
                        Timber.e("Paging -->  Setting expiring items!");
                        navAdapter.setExpiringItemsList(this.expiringItemsList);
                        navAdapter.notifyDataSetChanged();
                    }
                });

        homeViewModel.getPagingEvents().observe(this.getViewLifecycleOwner(), singleEvent -> {
            if(singleEvent != null) {
                switch (singleEvent.getPagingStatus()) {
                    case PagingArrayStatusType.LOAD_STOP:
                        Timber.e("Paging -->  STOP RECEIVED");
                        hasStoppedPaging = true;
                        break;
                    case PagingArrayStatusType.LOAD_FAIL:
                        Timber.e("Paging --> FAILED TO LOAD!");
                        isLoading = true;
                        break;
                    case PagingArrayStatusType.LOAD_SUCCESS:
                        Timber.e("Paging --> Load Success! Setting continue.");
                        isLoading = false;
                        break;
                }
            }
        });
    }

    private void initViewModels() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    private void initScrollListener() {
        expiringItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    if(!isLoading) {
                        homeViewModel.loadNextExpiryListPage();
                        isLoading = true;
                    }

                }
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (!isLoading) {
//                    if (linearLayoutManager != null &&
//                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == expiringItemsList.size() - 1) {
//                        //bottom of list!
//                        Timber.e("Paging --> Recycler at bottom of layout - LOADING NEXT.");
//                        homeViewModel.loadNextExpiryListPage();
//                        isLoading = true;
//                    }
//                }
            }
        });
    }
}
