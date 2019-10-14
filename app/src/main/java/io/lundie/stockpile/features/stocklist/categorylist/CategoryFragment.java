package io.lundie.stockpile.features.stocklist.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

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




    public CategoryFragment() { /* Required empty constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stockpile, container, false);

        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

    }
}
