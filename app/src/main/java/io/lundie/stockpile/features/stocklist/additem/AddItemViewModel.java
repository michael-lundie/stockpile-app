package io.lundie.stockpile.features.stocklist.additem;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.data.CounterType;
import io.lundie.stockpile.utils.data.FakeDataUtilHelper;

import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.*;

/**
 * AddItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input.
 */
public class AddItemViewModel extends FeaturesBaseViewModel{

    private static final String LOG_TAG = AddItemViewModel.class.getSimpleName();

    private final ItemRepository itemRepository;

    private MutableLiveData<String> categoryLiveData = new MutableLiveData<>();
    private MutableLiveData<String> testEditText = new MutableLiveData<>();
    private MutableLiveData<String> itemImageUri = new MutableLiveData<>();

    private MediatorLiveData<String> confirmationText = new MediatorLiveData<>();

    private SingleLiveEvent<Boolean> isAddItemSuccessfulEvent = new SingleLiveEvent<>();

    @Inject
    AddItemViewModel(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        confirmationText.addSource(testEditText, string -> {
            if(string.length() < 5 ) {
                confirmationText.postValue("Not okay");
            } else {
                confirmationText.postValue("okay!");
            }
        });
    }


    public LiveData<String> getTestData() {
        return itemRepository.getTestLiveData();
    }

    @Override
    public void onAttemptingSignIn() {

    }

    @Override
    public void onSignInSuccess(String userID) {

    }

    @Override
    public void onSignedInAnonymously(String userID) {

    }

    @Override
    public void onSignInFailed() {

    }

    public void onAddItemClicked() {

        ArrayList<Date> expiryList = new ArrayList<>();
        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));

        String catName = categoryLiveData.getValue();
        ItemPile newItem = new ItemPile();
        newItem.setItemName(testEditText.getValue());
        newItem.setCategoryName(catName);
        newItem.setItemCount(0);
        newItem.setCalories(0);
        newItem.setCounterType(CounterType.MILLILITRES);
        newItem.setQuantity(0);
        newItem.setExpiry(expiryList);

        if(!getUserID().isEmpty()) {
            itemRepository.addItem(getUserID(), getItemImageUri().getValue(), newItem,
                    addItemStatus -> {
                        switch(addItemStatus) {
                            case (ADDING_ITEM):
                                break;
                            case (SUCCESS):
                                isAddItemSuccessfulEvent.postValue(true);
                                break;
                            case(IMAGE_FAILED):
                                Log.e(LOG_TAG, "Image Failed");
                            case(FAILED):
                                Log.e(LOG_TAG, "Item Failed");
                                isAddItemSuccessfulEvent.postValue(false);
                                break;
                        }
                    });
        }
    }

    public SingleLiveEvent<Boolean> getIsAddItemSuccessfulEvent() {
        return isAddItemSuccessfulEvent;
    }

    public LiveData getCategoryLiveData() {
        return categoryLiveData;
    }

    void setCategory(String category) {
        categoryLiveData.postValue(category);
    }

    public void setItemImageUri(String uri) {
        itemImageUri.postValue(uri);
    }

    public LiveData<String> getItemImageUri() {
        return itemImageUri;
    }

    public MutableLiveData<String> getTestEditText() {
        return testEditText;
    }

    public void setTestEditText(MutableLiveData<String> testEditText) {
        this.testEditText = testEditText;
    }

    public MediatorLiveData<String> getConfirmationText() {
        return confirmationText;
    }

    public void setConfirmationText(MediatorLiveData<String> confirmationText) {
        this.confirmationText = confirmationText;
    }
}
