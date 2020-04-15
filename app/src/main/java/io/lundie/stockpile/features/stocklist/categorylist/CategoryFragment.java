package io.lundie.stockpile.features.stocklist.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.databinding.FragmentCategoryBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;

/**
 * Fragment responsible for the display os categories
 */
public class CategoryFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecyclerView categoriesRecyclerView;
    private CategoriesViewNavAdapter categoriesViewAdapter;

    private CategoryViewModel categoryViewModel;
    private ArrayList<ItemCategory> itemCategories;

    public CategoryFragment() { /* Required clear constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentCategoryBinding binding =
                FragmentCategoryBinding.inflate(inflater, container, false);
        initRecyclerView(binding,Navigation.findNavController(container));
        initObservers();
        binding.setViewmodel(categoryViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void initRecyclerView(FragmentCategoryBinding binding, NavController navController) {
        categoriesRecyclerView = binding.categoryRv;
        if(getIsLandscape()) {
            categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        categoriesViewAdapter = new CategoriesViewNavAdapter(navController);
        categoriesViewAdapter.setCategoryList(itemCategories);
        categoriesRecyclerView.setAdapter(categoriesViewAdapter);
    }

    private void initViewModels() {
        categoryViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(CategoryViewModel.class);
    }

    private void initObservers() {
        categoryViewModel.getItemCategoriesData().observe(this.getViewLifecycleOwner(),
                itemCategories -> {
                    this.itemCategories = itemCategories;
                    categoriesViewAdapter.setCategoryList(itemCategories);
                    categoriesViewAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
