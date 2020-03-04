package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.utils.AppExecutors;

public class TargetsRepository extends BaseRepository{
    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;
    private FirestoreQueryLiveData targetsLiveData;

    @Inject
    TargetsRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public LiveData<QuerySnapshot> getTargets(@NonNull String userID) {
        if(targetsLiveData == null || targetsLiveData.getValue() == null) {
            CollectionReference targetsReference = firestore.collection("users").document(userID)
                    .collection("targets");
            Query targetsQuery = targetsReference.limit(10);
            targetsLiveData = new FirestoreQueryLiveData(targetsQuery);
        }
        return targetsLiveData;
    }
}
