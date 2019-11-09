package io.lundie.stockpile.features.homeview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemList;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.databinding.FragmentHomeBinding;
import io.lundie.stockpile.features.authentication.UserViewModel;
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

    @Inject
    FirebaseStorage storage;

    private UserViewModel userViewModel;
    private HomeViewModel homeViewModel;

    private FirebaseUser user;

    public HomeFragment() { /* Required empty constructor */ }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Initiating HomeFragment onCreateView.");
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        //TODO: Confirm how this data-binding method is actually getting our layout id
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //firestoreTest();
    }

    public void onButtonClicked(View view) {
        Log.d(LOG_TAG, "Test Button Clicked");
        firestoreTest();
    }

    public void onAddImageClicked(View view) {
        Log.d(LOG_TAG, "Image upload clicked");
        imageUploadTest();
    }

    private void imageUploadTest() {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference testRef = storageRef.child("test.jpg");

        // Create a reference to 'images/mountains.jpg'
        String storagePath = "users/" + user.getUid() + "/test.jpg";
        Log.d(LOG_TAG, "Upload: Path: " + storagePath);
        StorageReference testImageRef = storageRef.child(storagePath);

        // While the file names are the same, the references point to different files
        testRef.getName().equals(testImageRef.getName());    // true
        testRef.getPath().equals(testImageRef.getPath());    // false


        Bitmap testBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = testImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d(LOG_TAG, "Upload: Goats have been teleported! (fail)");
            }
        }).addOnSuccessListener(taskSnapshot -> {
            Log.d(LOG_TAG, "Upload: Looks like Upload was successful!");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getUser();
    }

    private void getUser() {
        userViewModel.getSignInStatus().observe(this.getViewLifecycleOwner(),
                signInStatus -> {
                    switch(signInStatus) {
                        case ATTEMPTING_SIGN_IN:
                            Toast.makeText(getActivity(), "Signing-in.", Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESS:
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            user = userViewModel.getCurrentUser();
                            break;
                        case SUCCESS_ANON:
                            Toast.makeText(getActivity(), "Success Anon", Toast.LENGTH_SHORT).show();
                            user = userViewModel.getCurrentUser();
                            break;
                        case FAIL_AUTH:
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firestoreTest() {

        AtomicReference<UserData> reference = new AtomicReference<>();

        DocumentReference docRef = firestore.collection("users").document(userViewModel.getUserUID());
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
                    createFirestoreDocument(reference);
                }
            } else {
                Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
            }
        });


    }

    private void createFirestoreDocument(AtomicReference<UserData> reference) {
        UserData userData = new UserData();
        userData.setDisplayName(null);
        userData.setUserID(user.getUid());
        reference.set(userData);
        firestore.collection("users")
                .document(user.getUid()).set(userData).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                addFirestoreData(reference);
            }
        });

    }

    private void addFirestoreData(AtomicReference<UserData> reference) {
        Log.d(LOG_TAG,"Adding to firestore.");
        UserData userData = reference.get();
        int arrayLength = 0;

        if(userData.getCategories() != null) {
            arrayLength = userData.getCategories().size();
        }

        FakeDataUtil dataUtil = new FakeDataUtil();
        ItemCategory itemCategory = dataUtil.getItemCategory();
        //ItemList itemList = dataUtil.getItemList();
        ArrayList<ItemPile> itemPiles = dataUtil.getItemPiles();

        Log.e(LOG_TAG, "Category Name: " + dataUtil.getItemCategory().getCategoryName());

        ArrayList<ItemCategory> itemCategoryList = new ArrayList<>();

        if (userData.getCategories() != null) {
            itemCategoryList = userData.getCategories();
        } else {
            Log.d(LOG_TAG, "No categories existed in firebase. Creating new category.");
        }

        itemCategoryList.add(arrayLength, itemCategory);

        Map<String, Object> nestedCategoryData = new HashMap<>();
        nestedCategoryData.put("type_name", itemCategory.getCategoryName());
        nestedCategoryData.put("super_type", itemCategory.getSuperType());
        nestedCategoryData.put("numberOfPiles", itemCategory.getNumberOfPiles());
        nestedCategoryData.put("total_calories", itemCategory.getTotalCalories());
        nestedCategoryData.put("icon_uri", itemCategory.getIconUri());

        Map<String, Object> object = new HashMap<>();

        object.put("categories", itemCategoryList);

        firestore.collection("users").document(userViewModel.getUserUID())
                .set(object, SetOptions.merge());

//        firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
//                .collection("category").document(itemCategory.getCategoryName())
//                .set(itemList);

        for (ItemPile itemPile : itemPiles) {
            firestore.collection("users").document(userViewModel.getUserUID())
                    .collection("items").document(itemPile.getItemName())
                    .set(itemPile);
        }
    }
}