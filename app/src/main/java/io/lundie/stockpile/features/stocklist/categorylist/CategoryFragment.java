package io.lundie.stockpile.features.stocklist.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CategoryFragment extends DaggerFragment {

    private static final String LOG_TAG = CategoryFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;


    public CategoryFragment() { /* Required empty constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryViewModel = ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel.class);


        return inflater.inflate(R.layout.fragment_category, container, false);



    }
}
