package io.lundie.stockpile.features.stocklist.manageitem;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ExpiryPile;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.data.CounterType;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.*;
import static io.lundie.stockpile.utils.DateUtils.convertDatesToExpiryPiles;
import static io.lundie.stockpile.utils.DateUtils.convertExpiryPilesToDates;
import static io.lundie.stockpile.utils.DateUtils.orderDateArrayListAscending;
import static io.lundie.stockpile.utils.AppUtils.validateInput;

/**
 * ManageItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input. Handles both Add and Edit modes.
 * <p>
 * TODO: Bugs:
 * https://github.com/material-components/material-components-android/issues/525
 */
public class ManageItemViewModel extends FeaturesBaseViewModel {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Resources resources = this.getApplication().getResources();
    private MediatorLiveData<List<String>> categoryNameList = new MediatorLiveData<>();
    private String initialDocumentName;
    private int initialCalorieTotal;
    private boolean isEditMode = false;

    private int expiryPileIdCounter = 0;
    private boolean isItemNameError = true;
    private boolean isPileTotalItemsError = true;
    private boolean isItemCaloriesError = true;

    private MutableLiveData<String> addEditIconButtonText = new MutableLiveData<>();

    private MutableLiveData<String> currentImageUri = new MutableLiveData<>();

    private MutableLiveData<String> itemName = new MutableLiveData<>();
    private MutableLiveData<String> categoryName = new MutableLiveData<>();
    private MutableLiveData<String> itemImageUri = new MutableLiveData<>();

    private MutableLiveData<String> newPileQuantity = new MutableLiveData<>();
    private MutableLiveData<String> newPileExpiryDate = new MutableLiveData<>();
    private MediatorLiveData<String> pileTotalCalories = new MediatorLiveData<>();
    private MutableLiveData<String> itemCalories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ExpiryPile>> pileExpiryList = new MutableLiveData<>();

    private MediatorLiveData<String> itemNameErrorText = new MediatorLiveData<>();
    private MediatorLiveData<String> newPileQuantityErrorText = new MediatorLiveData<>();
    private MutableLiveData<Boolean> isExpiryEntryError = new MutableLiveData<>(false);
    private MediatorLiveData<String> itemCaloriesErrorText = new MediatorLiveData<>();
    private MediatorLiveData<String> itemExpiryErrorText = new MediatorLiveData<>();

    private MutableLiveData<Boolean> isUsingCurrentImage = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isAttemptingUpload = new MutableLiveData<>(false);
    private SingleLiveEvent<ManageItemStatusEventWrapper> isAddItemSuccessfulEvent = new SingleLiveEvent<>();

    @Inject
    ManageItemViewModel(@NonNull Application application, ItemRepository itemRepository,
                        UserRepository userRepository) {
        super(application);
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

//        ArrayList<ItemCategory> itemCategories = userRepository.getCategoryData().getValue();
//
//        if (itemCategories != null) {
//            setCategoryNameList(itemCategories);
//        }
        addCategoryItemsLiveDataSource();
        initItemNameValidation();
        initItemCountValidation();
        initItemCaloriesValidation();
        initTotalCaloriesCounter();
    }

    private void addCategoryItemsLiveDataSource() {
        if(userRepository.getUserDataSnapshot(getUserID()) != null) {
            UserData data = userRepository.getUserDataSnapshot(getUserID());
            ArrayList<String> list = new ArrayList<>();
            for (ItemCategory category : data.getCategories()) {
                list.add(category.getCategoryName());
            }
            Timber.e("UserData --> Returning from RECENT SNAPSHOT: %s", list);
            categoryNameList.setValue(list);
        } else
        if(userRepository.getUserDocSnapshotLiveData() != null) {
            categoryNameList.addSource(userRepository.getUserDocSnapshotLiveData(), snapshot -> {
                if(snapshot != null) {
                    UserData data = snapshot.toObject(UserData.class);
                    if(data != null) {
                        ArrayList<String> list = new ArrayList<>();
                        for (ItemCategory category : data.getCategories()) {
                            list.add(category.getCategoryName());
                        }
                        Timber.e("UserData --> Returning from LIVE: %s", list);
                        categoryNameList.setValue(list);
                    }
                }
            });
        }
        Timber.e("UserData: Current Category Name : %s", getCategoryName().getValue());
    }

