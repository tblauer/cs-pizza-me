package com.tblauer.pizzame.view.ui.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tblauer.pizzame.R;
import com.tblauer.pizzame.utils.AppIntents;
import com.tblauer.pizzame.view.ui.fragments.PlaceDetailsFragment;
import com.tblauer.pizzame.view.ui.fragments.PlacesFragment;
import com.tblauer.pizzame.viewmodel.PlacesViewModel;


public class MainActivity extends AppCompatActivity
                          implements GoogleApiClient.ConnectionCallbacks,
                                     GoogleApiClient.OnConnectionFailedListener {


    //-------------------------------------------------------------------------
    // Member variables

    private GoogleApiClient _googleApiClient = null;
    private PlacesViewModel _placesViewModel = null;

    private static final int CONNECTION_FAILED_LOCATION_RESOLUTION_CODE = 200;
    private static final int REQUEST_CHECK_LOCATION_SETTINGS = 205;

    private final String LOG_TAG = getClass().getName();

    //-------------------------------------------------------------------------
    // Activity Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _placesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);

        _googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
               // .enableAutoManage(this,);
                .build();

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
        if (!_googleApiClient.isConnected()) {
            _googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_googleApiClient.isConnected()) {
            _googleApiClient.disconnect();
        }
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CONNECTION_FAILED_LOCATION_RESOLUTION_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // The connection had failed to connect, but it was resolvable
                    // If whatever got popped up was successful, then try to connect again
                    if (!_googleApiClient.isConnected()) {
                        _googleApiClient.connect();
                    }
                }
                break;
            case REQUEST_CHECK_LOCATION_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    checkLocationSettings();
                }
                else {
                    // TODO
                    // Pop up an alert dialog telling the user the app cannot function
                    // correctly without locations turned on
                    // Ok will launch the location settings, quit will kill the app
                    // The user declined to turn on location in the settings,
                    // ths application will not be able to do anything useful
                    // Pop up a dialog and alert the user, let them close the app
                }
                break;
        }
    }

    //-------------------------------------------------------------------------
    // GoogleApi.ConnectionCallbacks implementation

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // hope it comes back
    }

    //-------------------------------------------------------------------------
    // GoogleApi.OnConnectionFailedListener implementation

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILED_LOCATION_RESOLUTION_CODE);
            }
            catch (IntentSender.SendIntentException ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        }
        Log.e(LOG_TAG, "Failed to connect to googleAPIClient due to " + connectionResult.getErrorMessage());
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

    private void checkLocationSettings() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        // This will automatically remove the listener if the activity's onStop method is called
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception ex) {
                if (ex instanceof ApiException) {
                    int statusCode = ((ApiException) ex).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) ex;
                                resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_LOCATION_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException sendEx) {
                                Log.e(LOG_TAG, sendEx.getMessage(), sendEx);
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // We don't have a way to fix this so there's no need showing anything
                            // We can't connect to location services and there's nothing to resolve.
                            // Suppose we should pop up something alerting the user and let them exit the app
                            break;
                    }
                }
            }
        });
        // This will automatically remove the listener if the activity's onStop method is called
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                _placesViewModel.setLocationServicesEnabled(true);
            }
        });
    }
}

