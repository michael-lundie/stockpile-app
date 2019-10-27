package io.lundie.stockpile.features.authentication;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentAuthBinding;

/**
 */
public class AuthFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseAuth mAuth;

    private UserViewModel userViewModel;

    public AuthFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        FragmentAuthBinding binding = FragmentAuthBinding.inflate(inflater, container, false);
        binding.setViewmodel(userViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

}
