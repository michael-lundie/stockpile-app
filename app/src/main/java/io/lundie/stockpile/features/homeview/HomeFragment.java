package io.lundie.stockpile.features.homeview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.databinding.FragmentHomeBinding;
import io.lundie.stockpile.features.authentication.UserViewModel;
import io.lundie.stockpile.utils.data.FakeData;

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

    public HomeFragment() { /* Required empty constructor */ }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void initViewModels() {
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //firestoreTest();
    }
    public void onAddImageClicked(View view) {
        Log.d(LOG_TAG, "Image upload clicked");
        imageUploadTest();
    }

    public void onAddFakeClicked(View view) {
        uploadFakeData();
    }

    private void imageUploadTest() {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference testRef = storageRef.child("test.jpg");

        // Create a reference to 'images/mountains.jpg'
        String storagePath = "users/" + userViewModel.getUserID() + "/test.jpg";
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
//        getUser();
    }

//    private void getUser() {
//        userViewModel.getSignInStatus().observe(this.getViewLifecycleOwner(),
//                signInStatus -> {
//                    switch(signInStatus) {
//                        case ATTEMPTING_SIGN_IN:
//                            Toast.makeText(getActivity(), "Signing-in.", Toast.LENGTH_SHORT).show();
//                            break;
//                        case SUCCESS:
//                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
//                            break;
//                        case SUCCESS_ANON:
//                            Toast.makeText(getActivity(), "Success Anon", Toast.LENGTH_SHORT).show();
//                            break;
//                        case FAIL_AUTH:
//                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void uploadFakeData() {

        String uid = userViewModel.getUserID();
        if(uid != null) {
            AtomicReference<UserData> reference = new AtomicReference<>();

            DocumentReference docRef = firestore.collection("users").document(userViewModel.getUserID());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(LOG_TAG, "Firestore: DocumentSnapshot data: " + document.getData());
                        reference.set(document.toObject(UserData.class));

                        UserData userData = reference.get();
                        if(userData.getCategories() != null) {
                            Log.e(LOG_TAG, "Fake Data already exists. Please clear database" +
                                    "before a second attempt.");
                        } else {
                            addFakeDatabaseData();
                        }

                    } else {
                        Log.d(LOG_TAG, "Firestore: No such document");
                        createUserAndAddData(reference);
                    }
                } else {
                    Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
                }
            });
        } else {
            Log.e(LOG_TAG, "Uploading Fake Data not possible. User ID is empty.");
        }

    }

    private void addFakeDatabaseData() {
        FakeData fakeData = new FakeData();


        Map<String, Object> categoryObject = new HashMap<>();

        categoryObject.put("categories", fakeData.getItemCategories());

        firestore.collection("users").document(userViewModel.getUserID())
                .set(categoryObject, SetOptions.merge());

        for (ItemPile itemPile : fakeData.getItemPiles()) {
            firestore.collection("users").document(userViewModel.getUserID())
                    .collection("items").document(itemPile.getItemName())
                    .set(itemPile);
        }
    }

    private void createUserAndAddData(AtomicReference<UserData> reference) {
        UserData userData = new UserData();
        userData.setDisplayName(null);
        userData.setUserID(userViewModel.getUserID());
        reference.set(userData);
        firestore.collection("users")
                .document(userViewModel.getUserID()).set(userData).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                addFakeDatabaseData();
            }
        });
    }
}