package com.tblauer.pizzame.view.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;


import com.tblauer.pizzame.R;
import com.tblauer.pizzame.utils.AppIntents;
import com.tblauer.pizzame.view.ui.fragments.PlaceDetailsFragment;
import com.tblauer.pizzame.view.ui.fragments.PlacesFragment;


public class MainActivity extends AppCompatActivity {


    //-------------------------------------------------------------------------
    // Member variables

    private final String LOG_TAG = getClass().getName();

    //-------------------------------------------------------------------------
    // Activity Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new PlacesFragment());
            ft.commit();

            // If this is the first time through, handle the intent
            handleIntent(getIntent());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    //-------------------------------------------------------------------------
    // Private methods

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (TextUtils.isEmpty(action)) {
            return ;
        }

        if (action.equals(AppIntents.SHOW_PLACE_DETAILS_ACTION)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new PlaceDetailsFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}

