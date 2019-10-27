package io.lundie.stockpile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
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

    @BindView(R.id.toolbar) Toolbar actionToolbar;

    @BindView(R.id.bottom_nav) BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(actionToolbar);
        navigationHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_nav_fragment_main);

        if(navigationHost != null) {
            NavController navController = navigationHost.getNavController();
            setupBottomNavMenu(navController);
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

    private void setupBottomNavMenu(NavController navController) {
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}