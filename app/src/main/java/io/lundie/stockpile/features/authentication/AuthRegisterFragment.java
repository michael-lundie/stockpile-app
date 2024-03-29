package io.lundie.stockpile.features.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentAuthRegisterBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.utils.NetworkUtils;
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

    @Inject
    NetworkUtils networkUtils;

    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean hasInitialisedNavigation = false;
    Snackbar offlineSnackbar;

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
        buildOfflineSnackbar(container);
        if(authViewModel.isUserSignedIn()) {
            navigateToHome();
        } else {
            observeSignInEvents();
        }
        binding.setViewmodel(authViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void buildOfflineSnackbar(ViewGroup container) {
        offlineSnackbar = Snackbar.make(container.getRootView(),
                getString(R.string.check_connection), Snackbar.LENGTH_LONG);
        offlineSnackbar.setAction(getString(R.string.label_dismiss), v -> offlineSnackbar.dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initViewModels() {
        authViewModel = new ViewModelProvider(this, viewModelFactory).get(AuthViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureSignInOptions();
    }

    public void onSignInWithGoogleClicked() {
        if(networkUtils.isInternetAvailable()) {
            authViewModel.setIsLoading(true);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            offlineSnackbar.show();
        }
    }

    public void onSkipSignUpClicked() {
        if(networkUtils.isInternetAvailable()) {
            authViewModel.setIsLoading(true);
            authViewModel.signInAnonymously();
        } else {
            offlineSnackbar.show();
        }
    }

    /**
     * Method configures sign-in options for google account sign-in or registration.
     * A token ID is requested so that a users anonymous account can be upgraded.
     */
    private void configureSignInOptions() {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authenticateAndSignInWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e, "Google sign in failed");
            }
        }
    }

    private void authenticateAndSignInWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // Links an already created anonymous account to a g-mail address
        authViewModel.signInWithGoogle(credential, acct.getDisplayName(), acct.getEmail());
    }

    private void observeSignInEvents() {
        authViewModel.getRequestSignInEvent().observe(getViewLifecycleOwner(), requestSignInEvent -> {
            switch (requestSignInEvent.getSignInStatus()) {
                case SUCCESS:
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