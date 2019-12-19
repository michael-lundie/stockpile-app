package io.lundie.stockpile.features.stocklist.manageitem;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ExpiryPile;
import io.lundie.stockpile.databinding.FragmentAddItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.IMAGE_FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.SUCCESS;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.SUCCESS_NO_IMAGE;
import static io.lundie.stockpile.utils.AppUtils.calendarToString;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemFragment extends FeaturesBaseFragment {

    private final int GALLERY_REQUEST_CODE = 31415;

    private static final int MODE_EDIT = 0;
    private static final int MODE_ADD = 1;

    private int fragmentMode;
    private ManageItemViewModel manageItemViewModel;
    private ItemDateListViewAdapter itemPileExpiryDatesListViewAdapter;
    private Calendar calendar;
    private TextInputEditText dateEditText;
    private String category;
    private ArrayList<ExpiryPile> expiryPileItems;
    private RecyclerView expiryItemsRecycleView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public ManageItemFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        if (getArguments() != null) {
            category = ManageItemFragmentArgs.fromBundle(getArguments()).getCategory();
            if(category == null || category.isEmpty()) {
                // If there is no bundle arguments, we must be in edit mode. Handle nav appropriately
                // using this flag.
                fragmentMode = MODE_EDIT;
            } else {
                manageItemViewModel.setCategoryNameLiveData(category);
                fragmentMode = MODE_ADD;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        FragmentAddItemBinding binding = FragmentAddItemBinding.inflate(inflater, container, false);

        if(expiryPileItems == null) {
            expiryPileItems = new ArrayList<>();
        }

        itemPileExpiryDatesListViewAdapter = new ItemDateListViewAdapter(itemId -> {
            Timber.e("Remove expiry item: %s", itemId);
            manageItemViewModel.removeExpiryPileItem(itemId);
        });
        itemPileExpiryDatesListViewAdapter.setExpiryItems(expiryPileItems);
        expiryItemsRecycleView = binding.expiryItemPilesRv;
        expiryItemsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        expiryItemsRecycleView.setAdapter(itemPileExpiryDatesListViewAdapter);
        initObservers();
        binding.setViewmodel(manageItemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        setUpDateDialog(binding);
        return binding.getRoot();
    }

    private void initObservers() {
            manageItemViewModel.getExpiryPileMutableList().observe(this.getViewLifecycleOwner(),
                    expiryPileArrayList -> {
                        if (expiryPileArrayList != null) {
                            this.expiryPileItems = expiryPileArrayList;
                            itemPileExpiryDatesListViewAdapter.setExpiryItems(expiryPileItems);
                            itemPileExpiryDatesListViewAdapter.notifyDataSetChanged();
                        }
                        Timber.e("Expiry pile items is null");
                    });
    }

    private void setUpDateDialog(FragmentAddItemBinding binding) {
        calendar = Calendar.getInstance();
        dateEditText = binding.expiryDateEditText;
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField(calendar);
                };
        dateEditText.setOnClickListener(view -> {
            new DatePickerDialog(getActivity(),
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    private void updateDateField(Calendar calendar) {
        dateEditText.setText(calendarToString(calendar));
    }


    private void initViewModels() {
        manageItemViewModel = ViewModelProviders.of(this, viewModelFactory).get(ManageItemViewModel.class);
    }

    public void onAddImageClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            if(requestCode == GALLERY_REQUEST_CODE) {

                Uri selectedImage = data.getData();
                manageItemViewModel.setItemImageUri(selectedImage.toString());
            }
    }

    public void onAddItemClicked() {
        // Set up observer so we can post the results of add item in the UI.
        manageItemViewModel.getIsAddItemSuccessfulEvent().observe(this, statusEvent -> {
            switch (statusEvent.getErrorStatus()) {
                case (SUCCESS):
                case(SUCCESS_NO_IMAGE):
                    popNavigation(statusEvent.getEventText());
                    break;
                case(IMAGE_FAILED):
                    break;
                case(FAILED):
                    break;
            }
        });

        //Finally, initialise our add item process view the view model.
        manageItemViewModel.onAddItemClicked();
    }

    private void popNavigation(String eventMessage) {

        manageItemViewModel.getMessageController().setEventMessage(eventMessage);

        if(fragmentMode == MODE_EDIT) {
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.item_fragment_dest, true).build();
            getNavController().navigate(
                    ManageItemFragmentDirections
                            .manageItemToItemNavAction(), navOptions);
        } else {
            // This is navigation equivalent to popping the back-stack, preventing us
            // from being able to navigate back to the add item form.
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.item_list_fragment_dest, true).build();
            getNavController().navigate(
                    ManageItemFragmentDirections
                            .relayAddItemToItemListNavAction()
                            .setCategory(category),
                    navOptions);
        }
    }
}