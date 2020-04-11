package io.lundie.stockpile.features.homeview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentHomeBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;
import io.lundie.stockpile.features.general.AlertDialogFragment;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUploadManager.UPLOAD_TAG;

/**
 * Fragment responsible for initialising views, paging adapter fragments and initialising
 * sign-in options.
 */
public class HomeFragment extends FeaturesBaseFragment {

    private static final int RC_SIGN_IN = 1111;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    HomeFragmentPagerAdapter pagerAdapter;
    @Inject
    FirebaseStorage storage;

    private GoogleSignInClient mGoogleSignInClient;
    private HomeViewModel homeViewModel;

    public HomeFragment() { setHasOptionsMenu(true);}

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
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        setNavController(container);
        setupViewPager(binding);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        displaySimpleEventMessages(homeViewModel);
        startObservingTransactionEvents(homeViewModel);
        homeViewModel.getTargetBus().clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopObservingTransactionEvents(homeViewModel);
    }

    private void setupViewPager(FragmentHomeBinding binding) {
        ViewPager viewPager = binding.homeViewPager;
        TabLayout tabLayout = binding.homeTabLayout;
        pagerAdapter.addFragment(new io.lundie.stockpile.features.homeview.HomeTargetsFragment(), "Target");
        pagerAdapter.addFragment(new ExpiringItemsFragment(), "Expiring");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    enableExFab();
                } else if (position == 1) {
                    disableExFab();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initViewModels() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureSignInOptions();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_sign_out) {
            // User will not have a display name if they are signed in anonymously.
            String displayName = homeViewModel.getUserDisplayName().getValue();
            if(displayName.isEmpty()) {
                showConfirmationDialog(getResources().getString(R.string.dialog_label_signout_user_anon));
            } else if(isStillUploadingImages()) {
                showConfirmationDialog(getResources().getString(R.string.dialog_label_signout_work_progress));
            } else {
                onSignOutConfirmed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isStillUploadingImages() {
        WorkManager wm = WorkManager.getInstance(getActivity().getApplication());
        ListenableFuture<List<WorkInfo>> future = wm.getWorkInfosByTag(UPLOAD_TAG);
        List<WorkInfo> list = null;
        try {
            list = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if((list != null) && (list.size() != 0)){
            for (WorkInfo info : list) {
                if(!info.getState().isFinished()) {
                    return true;
                }
            }
        } return false;
    }

    private void showConfirmationDialog(String signOutMessage) {
        AlertDialogFragment alertDialogFragment =
                AlertDialogFragment.newInstance(
                        getResources().getString(R.string.dialog_title_confirm_signout),
                        signOutMessage,
                        getResources().getString(R.string.action_yes),
                        getResources().getString(R.string.action_no),
                        this::onSignOutConfirmed);
        alertDialogFragment.show(getChildFragmentManager(), "AlertDialog");
    }


    private void onSignOutConfirmed() {
        homeViewModel.signOutUser();
        getNavController().navigate(HomeFragmentDirections.actionHomeFragmentDestToAuthRegisterFragmentDest());
    }

    /**
     * Method configures sign-in options for google account sign-in or registration.
     * A token ID is requested so that a users anonymous account can be upgraded.
     */
    private void configureSignInOptions() {
        Timber.e("Configuring Sign In Options");
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getContext());
//        if (googleAccount == null) {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
//        }
    }

    public void onRegisterClicked(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                authenticateAndRegisterWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e, "Google sign in failed");
                // ...
            }
        }
    }

    private void authenticateAndRegisterWithGoogle(GoogleSignInAccount acct) {
        Timber.d("authenticateAndRegisterWithGoogle: %s", acct.getId());
        observeSignInEvent();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // Links an already created anonymous account to a g-mail address
        homeViewModel.linkAndRegisterAnonymousAccount(credential, acct.getDisplayName(), acct.getEmail());
    }

    private void observeSignInEvent() {
        homeViewModel.getSignInStatusLiveEvents().observe(getViewLifecycleOwner(), requestSignInEvent -> {
            switch (requestSignInEvent.getSignInStatus()) {
                case SUCCESS:
                    Toast.makeText(getActivity(), "Sign In Successful", Toast.LENGTH_SHORT).show();
                    break;
                case FAIL_AUTH:
                    //TODO: Check if user already has an account registered and handle here.
                    Toast.makeText(getActivity(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}