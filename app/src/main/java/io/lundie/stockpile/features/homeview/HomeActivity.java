package io.lundie.stockpile.features.homeview;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.lundie.stockpile.R;

public class HomeActivity extends DaggerAppCompatActivity {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //TODO: Add fragment ID.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_content_home, new HomeFragment())
                .commit();
    }


    private void testObserver() {
        homeViewModel.getTestLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    Log.i(LOG_TAG, s);
                }
            }
        });
    }
}
