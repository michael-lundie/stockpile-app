package io.lundie.stockpile.features.stocklist.manageitem;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.internal.ExpiryPile;
import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.model.firestore.UserData;
import io.lundie.stockpile.data.model.internal.ItemPileRef;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.data.repository.TargetsRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.utils.DataUtils;
import io.lundie.stockpile.utils.NetworkUtils;
import io.lundie.stockpile.utils.Prefs;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.data.CounterType;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.*;
import static io.lundie.stockpile.utils.DateUtils.convertDatesToExpiryPiles;
import static io.lundie.stockpile.utils.DateUtils.convertExpiryPilesToDates;
import static io.lundie.stockpile.utils.DateUtils.orderDateArrayListAscending;
import static io.lundie.stockpile.utils.ValidationUtils.*;
import static io.lundie.stockpile.utils.ValidationUtils.validateInput;
import static io.lundie.stockpile.utils.ValidationUtilsErrorType.*;

/**
 * ManageItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input. Handles both Add and Edit modes.
 *
 */
public class ManageItemViewModel extends FeaturesBaseViewModel {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final TargetsRepository targetsRepository;
    private final Prefs prefs;
    private final DataUtils dataUtils;
    private final NetworkUtils networkUtils;

    private Resources resources = this.getApplication().getResources();
    private MediatorLiveData<List<String>> categoryNameList = new MediatorLiveData<>();
    private ItemPileRef itemPileRef;
    private boolean isEditMode = false;

    private String selectedUriOnResume;
    private int expiryPileIdCounter = 0;
    private boolean isItemNameError = true;
    private boolean isPileTotalItemsError = true;
    private boolean isItemCaloriesError = true;

    private MutableLiveData<String> addEditIconButtonText = new MutableLiveData<>();

    private MutableLiveData<String> currentImagePath = new MutableLiveData<>();

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
    private SingleLiveEvent<ManageItemStatusEventWrapper> addItemEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> isOfflineOnImageSubmission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> isAddItemSuccessful = new SingleLiveEvent<>();

    @Inject
    ManageItemViewModel(@NonNull Application application, ItemRepository itemRepository,
                        UserRepository userRepository, TargetsRepository targetsRepository,
                        Prefs prefs, DataUtils dataUtils, NetworkUtils networkUtils) {
        super(application);
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.targetsRepository = targetsRepository;
        this.prefs = prefs;
        this.dataUtils = dataUtils;
        this.networkUtils = networkUtils;

        addCategoryItemsLiveDataSource();
        initItemNameValidation();
        initItemCountValidation();
        initItemCaloriesValidation();
        initTotalCaloriesCounter();
    }

