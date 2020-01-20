package io.lundie.stockpile.features.homeview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.databinding.FragmentHomeBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;

import static android.app.Activity.RESULT_OK;
import static io.lundie.stockpile.features.authentication.SignInStatusType.REQUEST_SIGN_IN;

/**
 *
 */
public class HomeFragment extends FeaturesBaseFragment {

    private static final int RC_SIGN_IN = 1111;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    HomeFragmentPagerAdapter pagerAdapter;

    @Inject
    FirebaseFirestore firestore;

    @Inject
    FirebaseStorage storage;

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

        setNavController(container);
        setupViewPager(binding);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        initObservers();
        return binding.getRoot();
    }

    private void setupViewPager(FragmentHomeBinding binding) {
        ViewPager viewPager = binding.homeViewPager;
        TabLayout tabLayout = binding.homeTabLayout;
        pagerAdapter.addFragment(new ExpiringItemsFragment(), "Expiring");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private void initViewModels() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initObservers() {
        homeViewModel.getRequestSignInEvent().observe(this, requestSignInEvent -> {
            if(requestSignInEvent.getSignInStatus() == REQUEST_SIGN_IN) {
                requestSignIn();
            }
        });
    }

    private void requestSignIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void onRegisterClicked(View view) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

        private void createUserAndAddData (AtomicReference < UserData > reference) {
            UserData userData = new UserData();
            userData.setDisplayName(null);
            userData.setUserID(homeViewModel.getUserID());
            reference.set(userData);
            firestore.collection("users")
                    .document(homeViewModel.getUserID()).set(userData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                }
            });
        }
}