package io.lundie.stockpile;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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