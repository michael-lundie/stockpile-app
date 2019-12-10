package io.lundie.stockpile.features.stocklist.additem;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

class DualObserverLiveData extends MediatorLiveData<Pair<String, String>> {

    public DualObserverLiveData(LiveData<String> stringA, LiveData<String> stringB) {
        addSource(stringA, first -> setValue(Pair.create(first, stringA.getValue())));
        addSource(stringB, second -> setValue(Pair.create(second, stringB.getValue())));
    }
}