    /**
     * This method returns an itemPile fetched from the {@link ItemPileBus} if one exists.
     * This will in turn trigger the 'edit' mode of ItemManager.
     * @param itemPileBus
     */
    @Override
    public void onItemPileBusInjected(ItemPileBus itemPileBus) {
        if(itemPileBus.getItemPile() != null) {
            isEditMode = true;
            addEditIconButtonText.setValue(getApplication().getResources().getString(R.string.edit_item));
            ItemPile itemPile = itemPileBus.getItemPile();

            initialDocumentName = itemPile.getItemName();
            initialCalorieTotal = itemPile.getCalories() * itemPile.getItemCount();
            currentImageUri.setValue(itemPile.getImageURI());
            categoryName.setValue(itemPile.getCategoryName());
//            setCategoryName(itemPile.getCategoryName());
            Timber.e("UserData: inject; Current Category Name : %s", getCategoryName().getValue());
            itemName.setValue(itemPile.getItemName());
            pileExpiryList.setValue(convertDatesToExpiryPiles(itemPile.getExpiry()));
            itemCalories.setValue(String.valueOf(itemPile.getCalories()));
        }

        if(currentImageUri.getValue() != null && !currentImageUri.getValue().isEmpty()) {
            isUsingCurrentImage.setValue(true);
        }
    }

    private void initItemNameValidation() {
        itemNameErrorText.addSource(itemName, string -> {

            String errorText = validateInput("[\\p{P}\\p{S}]", string, 5,
                    "No special characters");
            if (errorText != null) {
                itemNameErrorText.setValue(errorText);
                isItemNameError = true;
            } else {
                if (isItemNameError) {
                    // Boolean check prevents null value for being set more than once -
                    // triggering a bug in the material design component. (see class docs above)
                    itemNameErrorText.setValue(null);
                }
                isItemNameError = false;
            }
        });
    }

    private void initItemCountValidation() {
        newPileQuantityErrorText.addSource(newPileQuantity, string -> {
            String errorText = validateInput("[^0-9]", string, 0,
                    "0-9 only");
            if (errorText != null && pileExpiryList.getValue() == null) {
                newPileQuantityErrorText.setValue(errorText);
                isPileTotalItemsError = true;
            } else {
                if (isPileTotalItemsError) {
                    newPileQuantityErrorText.setValue(null);
                }
                isPileTotalItemsError = false;
            }
        });
    }

    private void initItemCaloriesValidation() {
        itemCaloriesErrorText.addSource(itemCalories, string -> {
            String errorText = validateInput("[^0-9]", string, 1,
                    "0-9 only");
            if (errorText != null) {
                itemCaloriesErrorText.setValue(errorText);
                isItemCaloriesError = true;
            } else {
                if (isItemCaloriesError) {
                    itemCaloriesErrorText.setValue(null);
                }
                isItemCaloriesError = false;
            }
        });
    }

    private void initTotalCaloriesCounter() {
        pileTotalCalories.addSource(pileExpiryList, list -> {
            if(!isItemCaloriesError && itemCalories.getValue() != null && !list.isEmpty()) {
                pileTotalCalories.setValue(Integer.toString(
                        Integer.parseInt(itemCalories.getValue()) * getTotalExpiryPileItems(list)));
            } else {
                pileTotalCalories.setValue("");
            }
        });

        pileTotalCalories.addSource(itemCalories, string -> {
            if(string.equals("")) {
                pileTotalCalories.setValue("");
            } else
            if(!isItemCaloriesError && pileExpiryList.getValue() != null) {
                pileTotalCalories.setValue(Integer.toString(
                        Integer.parseInt(string) * getTotalExpiryPileItems(pileExpiryList.getValue())));
            }
        });
    }

