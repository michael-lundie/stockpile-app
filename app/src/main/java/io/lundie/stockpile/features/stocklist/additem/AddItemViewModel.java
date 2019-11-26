package io.lundie.stockpile.features.stocklist.additem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;

/**
 * AddItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input.
 */
public class AddItemViewModel extends FeaturesBaseViewModel {


    private static final String LOG_TAG = AddItemViewModel.class.getSimpleName();

    private final ItemRepository itemRepository;

    private MutableLiveData<String> categoryLiveData = new MutableLiveData<>();
    private MutableLiveData<String> testEditText = new MutableLiveData<>();

    private MediatorLiveData<String> confirmationText = new MediatorLiveData<>();

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

    public LiveData getCategoryLiveData() {
        return categoryLiveData;
    }

    void setCategory(String category) {
        categoryLiveData.postValue(category);
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
