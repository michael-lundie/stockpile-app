package io.lundie.stockpile.features.stocklist.itemlist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import io.lundie.stockpile.R;

/**
 *
 */
public class ItemListFragment extends Fragment {

    private static final String LOG_TAG = ItemListFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ItemListViewModel listViewModel;

    public ItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

}
