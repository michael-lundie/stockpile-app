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
import io.lundie.stockpile.data.model.internal.ExpiryPile;
import io.lundie.stockpile.databinding.FragmentManageItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.general.AlertDialogFragment;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.IMAGE_FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.SUCCESS;
import static io.lundie.stockpile.utils.DateUtils.calendarToString;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemFragment extends FeaturesBaseFragment {

    private final int GALLERY_REQUEST_CODE = 31415;

    private static final int MODE_EDIT = 0;
    private static final int MODE_ADD = 1;

    private int fragmentMode;
    private ManageItemViewModel manageItemViewModel;
    private ManageItemDateListViewAdapter datesListViewAdapter;
    private Calendar calendar;
    private TextInputEditText dateEditText;
    private String category;
    private ArrayList<ExpiryPile> expiryPileItems;
    private RecyclerView expiryItemsRecycleView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public ManageItemFragment() { /* Required clear public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = ManageItemFragmentArgs.fromBundle(getArguments()).getCategory();
            if(category == null || category.isEmpty()) {
                // If there is no bundle arguments, we must be in edit mode. Handle nav appropriately
                // using this flag.
                fragmentMode = MODE_EDIT;
            } else {
                manageItemViewModel.setCategoryName(category);
                fragmentMode = MODE_ADD;
            }
        }
        initViewModels();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        FragmentManageItemBinding binding = FragmentManageItemBinding.inflate(inflater, container, false);

        if(expiryPileItems == null) {
            expiryPileItems = new ArrayList<>();
        }

        datesListViewAdapter = new ManageItemDateListViewAdapter(itemId -> {
            Timber.e("Remove expiry item: %s", itemId);
            manageItemViewModel.removeExpiryPileItem(itemId);
        });
        datesListViewAdapter.setExpiryItems(expiryPileItems);
        expiryItemsRecycleView = binding.expiryItemPilesRv;
        expiryItemsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        expiryItemsRecycleView.setAdapter(datesListViewAdapter);
        initObservers();
        binding.setViewmodel(manageItemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        setUpDateDialog(binding);
        return binding.getRoot();
    }

    private void initObservers() {
            manageItemViewModel.getPileExpiryList().observe(this.getViewLifecycleOwner(),
                    expiryPileArrayList -> {
                        if (expiryPileArrayList != null) {
                            this.expiryPileItems = expiryPileArrayList;
                            datesListViewAdapter.setExpiryItems(expiryPileItems);
                            datesListViewAdapter.notifyDataSetChanged();
                        }
                        Timber.e("Expiry pile items is null");
                    });
    }

    private void setUpDateDialog(FragmentManageItemBinding binding) {
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

                Uri selectedImage = null;
                if (data != null) {
                    selectedImage = data.getData();
                }

                if (selectedImage != null) {
                    manageItemViewModel.setItemImageUri(selectedImage.toString());
                }
            }
    }

    public void onAddItemClicked() {
        // Set up observer so we can post the results of add item in the UI.
        manageItemViewModel.getAddItemEvent().observe(getViewLifecycleOwner(), statusEvent -> {
            switch (statusEvent.getErrorStatus()) {
                case (SUCCESS):
                    popNavigation(statusEvent.getEventText());
                    break;
                case(IMAGE_FAILED):
                    //TODO: check offline and pop dialog fragment
                    showImageFailureDialog(statusEvent.getEventText());
                    break;
            }
        });

        //Finally, initialise our add item process view the view model.
        manageItemViewModel.onAddItemClicked();
    }

    private void showImageFailureDialog(String statusEventMsg) {
        AlertDialogFragment alertDialogFragment =
                AlertDialogFragment.newInstance(
                        getResources().getString(R.string.dialog_title_failed_image),
                        getResources().getString(R.string.dialog_label_failed_image),
                        getResources().getString(R.string.action_continue),
                        getResources().getString(R.string.action_try_again),
                        () -> onContinueConfirmed(statusEventMsg));
        alertDialogFragment.show(getChildFragmentManager(), "ImageFailureDialog");
    }

    private void onContinueConfirmed(String statusEventMsg) {
        popNavigation(statusEventMsg);
    }

    private void popNavigation(String statusEventMsg) {

        manageItemViewModel.getStatusController().setEventMessage(statusEventMsg);

        if(fragmentMode == MODE_EDIT) {
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.item_fragment_dest, true).build();
            getNavController().navigate(
                    ManageItemFragmentDirections
                            .manageItemToItemNavAction(manageItemViewModel.getItemName().getValue(),
                                    R.id.item_list_fragment), navOptions);
        } else {
            // This is navigation equivalent to popping the back-stack, preventing us
            // from being able to navigate back to the add item form.
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.item_list_fragment, true).build();
            getNavController().navigate(
                    ManageItemFragmentDirections
                            .relayAddItemToItemListNavAction()
                            .setCategory(category),
                    navOptions);
        }
    }
}