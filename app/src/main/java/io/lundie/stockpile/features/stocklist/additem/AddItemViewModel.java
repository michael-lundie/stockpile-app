package io.lundie.stockpile.features.stocklist.additem;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ExpiryPile;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.data.CounterType;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.*;
import static io.lundie.stockpile.utils.AppUtils.localDatetoDate;

/**
 * AddItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input.
 * <p>
 * TODO: Bugs:
 * https://github.com/material-components/material-components-android/issues/525
 */
public class AddItemViewModel extends FeaturesBaseViewModel {

    private static final String LOG_TAG = AddItemViewModel.class.getSimpleName();

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Resources resources = this.getApplication().getResources();

    private MutableLiveData<String> categoryNameLiveData = new MutableLiveData<>();
    private List<String> categoryNameList;

    private MutableLiveData<String> itemNameLiveData = new MutableLiveData<>();
    private MediatorLiveData<String> itemNameErrorText = new MediatorLiveData<>();
    private boolean isItemNameError = true;

    private MutableLiveData<ArrayList<ExpiryPile>> expiryPileMutableList = new MutableLiveData<>();
    private int expiryPileIdCounter = 0;
    private MutableLiveData<Boolean> isExpiryEntryError = new MutableLiveData<>(false);

    private MutableLiveData<String> itemExpiryDate = new MutableLiveData<>();
    private MediatorLiveData<String> itemExpiryErrorText = new MediatorLiveData<>();

    private MutableLiveData<String> itemsCount = new MutableLiveData<>();
    private MediatorLiveData<String> itemCountErrorText = new MediatorLiveData<>();
    private boolean isItemCountError = true;

    private MutableLiveData<String> caloriesPerItem = new MutableLiveData<>();
    private MediatorLiveData<String> itemCaloriesErrorText = new MediatorLiveData<>();
    private boolean isCaloriesPerItemError = true;

    private MediatorLiveData<String> totalCalories = new MediatorLiveData<>();
    private MutableLiveData<String> itemImageUri = new MutableLiveData<>();

    private MutableLiveData<Boolean> isAttemptingUpload = new MutableLiveData<>(false);
    private SingleLiveEvent<AddItemStatusEvent> isAddItemSuccessfulEvent = new SingleLiveEvent<>();

    @Inject
    AddItemViewModel(@NonNull Application application, ItemRepository itemRepository,
                     UserRepository userRepository) {
        super(application);
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        ArrayList<ItemCategory> itemCategories = userRepository.getCategoryData().getValue();
        Timber.e("#ItemCat --> Constructor: Category List is : %s", itemCategories);
        if (itemCategories != null) {
            setCategoryNameList(itemCategories);
        }

        initItemNameValidation();
        initItemCountValidation();
        initItemCaloriesValidation();
        initTotalCaloriesCounter();
    }

    @Override
    public void onAttemptingSignIn() {}

    @Override
    public void onSignInSuccess(String userID) {}

    @Override
    public void onSignedInAnonymously(String userID) {}

    @Override
    public void onSignInFailed() {}

    private int getTotalItems(ArrayList<ExpiryPile> expiryPileArrayList) {
        int totalItems = 0;
        for (ExpiryPile e: expiryPileArrayList) {
            totalItems += e.getItemCount();
        } return  totalItems;
    }

