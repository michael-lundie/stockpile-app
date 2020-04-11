package io.lundie.stockpile.features.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentAuthRegisterBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;

/**
 */
public class AuthRegisterFragment extends FeaturesBaseFragment {

    private static final int RC_SIGN_IN = 1111;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    FirebaseAuth mAuth;
    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean hasInitialisedNavigation = false;

    public AuthRegisterFragment() { /* Required clear public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        FragmentAuthRegisterBinding binding = FragmentAuthRegisterBinding.inflate(inflater, container, false);
        //setNavController(container);
        if(authViewModel.isUserSignedIn()) {
            navigateToHome();
        } else {
            observeSignInEvents();
        }
        binding.setViewmodel(authViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initViewModels() {
        authViewModel = ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureSignInOptions();
    }

    public void onSignInWithGoogleClicked() {
        authViewModel.setIsLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onSkipSignUpClicked() {
        authViewModel.setIsLoading(true);
        authViewModel.signInAnonymously();
    }

    /**
     * Method configures sign-in options for google account sign-in or registration.
     * A token ID is requested so that a users anonymous account can be upgraded.
     */
    private void configureSignInOptions() {
        Timber.e("Configuring Sign In Options");
//        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getContext());
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authenticateAndSignInWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e, "Google sign in failed");
                // ...
            }
        }
    }

    private void authenticateAndSignInWithGoogle(GoogleSignInAccount acct) {
        Timber.d("authenticateAndSignInWithGoogle: %s", acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // Links an already created anonymous account to a g-mail address
        authViewModel.signInWithGoogle(credential, acct.getDisplayName(), acct.getEmail());
    }

    private void observeSignInEvents() {
        authViewModel.getRequestSignInEvent().observe(getViewLifecycleOwner(), requestSignInEvent -> {
            switch (requestSignInEvent.getSignInStatus()) {
                case SUCCESS:
                    Timber.e("Sign-in SUCCESS Received");
                    navigateToHome();
                    break;
                case FAIL_AUTH:
                    //TODO: Check if user already has an account registered and handle here.
                    Toast.makeText(getActivity(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                    authViewModel.setIsLoading(false);
                    break;
            }
        });
    }

    @Override
    public void onStop() {
        removeObservers();
        super.onStop();
    }

    private void navigateToHome() {
        // Navigation variable prevents multiple calls to navigation
        if(!hasInitialisedNavigation) {
            removeObservers();
            getNavController().navigate(AuthRegisterFragmentDirections.actionAuthRegisterFragmentDestToHomeFragmentDest());
            hasInitialisedNavigation = true;
        }
    }

    private void removeObservers() {
        authViewModel.getRequestSignInEvent().removeObservers(getViewLifecycleOwner());
    }
}
