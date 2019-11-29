package io.lundie.stockpile.features.stocklist.categorylist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.databinding.FragmentCategoryBinding;

/**
 *
 */
public class CategoryFragment extends DaggerFragment {

    private static final String LOG_TAG = CategoryFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecyclerView categoriesRecyclerView;
    private CategoriesViewAdapter categoriesViewAdapter;

    private CategoryViewModel categoryViewModel;
    private ArrayList<ItemCategory> itemCategories;

    public CategoryFragment() { /* Required empty constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_TAG, "Viewmodel Factory is:" + viewModelFactory);

        FragmentCategoryBinding binding =
                FragmentCategoryBinding.inflate(inflater, container, false);
        initRecyclerView(binding,Navigation.findNavController(container));
        setObserver();
        binding.setViewmodel(categoryViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void initRecyclerView(FragmentCategoryBinding binding, NavController navController) {
        categoriesRecyclerView = binding.categoryRv;
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesViewAdapter = new CategoriesViewAdapter(navController);
        categoriesViewAdapter.setCategoryList(itemCategories);
        categoriesRecyclerView.setAdapter(categoriesViewAdapter);
    }

    private void initViewModels() {
        categoryViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CategoryViewModel.class);
    }

    private void setObserver() {
        categoryViewModel.getItemCategories().observe(this.getViewLifecycleOwner(),
                itemCategories -> {
                    this.itemCategories = itemCategories;
                    categoriesViewAdapter.setCategoryList(itemCategories);
                    //TODO: I don't think we need notify changed here.
                    categoriesViewAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