    private void addCategoryItemsLiveDataSource() {
        categoryNameList.addSource(userRepository.getUserDocumentRealTimeData(), snapshot -> {
            if(snapshot != null) {
                UserData data = snapshot.toObject(UserData.class);
                if(data != null) {
                    ArrayList<String> list = new ArrayList<>();
                    for (ItemCategory category : data.getCategories()) {
                        list.add(category.getCategoryName());
                    }
                    categoryNameList.setValue(list);
                }
            }
        });
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
            addEditIconButtonText.setValue(resources.getString(R.string.edit_item));
            ItemPile itemPile = itemPileBus.getItemPile();
            buildItemPileReference(itemPile);
            restoreItemPileBoundData(itemPile);
        }
    }

    /**
     * Method to be called during fragment lifecycle {@link Fragment#onPause()}
     * Works in conjunction with {@link android.content.SharedPreferences} to save state within
     * viewmodel.
     * {@link DataUtils} methods are used to serialize data using gson.
     */
    void saveStateOnPause() {
        ItemPile itemPile = buildItemPileFromInput();
        prefs.setSavedStateJsonItemPile(dataUtils.serializeItemPileToJson(itemPile));
        prefs.setSavedStateIsEdit(isEditMode);
        if(selectedUriOnResume != null) {
            prefs.setSavedStateSelectedUri(selectedUriOnResume);
        }
        if(isEditMode) {
            prefs.setSavedStateJsonItemRef(dataUtils.serializeItemPileRefToJson(itemPileRef));
        }
    }

    /**
     * Method to be called during fragment lifecycle {@link Fragment#onResume()}
     * Works in conjunction with {@link android.content.SharedPreferences} to restore saved data.
     *      * {@link DataUtils} methods are used to deserialize data using gson.
     */
    void restoreOnResume() {
        String itemPileJson = prefs.getSavedStateJsonItemPile();
        if(itemPileJson != null) {
            restoreItemPileBoundData(dataUtils.deserializeToItemPile(itemPileJson));
            isEditMode = prefs.getSavedStateIsEditMode(isEditMode);
        }
        if(selectedUriOnResume != null) {
            prefs.setSavedStateSelectedUri(selectedUriOnResume);
        }
        if(isEditMode) {
            String itemPileRefJson = prefs.getSavedStateJsonItemRef();
            if(itemPileRefJson != null) {
                itemPileRef = dataUtils.deserializeToItemPileRef(itemPileRefJson);
            }
        }
        // Restore any user selected image that has not yet been committed
        selectedUriOnResume = prefs.getSavedStateSelectedUri();
        if(selectedUriOnResume != null && !selectedUriOnResume.isEmpty()) {
            setUserSelectedImageUri(selectedUriOnResume);
            selectedUriOnResume = null;
        }
    }

    private void buildItemPileReference(ItemPile itemPile) {
        itemPileRef = new ItemPileRef();
        itemPileRef.setItemName(itemPile.getItemName());
        itemPileRef.setCaloriesTotal(itemPile.getCalories() * itemPile.getItemCount());
        itemPileRef.setItemCount(itemPile.getItemCount());
        itemPileRef.setImagePath(itemPile.getImagePath());
        itemPileRef.setImageStatus(itemPile.getImageStatus());
    }

    private void restoreItemPileBoundData(ItemPile itemPile) {
        currentImagePath.setValue(itemPile.getImagePath());
        categoryName.setValue(itemPile.getCategoryName());
        itemName.setValue(itemPile.getItemName());
        pileExpiryList.setValue(convertDatesToExpiryPiles(itemPile.getExpiry()));
        itemCalories.setValue(String.valueOf(itemPile.getCalories()));

        if(currentImagePath.getValue() != null && !currentImagePath.getValue().isEmpty()) {
            isUsingCurrentImage.setValue(true);
        }
    }

    private void initItemNameValidation() {
        itemNameErrorText.addSource(itemName, string -> {

            int minLength = 5;
            String errorText = retrieveValidationText(validateInput(specialCharsRegEx, string, minLength),
                    resources.getString(R.string.form_error_invalid_chars_no_special));

            if (!errorText.isEmpty()) {
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
            String errorText = retrieveValidationText(validateInput(numbersRegEx, string, 0),
                    resources.getString(R.string.form_error_invalid_chars_number_only));
            if (!errorText.isEmpty() && pileExpiryList.getValue() == null) {
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
            String errorText = retrieveValidationText(validateInput(numbersRegEx, string, 1),
                    resources.getString(R.string.form_error_invalid_chars_number_only));
            if (!errorText.isEmpty()) {
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

    private String retrieveValidationText(@ValidationUtilsErrorTypeDef int errorType,
                                          String invalidCharactersErrorText) {
        switch (errorType) {
            case NULL_INPUT:
            case EMPTY_FIELD: return resources.getString(R.string.form_error_empty_field);
            case INVALID_CHARS: return invalidCharactersErrorText;
            case MIN_NOT_REACHED: return resources.getString(R.string.form_error_min_not_reached);
            case VALID: return "";
        } return "";
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
        return categoryNameList;
    }

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


    SingleLiveEvent<ManageItemStatusEventWrapper> getAddItemEvent() {
        return addItemEvent;
    }

    public LiveData<String> getItemImageUri() {
        return itemImageUri;
    }

    private void setUserSelectedImageUri(String uri) {
        itemImageUri.postValue(uri);
        isUsingCurrentImage.setValue(false);
    }

    /**
     * Due to onActivityResult being called before onResume, a user selected image,
     * must be set after restoring an item pile onResume. This allows the new uri to be selected
     * at the right time in our fragment/view model life cycle.
     *
     * @param uri
     */
    void setUserSelectedImageUriOnResume(String uri) {
        selectedUriOnResume = uri;
    }

    public LiveData<String> getCurrentImagePath() {
        return currentImagePath;
    }

    public LiveData<Boolean> getIsUsingCurrentImage() { return isUsingCurrentImage;}

    public LiveData<Boolean> getIsAttemptingUpload() {
        return isAttemptingUpload;
    }

    public void onAddExpiryPileClicked() {
        String date = getNewPileExpiryDate().getValue();

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
            isExpiryEntryError.setValue(false);
        }
    }

    private ItemPile buildItemPileFromInput() {
        ArrayList<Date> expiryList = convertExpiryPilesToDates(pileExpiryList.getValue());
        ItemPile newItem = new ItemPile();
        newItem.setItemName(itemName.getValue());
        newItem.setCategoryName(categoryName.getValue());
        if (expiryList != null) {
            newItem.setItemCount(expiryList.size());
            newItem.setExpiry(orderDateArrayListAscending(expiryList));
        }
        if(itemCalories.getValue() != null) {
            newItem.setCalories(Integer.parseInt(itemCalories.getValue()));
        }
        newItem.setCounterType(CounterType.GRAMS);
        newItem.setQuantity(0);
        if(isEditMode) {
            if(itemPileRef.getImagePath() != null && !itemPileRef.getImagePath().isEmpty()) {
                newItem.setImagePath(itemPileRef.getImagePath());
                newItem.setImageStatus(itemPileRef.getImageStatus());
            }
        }
        return newItem;
    }

    /**
     * Methods add an item to, or edits an item in cloud firestore (via repository).
     */
    void onAddItemClicked() {

        if (areAllInputsValid()) {

            ItemPile newItem = buildItemPileFromInput();

            if (!getUserID().isEmpty()) {
                isAttemptingUpload.setValue(true);
                if(itemImageUri.getValue() == null || itemImageUri.getValue().isEmpty()) {
                    if (isEditMode) {
                        updateItemWithNoImageChange(newItem);
                    } else {
                        newItem.setImageStatus(NONE);
                        addNewItemWithoutImage(newItem);
                    }
                    isAddItemSuccessful.setValue(true);
                } else {
                    // User is attempting to upload an image. Offline checks should be made.
                    if(networkUtils.isInternetAvailable()) {
                        if(isEditMode) {
                            newItem.setImageStatus(UPLOADING);
                            updateItemAndEditImage(newItem);
                        } else {
                            newItem.setImageStatus(UPLOADING);
                            addNewItemWithImage(newItem);
                        }
                    } else {
                        isOfflineOnImageSubmission.setValue(true);
                        isAttemptingUpload.setValue(false);
                    }
                }
                if(isOfflineOnImageSubmission.getValue() == null ||
                    !isOfflineOnImageSubmission.getValue() ) {
                    isAddItemSuccessful.setValue(true);
                    updateRepositories(newItem);
                    postAddItemEventAndClear();
                }

            } else {
                Timber.e("Could not get userid");
            }
        }
    }

    void onSubmitWhileOffline() {
        itemImageUri.setValue(null);
        onAddItemClicked();
    }

    LiveData<Boolean> getIsOfflineImageSubmission() {
        return isOfflineOnImageSubmission;
    }

    LiveData<Boolean> getIsAddItemSuccessful() {
        return isAddItemSuccessful;
    }

    private void addNewItemWithoutImage(ItemPile newItemPile) {
        itemRepository.addItem(getUserID(), newItemPile);
    }

    private void updateItemWithNoImageChange(ItemPile updatedItemPile) {
        itemRepository.updateItem(getUserID(), updatedItemPile, itemPileRef.getItemName());
    }

    private void addNewItemWithImage(ItemPile newItemPile) {
        itemRepository.addItem(getUserID(), itemImageUri.getValue(), newItemPile);
    }

    private void updateItemAndEditImage(ItemPile updatedItemPile) {
        itemRepository.updateItemWithImageChange(getUserID(), itemImageUri.getValue(), updatedItemPile,
                itemPileRef.getItemName());
    }

    private int getTotalChangeInCalories(ItemPile newItem) {
        int initialCalories = 0;
        if(itemPileRef != null) { initialCalories = itemPileRef.getCaloriesTotal(); }
        int newCalorieTotal = newItem.getItemCount() * newItem.getCalories();
        return newCalorieTotal - initialCalories;
    }

    private int getTotalChangeInItems(ItemPile newItem) {
        int initialItemCount = 0;
        if(itemPileRef != null) { initialItemCount = itemPileRef.getItemCount(); }
        return newItem.getItemCount() - initialItemCount;
    }

    private void updateRepositories(ItemPile newItem) {
        int totalCalorieChange = getTotalChangeInCalories(newItem);
        targetsRepository.updateTargetProgress(getUserID(), categoryName.getValue(),
                getTotalChangeInItems(newItem), totalCalorieChange);
        userRepository.updateCategoryTotals(getUserID(), categoryName.getValue(),
                totalCalorieChange, 1);
    }

    private void postAddItemEventAndClear() {
        prefs.clearManageItemSavedStatePrefs();
        isAttemptingUpload.setValue(false);
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

        //TODO: push validation errors with event
        return isInputValid;
    }

    private static int getTotalExpiryPileItems(ArrayList<ExpiryPile> expiryPileArrayList) {
        int totalItems = 0;
        for (ExpiryPile e: expiryPileArrayList) {
            totalItems += e.getItemCount();
        } return  totalItems;
    }
}