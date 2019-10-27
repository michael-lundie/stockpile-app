package io.lundie.stockpile.features.stocklist.categorylist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.databinding.FragmentCategoryBinding;
import io.lundie.stockpile.utils.BindingBaseAdapter;

/**
 *
 */
public class CategoryFragment extends DaggerFragment {

    private static final String LOG_TAG = CategoryFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecyclerView recyclerView;
    private CategoriesViewAdapter categoriesViewAdapter;

    private CategoryViewModel categoryViewModel;

    private ArrayList<ItemCategory> itemCategories;


    public CategoryFragment() { /* Required empty constructor */ }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CategoryViewModel.class);

        NavController navController = Navigation.findNavController(container);

        //TODO: Move RV creation to onActivityCreated
        FragmentCategoryBinding binding =
                FragmentCategoryBinding.inflate(inflater, container, false);
        recyclerView = binding.categoryRv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesViewAdapter = new CategoriesViewAdapter(itemCategories,
                itemName -> {
            //navController.
            Log.i(LOG_TAG, "Registering item clicked (frag): " + itemName);
        });

        recyclerView.setAdapter(categoriesViewAdapter);
        //categoriesViewAdapter.setCategoryList(itemCategories);
        setObserver();

        binding.setViewmodel(categoryViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());

        return binding.getRoot();
    }

    private void setObserver() {
        categoryViewModel.getItemTypes().observe(this.getViewLifecycleOwner(),
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
