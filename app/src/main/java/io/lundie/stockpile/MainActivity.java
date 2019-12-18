package io.lundie.stockpile;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.lundie.stockpile.utils.layoutbehaviors.HideBottomNavigationOnScrollBehavior;

public class MainActivity extends DaggerAppCompatActivity {

    NavHostFragment navigationHost;
    NavController navController;

    @Inject
    FirebaseAuth mAuth;

    BottomNavigationView bottomNav;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_nav_fragment_main);

        if(navigationHost != null) {
            navController = navigationHost.getNavController();
            setupNavigation(navController);
            navVisibilityController(navController);
        }
    }

    @SuppressWarnings("unchecked")
    private void navVisibilityController(NavController navController) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) bottomNav.getLayoutParams();
        HideBottomNavigationOnScrollBehavior behavior =
                (HideBottomNavigationOnScrollBehavior) params.getBehavior();
        Handler handler = new Handler();
        Runnable hideNav = () -> bottomNav.setVisibility(View.GONE);

        if (behavior != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if(destination.getId() == R.id.add_item_fragment_destination) {
                    if(behavior.isNavigationVisible()) {
                        behavior.slideDown(bottomNav);
                        handler.postDelayed(hideNav, 200);
                    }

                } else {
                    if(!behavior.isNavigationVisible()) {
                        behavior.slideUp(bottomNav);
                    }
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

    }

    private void setupNavigation(NavController navController) {
        new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}