    public MutableLiveData<String> getAddEditIconButtonText() { return addEditIconButtonText; }

    public MutableLiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemNameErrorText() {
        return itemNameErrorText;
    }

    public MutableLiveData<String> getCategoryName() {
        return categoryName;
    }

    void setCategoryName(String string) {
        categoryName.setValue(string);
    }

    public LiveData<List<String>> getCategoryNameList() {
        Timber.e("#ItemCat --> Getter: Category List is retrieving as: %s", categoryNameList);
        return categoryNameList;
    }

//    private void setCategoryNameList(ArrayList<ItemCategory> itemCategories) {
//        ArrayList<String> list = new ArrayList<>();
//        for (ItemCategory category : itemCategories) {
//            list.add(category.getCategoryName());
//        }
//        Timber.e("#ItemCat --> Setter: Category List is setting as: %s", list);
//        this.categoryNameList = list;
//    }

    public MutableLiveData<String> getNewPileExpiryDate() {
        return newPileExpiryDate;
    }

    public LiveData<String> getItemExpiryErrorText() {
        return itemExpiryErrorText;
    }

    LiveData<ArrayList<ExpiryPile>> getPileExpiryList() {
        if (pileExpiryList.getValue() == null) {
            ArrayList<ExpiryPile> expiryPiles = new ArrayList<>();
            pileExpiryList.setValue(expiryPiles);
        }
        return pileExpiryList;
    }

    public LiveData<Boolean> isExpiryEntryError() { return isExpiryEntryError; }

    void removeExpiryPileItem(int itemId) {
        ArrayList<ExpiryPile> expiryPiles = pileExpiryList.getValue();
        if (expiryPiles != null) {
            for (ExpiryPile e : expiryPiles) {
                if (e.getItemId() == itemId) {
                    expiryPiles.remove(e);
                    break;
                }
            }
            pileExpiryList.setValue(expiryPiles);
        }
    }

    public MutableLiveData<String> getNewPileQuantity() {
        return newPileQuantity;
    }

    public LiveData<String> getNewPileQuantityErrorText() {
        return newPileQuantityErrorText;
    }

    public MutableLiveData<String> getItemCalories() {
        return itemCalories;
    }

    public LiveData<String> getItemCaloriesErrorText() {
        return itemCaloriesErrorText;
    }

    public LiveData<String> getTotalCaloriesLiveData() {
        return pileTotalCalories;
    }


    SingleLiveEvent<ManageItemStatusEventWrapper> getIsAddItemSuccessfulEvent() {
        return isAddItemSuccessfulEvent;
    }

    public LiveData<String> getItemImageUri() {
        return itemImageUri;
    }

    void setItemImageUri(String uri) {
        itemImageUri.postValue(uri);
        isUsingCurrentImage.setValue(false);
    }

    public LiveData<String> getCurrentImageUri() {
        return currentImageUri;
    }

    public LiveData<Boolean> getIsUsingCurrentImage() { return isUsingCurrentImage;}

    public LiveData<Boolean> getIsAttemptingUpload() {
        return isAttemptingUpload;
    }

    public void onAddExpiryPileClicked() {
        String date = getNewPileExpiryDate().getValue();
        Timber.e("Quantity is: %s", newPileQuantity.getValue());

        if(newPileQuantity.getValue() == null || newPileQuantity.getValue().isEmpty()) {
            newPileQuantityErrorText.setValue("Required.");
        } else if (getNewPileExpiryDate().getValue() == null || getNewPileExpiryDate().getValue().isEmpty()) {
            itemExpiryErrorText.setValue("Required.");
        } else {
            int quantity = Integer.parseInt(newPileQuantity.getValue());

            ExpiryPile newExpiryPile = new ExpiryPile(date, quantity, expiryPileIdCounter);
            expiryPileIdCounter++;
            ArrayList<ExpiryPile> list = pileExpiryList.getValue();

            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(newExpiryPile);
            pileExpiryList.setValue(list);
            newPileExpiryDate.setValue(null);
            newPileQuantity.setValue(null);
            Timber.e("Expiry is: %s. Quantity is: %s", date, quantity);
            isExpiryEntryError.setValue(false);
        }
    }


