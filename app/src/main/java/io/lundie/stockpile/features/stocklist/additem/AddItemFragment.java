package io.lundie.stockpile.features.stocklist.additem;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.lundie.stockpile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {


    public AddItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

}