    private void initItemNameValidation() {
        itemNameErrorText.addSource(itemNameLiveData, string -> {

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
        itemCountErrorText.addSource(itemsCount, string -> {
            String errorText = validateInput("[^0-9]", string, 0,
                    "0-9 only");
            if (errorText != null && expiryPileMutableList.getValue() == null) {
                itemCountErrorText.setValue(errorText);
                isItemCountError = true;
            } else {
                if (isItemCountError) {
                    itemCountErrorText.setValue(null);
                }
                isItemCountError = false;
            }
        });
    }

    private void initItemCaloriesValidation() {
        itemCaloriesErrorText.addSource(caloriesPerItem, string -> {
            String errorText = validateInput("[^0-9]", string, 1,
                    "0-9 only");
            if (errorText != null) {
                itemCaloriesErrorText.setValue(errorText);
                isCaloriesPerItemError = true;
            } else {
                if (isCaloriesPerItemError) {
                    itemCaloriesErrorText.setValue(null);
                }
                isCaloriesPerItemError = false;
            }
        });
    }

    private void initTotalCaloriesCounter() {
        totalCalories.addSource(expiryPileMutableList, list -> {
            if(!isCaloriesPerItemError && caloriesPerItem.getValue() != null && !list.isEmpty()) {
                totalCalories.setValue(Integer.toString(
                        Integer.parseInt(caloriesPerItem.getValue()) * getTotalItems(list)));
            } else {
                totalCalories.setValue("");
            }
        });

        totalCalories.addSource(caloriesPerItem, string -> {
            if(string.equals("")) {
                totalCalories.setValue("");
            } else
            if(!isCaloriesPerItemError && expiryPileMutableList.getValue() != null) {
                totalCalories.setValue(Integer.toString(
                        Integer.parseInt(string) * getTotalItems(expiryPileMutableList.getValue())));
            }
        });
    }

    private String validateInput(String regex, String input,
                                 int minLength, String errorInvalidCharacters) {
        if(input == null) return null;
        if (hasInvalidCharacters(regex, input)) {
            return errorInvalidCharacters;
        }

        if (minLength != 0) {
            if (minLength == 1 && input.length() < 1) {
                //Todo: fix string literal
                return "Required";
            } else if (input.length() < minLength) {
                return "Minimum length: " + minLength;
            }
        }
        return null;
    }

    private boolean hasInvalidCharacters(String regex, String string) {
        String filteredString = string.replaceAll(regex, "");
        return !string.equals(filteredString);
    }

    public MutableLiveData<String> getItemNameLiveData() {
        return itemNameLiveData;
    }

    public LiveData<String> getItemNameErrorText() {
        return itemNameErrorText;
    }

    public MutableLiveData<String> getCategoryNameLiveData() {
        return categoryNameLiveData;
    }

    void setCategoryNameLiveData(String string) {
        categoryNameLiveData.setValue(string);
    }

    public List<String> getCategoryNameList() {
        Timber.e("#ItemCat --> Getter: Category List is retrieving as: %s", categoryNameList);
        return categoryNameList;
    }

    private void setCategoryNameList(ArrayList<ItemCategory> itemCategories) {
        ArrayList<String> list = new ArrayList<>();
        for (ItemCategory category : itemCategories) {
            list.add(category.getCategoryName());
        }
        Timber.e("#ItemCat --> Setter: Category List is setting as: %s", list);
        this.categoryNameList = list;
    }

    public MutableLiveData<String> getItemExpiryDate() {
        return itemExpiryDate;
    }

    public LiveData<String> getItemExpiryErrorText() {
        return itemExpiryErrorText;
    }

    LiveData<ArrayList<ExpiryPile>> getExpiryPileMutableList() {
        if (expiryPileMutableList.getValue() == null) {
            ArrayList<ExpiryPile> expiryPiles = new ArrayList<>();
            expiryPileMutableList.setValue(expiryPiles);
        }
        return expiryPileMutableList;
    }

    public LiveData<Boolean> isExpiryEntryError() { return isExpiryEntryError; }

    void removeExpiryPileItem(int itemId) {
        ArrayList<ExpiryPile> expiryPiles = expiryPileMutableList.getValue();
        if (expiryPiles != null) {
            for (ExpiryPile e : expiryPiles) {
                if (e.getItemId() == itemId) {
                    expiryPiles.remove(e);
                    break;
                }
            }
            expiryPileMutableList.setValue(expiryPiles);
        }
    }

    public MutableLiveData<String> getItemsCount() {
        return itemsCount;
    }

    public LiveData<String> getItemCountErrorText() {
        return itemCountErrorText;
    }

    public MutableLiveData<String> getCaloriesPerItem() {
        return caloriesPerItem;
    }

    public LiveData<String> getItemCaloriesErrorText() {
        return itemCaloriesErrorText;
    }

    public LiveData<String> getTotalCaloriesLiveData() {
        return totalCalories;
    }


    SingleLiveEvent<AddItemStatusEvent> getIsAddItemSuccessfulEvent() {
        return isAddItemSuccessfulEvent;
    }

    public LiveData<String> getItemImageUri() {
        return itemImageUri;
    }

    void setItemImageUri(String uri) {
        itemImageUri.postValue(uri);
    }

    public LiveData<Boolean> getIsAttemptingUpload() {
        return isAttemptingUpload;
    }

    public void onAddExpiryPileClicked() {
        String date = getItemExpiryDate().getValue();
        Timber.e("Quantity is: %s", itemsCount.getValue());

        if(itemsCount.getValue() == null || itemsCount.getValue().isEmpty()) {
            itemCountErrorText.setValue("Required.");
        } else if (getItemExpiryDate().getValue() == null || getItemExpiryDate().getValue().isEmpty()) {
            itemExpiryErrorText.setValue("Required.");
        } else {
            int quantity = Integer.parseInt(itemsCount.getValue());

            ExpiryPile newExpiryPile = new ExpiryPile(date, quantity, expiryPileIdCounter);
            expiryPileIdCounter++;
            ArrayList<ExpiryPile> list = expiryPileMutableList.getValue();

            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(newExpiryPile);
            expiryPileMutableList.setValue(list);
            itemExpiryDate.setValue(null);
            itemsCount.setValue(null);
            Timber.e("Expiry is: %s. Quantity is: %s", date, quantity);
            isExpiryEntryError.setValue(false);
        }
    }


    void onAddItemClicked() {

        if (areAllInputsValid()) {

            ArrayList<Date> expiryList = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            int totalItemCount = 0;

            for (ExpiryPile e: expiryPileMutableList.getValue()) {

                Date date = localDatetoDate(LocalDate.parse(e.getExpiry(), formatter));
                totalItemCount += e.getItemCount();
                for (int i = 0; i < e.getItemCount(); i++) {
                    expiryList.add(date);
                }
            }

            ItemPile newItem = new ItemPile();
            newItem.setItemName(itemNameLiveData.getValue());
            newItem.setCategoryName(categoryNameLiveData.getValue());
            newItem.setItemCount(totalItemCount);
            newItem.setCalories(Integer.parseInt(caloriesPerItem.getValue()));
            newItem.setCounterType(CounterType.GRAMS);
            newItem.setQuantity(0);
            newItem.setExpiry(expiryList);


            if (!getUserID().isEmpty()) {
                isAttemptingUpload.setValue(true);
                itemRepository.addItem(getUserID(), getItemImageUri().getValue(), newItem, addItemStatus -> {
                    if (addItemStatus != 0) {
                        if (addItemStatus != ADDING_ITEM) {
                            isAttemptingUpload.setValue(false);
                            postAddItemSuccessfulEvent(addItemStatus, getEventMessage(addItemStatus));
                        } else {
                            //TODO: handle delayed upload status
                        }
                    }
                });
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
        AddItemStatusEvent statusEvent = new AddItemStatusEvent();
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

        if (isCaloriesPerItemError) {
            itemCaloriesErrorText.setValue("Error");
            isInputValid = false;
        }

        if(expiryPileMutableList.getValue() == null || expiryPileMutableList.getValue().isEmpty()) {
            isExpiryEntryError.setValue(true);
            isInputValid = false;
        }

        return isInputValid;
    }
}
