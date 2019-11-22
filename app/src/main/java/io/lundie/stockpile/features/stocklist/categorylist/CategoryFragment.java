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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
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
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_TAG, "Viewmodel Factory is:" + viewModelFactory);
        categoryViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CategoryViewModel.class);

        //TODO: Move RV creation to onActivityCreated
        FragmentCategoryBinding binding =
                FragmentCategoryBinding.inflate(inflater, container, false);
        categoriesRecyclerView = binding.categoryRv;
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesViewAdapter = new CategoriesViewAdapter(Navigation.findNavController(container));
        categoriesViewAdapter.setCategoryList(itemCategories);

        categoriesRecyclerView.setAdapter(categoriesViewAdapter);
        //categoriesViewAdapter.setCategoryList(itemCategories);
        setObserver();

        binding.setViewmodel(categoryViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());

        return binding.getRoot();
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
