package io.lundie.stockpile.features.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import io.lundie.stockpile.databinding.FragmentAuthRegisterBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;

/**
 */
public class AuthRegisterFragment extends FeaturesBaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseAuth mAuth;

    private AuthViewModel authViewModel;

    public AuthRegisterFragment() { /* Required clear public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAuthRegisterBinding binding = FragmentAuthRegisterBinding.inflate(inflater, container, false);
        binding.setViewmodel(authViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void initViewModels() {
        authViewModel = ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel.class);
    }

    public void onRegisterClicked() {

    }
}
