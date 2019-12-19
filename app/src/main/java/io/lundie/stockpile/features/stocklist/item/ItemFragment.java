package io.lundie.stockpile.features.stocklist.item;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

/**
 * A simple {@link DaggerFragment} subclass.
 */
public class ItemFragment extends FeaturesBaseFragment {

    private static final String LOG_TAG = ItemFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ItemViewModel itemViewModel;
    private String itemName;

    public ItemFragment() { setHasOptionsMenu(true); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        if(getArguments() != null) {
            itemName = ItemFragmentArgs.fromBundle(getArguments()).getItemName();
//            itemViewModel.setItem(itemName);
        } else {
            //TODO: Handle this error on the front end.
            Timber.e("Error retrieving itemName to send to view model.");
        }

        FragmentItemBinding binding = FragmentItemBinding.inflate(inflater, container, false);
        binding.setViewmodel(itemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        requestItemObserver();
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_item_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Timber.i("ON OPTION SELECTED! %s, ", item);
        if(item.getItemId() == R.id.manage_item_fragment_dest) {
            getNavController().navigate(ItemFragmentDirections.itemToManageFragmentNavAction());
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestItemObserver() {

    }
}