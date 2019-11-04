package io.lundie.stockpile.features.homeview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemList;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.databinding.FragmentHomeBinding;
import io.lundie.stockpile.utils.data.FakeDataUtil;

/**
 *
 */
public class HomeFragment extends DaggerFragment {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseFirestore firestore;

    private HomeViewModel homeViewModel;

    public HomeFragment() { /* Required empty constructor */ }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Initiating HomeFragment onCreateView.");

        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        //TODO: Confirm how this data-binding method is actually getting our layout id
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //firestoreTest();
    }


    private void firestoreTest() {

        AtomicReference<UserData> reference = new AtomicReference<>();

        DocumentReference docRef = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(LOG_TAG, "Firestore: DocumentSnapshot data: " + document.getData());
                    reference.set(document.toObject(UserData.class));

                    UserData userData = reference.get();

                    Log.i(LOG_TAG, "Firestore result: " + userData.getDisplayName());
                    Log.i(LOG_TAG, "Firestore result: " + userData.getUserID());
                    Log.i(LOG_TAG, "Firestore result: " + userData.getCategories());

                    addFirestoreData(reference);
                } else {
                    Log.d(LOG_TAG, "Firestore: No such document");
                }
            } else {
                Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
            }
        });


    }

    private void addFirestoreData(AtomicReference<UserData> reference) {
        UserData userData = reference.get();

        int arrayLength = userData.getCategories().size();



        FakeDataUtil dataUtil = new FakeDataUtil();
        ItemCategory itemCategory = dataUtil.getItemCategory();
        ItemList itemList = dataUtil.getItemList();

        Log.e(LOG_TAG, "Category Name: " + dataUtil.getItemCategory().getCategoryName());

        ArrayList<ItemCategory> itemCategoryList = userData.getCategories();
        itemCategoryList.add(arrayLength, itemCategory);

        Map<String, Object> nestedCategoryData = new HashMap<>();
        nestedCategoryData.put("type_name", itemCategory.getCategoryName());
        nestedCategoryData.put("super_type", itemCategory.getSuperType());
        nestedCategoryData.put("numberOfPiles", itemCategory.getNumberOfPiles());
        nestedCategoryData.put("total_calories", itemCategory.getTotalCalories());
        nestedCategoryData.put("icon_uri", itemCategory.getIconUri());

        Map<String, Object> object = new HashMap<>();

        object.put("categories", itemCategoryList);

        firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
                .set(object, SetOptions.merge());

        firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
                .collection("category").document(itemCategory.getCategoryName())
                .set(itemList);
    }
}