    void onAddItemClicked() {

        if (areAllInputsValid()) {

            ArrayList<Date> expiryList = convertExpiryPilesToDates(pileExpiryList.getValue());

            ItemPile newItem = new ItemPile();
            newItem.setItemName(itemName.getValue());
            newItem.setCategoryName(categoryName.getValue());
            newItem.setItemCount(expiryList.size());
            newItem.setCalories(Integer.parseInt(itemCalories.getValue()));
            newItem.setCounterType(CounterType.GRAMS);
            newItem.setQuantity(0);
            newItem.setExpiry(orderDateArrayListAscending(expiryList));

            int newCalorieTotal = newItem.getItemCount() * newItem.getCalories();
            int totalChangeInCalories = newCalorieTotal - initialCalorieTotal;

            if (!getUserID().isEmpty()) {
                isAttemptingUpload.setValue(true);
                if(!isEditMode) {
                    itemRepository.setItem(getUserID(), getItemImageUri().getValue(), newItem,
                            addItemStatus -> handleItemUploadStatusEvents(totalChangeInCalories,
                                    addItemStatus, newItem));
                } else {
                    itemRepository.setItem(getUserID(), getItemImageUri().getValue(),
                            newItem, initialDocumentName,
                            addItemStatus -> handleItemUploadStatusEvents(totalChangeInCalories,
                                    addItemStatus, newItem));
                }
            }
        }
    }

    private void handleItemUploadStatusEvents(int totalChangeInCalories,
                                              int addItemStatus, ItemPile newItemPile) {
        if (addItemStatus != 0) {
            if (addItemStatus != ADDING_ITEM) {
                if(addItemStatus != FAILED) {
                    updateItemPileBus(newItemPile);
                    userRepository.updateTotalCalories(getUserID(), categoryName.getValue(), totalChangeInCalories);
                }
                isAttemptingUpload.setValue(false);
                postAddItemSuccessfulEvent(addItemStatus, getEventMessage(addItemStatus));
            } else {
                //TODO: handle delayed upload status
            }
        }
    }

    private String getEventMessage(@AddItemStatusTypeDef int addItemStatus) {
        switch (addItemStatus) {
            case (SUCCESS):
                return resources.getString(R.string.im_event_success);
            case (SUCCESS_NO_IMAGE):
                return resources.getString(R.string.im_event_no_image);
            case (IMAGE_FAILED):
                return resources.getString(R.string.im_event_image_failed);
            case (FAILED):
                return resources.getString(R.string.im_event_failed);
        }
        return "";
    }

    private void postAddItemSuccessfulEvent(@AddItemStatusTypeDef int status, String eventMessage) {
        ManageItemStatusEventWrapper statusEvent = new ManageItemStatusEventWrapper();
        statusEvent.setErrorStatus(status);
        statusEvent.setEventText(eventMessage);
        isAddItemSuccessfulEvent.postValue(statusEvent);
    }

    private boolean areAllInputsValid() {
        boolean isInputValid = true;

        if (isItemNameError) {
            itemNameErrorText.setValue("Error.");
            isInputValid = false;
        }

        if (isItemCaloriesError) {
            itemCaloriesErrorText.setValue("Error");
            isInputValid = false;
        }

        if(pileExpiryList.getValue() == null || pileExpiryList.getValue().isEmpty()) {
            isExpiryEntryError.setValue(true);
            isInputValid = false;
        }

        return isInputValid;
    }

    private static int getTotalExpiryPileItems(ArrayList<ExpiryPile> expiryPileArrayList) {
        int totalItems = 0;
        for (ExpiryPile e: expiryPileArrayList) {
            totalItems += e.getItemCount();
        } return  totalItems;
    }
}