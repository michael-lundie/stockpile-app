package io.lundie.stockpile;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    NavHostFragment navigationHost;

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

        navigationHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_nav_fragment_main);

        if(navigationHost != null) {
            NavController navController = navigationHost.getNavController();
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

    /**
     * Method adds a layout listener which calculates a difference in height between the root and
     * content view. If there is a significant height difference, this means a keyboard is
     * active. If a keyboard is not active, the bottom nav behavior is activated an the keyboard
     * displays. This overcomes issue with keyboard remaining hidden when a view is not scrollable.
     * Using a modified version of kotlin code found at:
     * https://stackoverflow.com/a/55314789
     */
    private void addKeyboardDetectListener() {
        View mainView = findViewById(R.id.host_nav_fragment_main);
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            float heightDiff = mainView.getRootView().getHeight() - mainView.getHeight();
            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) bottomNav.getLayoutParams();
            HideBottomNavigationOnScrollBehavior behavior =
                    (HideBottomNavigationOnScrollBehavior) params.getBehavior();
            if(!(heightDiff > dpToPixel(this, 200F))) {

                if (behavior != null) {
                    behavior.slideUp(bottomNav);
                }
            } else {
                behavior.slideDown(bottomNav);
            }
        });
    }

    private float dpToPixel(MainActivity mainActivity, float dpValue) {
        DisplayMetrics metrics = mainActivity.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        return Navigation.findNavController(this, R.id.host_nav_fragment_main).navigateUp()
//                || super.onSupportNavigateUp();
//    }

    private void updateUI(FirebaseUser currentUser) {

    }

    private void setupNavigation(NavController navController) {
        new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}