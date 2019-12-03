package io.lundie.stockpile;

import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.lundie.stockpile.utils.layoutbehaviors.HideBottomNavigationOnScrollBehavior;

public class MainActivity extends DaggerAppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    NavHostFragment navigationHost;

    @Inject
    FirebaseAuth mAuth;

    @BindView(R.id.bottom_nav) BottomNavigationView bottomNav;
    @BindView(R.id.toolbar) Toolbar toolbar;

    //TODO: Replace with binding library - remove butterknife
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        navigationHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_nav_fragment_main);

        if(navigationHost != null) {
            NavController navController = navigationHost.getNavController();
            setupNavigation(navController);
        }
        addKeyboardDetectListener();
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
            if(!(heightDiff > dpToPixel(this, 200F))) {
                CoordinatorLayout.LayoutParams params =
                        (CoordinatorLayout.LayoutParams) bottomNav.getLayoutParams();
                HideBottomNavigationOnScrollBehavior behavior =
                        (HideBottomNavigationOnScrollBehavior) params.getBehavior();
                if (behavior != null) {
                    behavior.slideUp(bottomNav);
                }
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
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}