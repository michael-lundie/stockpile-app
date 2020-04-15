package io.lundie.stockpile.features.homeview;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.adapters.PagingAdapterListener;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PagingArrayStatusType;
import io.lundie.stockpile.databinding.FragmentExpiringItemsBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.utils.views.RecycleViewWithSetEmpty;

/**
 * Fragment displays a paged list of expiring items.
 */
public class ExpiringItemsFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;

    private ExpiringItemsViewRecycleAdapter navAdapter;
    private RecycleViewWithSetEmpty expiringItemsRecyclerView;
    private View emptyRecyclerView;
    private ArrayList<ItemPile> expiringItemsList = new ArrayList<>();

    private boolean isLoading;
    private boolean hasStoppedPaging = false;

    public ExpiringItemsFragment() { /* Required empty public constructor */ }

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
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        homeViewModel.broadcastToWidget(false);
    }

    private void initAdapter(NavController navController) {
        expiringItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        expiringItemsRecyclerView.setEmptyView(emptyRecyclerView);
        navAdapter = new ExpiringItemsViewRecycleAdapter(navController);
        navAdapter.setExpiringItemsList(expiringItemsList);
        navAdapter.setPagingListener(new PagingAdapterListener() {
            @Override
            public void onObjectClicked(ItemPile itemPile) { }

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

    @Override
    public void onStop() {
        homeViewModel.getPagingExpiryList().removeObservers(this.getViewLifecycleOwner());
        homeViewModel.getPagingEvents().removeObservers(this.getViewLifecycleOwner());
        super.onStop();
    }

    private void initObservers() {
        homeViewModel.getPagingExpiryList().observe(this.getViewLifecycleOwner(),
                expiringItemsList -> {
                    if(expiringItemsList != null) {
                        this.expiringItemsList = expiringItemsList;
                        navAdapter.setExpiringItemsList(this.expiringItemsList);
                        homeViewModel.setIsExpiringItemsLoading(false);
                        navAdapter.notifyDataSetChanged();
                        homeViewModel.broadcastToWidget(false);
                    }
                });

        homeViewModel.getPagingEvents().observe(this.getViewLifecycleOwner(), event -> {
            if(event != null) {
                switch (event.getPagingStatus()) {
                    case PagingArrayStatusType.LOAD_STOP:
                        hasStoppedPaging = true;
                        homeViewModel.getPagingEvents().removeObservers(this.getViewLifecycleOwner());
                        break;
                    case PagingArrayStatusType.LOAD_FAIL:
                        isLoading = true;
                        break;
                    case PagingArrayStatusType.LOAD_SUCCESS:
                        isLoading = false;
                        break;
                }
            }
        });
    }

    private void initViewModels() {
        homeViewModel = new ViewModelProvider(this, viewModelFactory).get(HomeViewModel.class);
    }
}