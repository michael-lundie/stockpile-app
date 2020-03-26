package io.lundie.stockpile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.features.widget.ExpiringItemsWidgetListProvider;
import io.lundie.stockpile.features.widget.ExpiringItemsWidgetProvider;
import io.lundie.stockpile.utils.Prefs;
import io.lundie.stockpile.utils.layoutbehaviors.HideBottomNavigationOnScrollBehavior;

public class MainActivity extends DaggerAppCompatActivity {

    private NavController navController;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ItemPileBus itemPileBus;

    @Inject
    Prefs prefs;

    @Inject
    SharedPreferences sharedPreferences;

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavHostFragment navigationHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_nav_fragment_main);

        if(navigationHost != null) {
            navController = navigationHost.getNavController();
            setupNavigation(navController);
            navVisibilityController(navController);
        }
        itemManagerStateCacheReset();
    }

    /**
     * Resets the ItemPileBus whenever the user navigates out of an item or item management
     * fragment. This behavior would ideally be controlled by a ViewModel which is aware
     * of the NavGraph.
     */
    private void itemManagerStateCacheReset() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() != R.id.manage_item_fragment_dest) {
                    itemPileBus.empty();
                    prefs.clearManageItemSavedStatePrefs();
            }
        });
    }

    /**
     * Controls the visibility of the bottom navigation bar for certain fragments where it's use
     * is not necessary or even detrimental to user interactions.
     * @param navController {@link NavController} of host fragment.
     */
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
                if(destination.getId() == R.id.manage_item_fragment_dest ||
                    destination.getId() == R.id.item_fragment_dest ||
                    destination.getId() == R.id.add_targets_fragment_dest) {
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

    /**
     * Sets up navigation control for toolbar and bottom navigation.
     * @param navController {@link NavController} of host fragment.
     */
    private void setupNavigation(NavController navController) {
        new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}