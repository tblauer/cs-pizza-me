package com.tblauer.pizzame.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tblauer.pizzame.SingleLiveEvent;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.model.dataprovider.WebService;
import com.tblauer.pizzame.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.tblauer.pizzame.utils.AppRequestCodes.REQUEST_CHECK_LOCATION_SETTINGS;


public class PlacesViewModel extends AndroidViewModel {

    //-------------------------------------------------------------------------
    // Member variables

    private WebService _webService;
    private FusedLocationProviderClient _locationProviderClient = null;

    private Location _currentLocation = null;

    private final ObservableInt _progressViewVisible = new ObservableInt(View.GONE);
    private final ObservableInt _recyclerViewVisible = new ObservableInt(View.GONE);
    private final ObservableInt _emptyViewVisible = new ObservableInt(View.VISIBLE);

    private MutableLiveData<Boolean> _swipedToRefreshProgressVisible = new MutableLiveData<>();
    private MutableLiveData<List<PizzaPlace>> _pizzaPlaces = new MutableLiveData<>();

    // Location specific
    private MutableLiveData<Boolean> _locationPermissionsRequested = new MutableLiveData<>();
    private ObservableBoolean _locationSettingsEnabled = new ObservableBoolean(false);
    private SingleLiveEvent<Boolean> _userDeniedLocationServices = new SingleLiveEvent<>();
    private LocationSettingsFailedMessage _locationSettingsFailedMsg = new LocationSettingsFailedMessage();

    private LocationCallback _myLocationCallback = null;

    //-------------------------------------------------------------------------
    // Constructor

    public PlacesViewModel(Application application) {
        super(application);
        _swipedToRefreshProgressVisible.setValue(false);
        _locationPermissionsRequested.setValue(false);
        _userDeniedLocationServices.setValue(false);
        _locationSettingsFailedMsg.setValue(null);


        // Listen to changes in LocationServicesEnabled,
        // If it's true, it means we have checked, and the device has locations turned on in the
        // settings and we should be able to request locations
        _locationSettingsEnabled.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (_locationSettingsEnabled.get()) {
                    requestNewLocation();
                }
                else {
                    // We may have checked due to a swipe to refresh, if we did
                    // and we cannot request locations, then reset the swipeToRefresh
                    // because it will have already displayed it's own progress
                    turnSwipeToRefreshProgressOffIfOn();
                }
            }
        });
    }

    //-------------------------------------------------------------------------
    // Class methods


    /**
     * Should be observed to determine when the list of pizza places has changed
     * @return LiveData for the list of available pizza places
     */
    public LiveData<List<PizzaPlace>> getPizzaPlaces() {
        if (_pizzaPlaces == null) {
            _pizzaPlaces = new MutableLiveData<>();
        }
        return _pizzaPlaces;
    }

    /**
     * Should be observed to determine when to set a SwipeRefreshLayout's refreshing state
     * It gets turned on when the viewmodel gets told the user swiped, we need to ensure it gets
     * turned back off either because we cannot refresh at this time, or when the refresh is complete
     * @return LiveData for a SwipeRefreshLayout refreshing state.
     */
    public LiveData<Boolean> getSwipedToRefreshProgressVisible() { return _swipedToRefreshProgressVisible; }

    /**
     * Should get called during the onStart of the view that is dependent on locations
     */
    public void onLocationDependentViewStarted() {
        checkLocationSettings();
    }

    /**
     * Should get called in onStop of the view that is dependent on locations
     */
    public void onLocationDependentViewStopped() {
        // Just in case we are still listening for location updates, stop listening
        stopLocationUpdates();
    }

    /**
     * Method that should get called in response to the SwipeRefreshLayout request to refresh
     */
    public void onSwipeToRefreshCalled() {
        // The user wants to refresh the list of pizza places
        // We want to make the request with an updated location, so let's request that
        // and when we get one we will make the request to get an updated list

        _swipedToRefreshProgressVisible.setValue(true);
        requestNewLocation();
    }


    /**
     * Method that should get called in response to the user pressing a refresh button
     */
    public void onFABRefreshCalled() {
        requestNewLocation();
    }


    /**
     * Observable used to request UI be displayed to allow the app to use the devices location
     * When the value is set to true, the observer should display UI requesting location permissions
     */
    public LiveData<Boolean> getLocationPermissionsRequested() {
        return _locationPermissionsRequested;
    }

    /**
     * Observable to request UI be displayed to handle a ResolvableApiException
     * @return the LocationSettingsFailedMessage to be observed
     */
    public LocationSettingsFailedMessage getLocationSettingsFailedMessge() {
        return _locationSettingsFailedMsg;
    }

    /**
     * Observable to request UI be displayed to handle the user denying location settings
     * being turned on
     * @return the LiveData to be observed
     */
    public LiveData<Boolean> getUserDeniedLocationServicesEvent() {
        return _userDeniedLocationServices;
    }

    // Databinding Observable for  progress view visibility
    public ObservableInt getProgressViewVisible() {
        return _progressViewVisible;
    }

    // Databinding Observable for recycler view visibility
    public ObservableInt getRecyclerViewVisible() {
        return _recyclerViewVisible;
    }

    // Databinding Observable for emptylayout Visibility
    public ObservableInt getEmptyLayoutVisible() {
        return _emptyViewVisible;
    }

    /**
     * Method called to indicate when the app has started and finished loading data, it is used
     * to set the visibility of progress while data is being loaded.
     * @param isLoadingData <code>true</code> if the app is currently retrieving data and
     *                      <code>false></code> if it's done loading data
     */
    public void setIsLoadingData(boolean isLoadingData) {
        Boolean swipeProgressIsVisible = _swipedToRefreshProgressVisible.getValue();
        if (swipeProgressIsVisible == null) swipeProgressIsVisible = Boolean.FALSE;

        if (isLoadingData) {
            // If the user swiped to refresh, the swipe layout has it's own progress bar
            // don't show it again
            if (!swipeProgressIsVisible) {
                _progressViewVisible.set(View.VISIBLE);
            }
        }
        else {
            if (swipeProgressIsVisible) {
                _swipedToRefreshProgressVisible.setValue(false);
            }
            else {
                _progressViewVisible.set(View.GONE);
            }
        }
    }

    public void handleRequestPermissionsResult(int requestCode,
                                               @NonNull String permissions[],
                                               @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSIONS_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        requestNewLocation();
                    }
                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        turnSwipeToRefreshProgressOffIfOn();
                    }
                }
                break;
        }
    }

    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_LOCATION_SETTINGS:
                // The user turned on location services in the settings, let's go through the check again
                if (resultCode == Activity.RESULT_OK) {
                    _userDeniedLocationServices.setValue(false);
                    checkLocationSettings();
                }
                else {
                    turnSwipeToRefreshProgressOffIfOn();
                    _userDeniedLocationServices.setValue(true);
                }
                break;
        }
    }


    //-------------------------------------------------------------------------
    // Public for testing

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public void setPizzaPlaces(List<PizzaPlace> places) {
        List<PizzaPlace> pplaces = places;
        if (pplaces == null) {
            pplaces = new ArrayList<>();
        }

        _pizzaPlaces.setValue(pplaces);

        // Now update the visibility
        if (pplaces.isEmpty()) {
            _emptyViewVisible.set(View.VISIBLE);
            _recyclerViewVisible.set(View.GONE);
        }
        else {
            _recyclerViewVisible.set(View.VISIBLE);
            _emptyViewVisible.set(View.GONE);
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setLocationPermissionsRequested(Boolean permissionsRequested) {
        _locationPermissionsRequested.setValue(permissionsRequested);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setCurrentLocation(Location location) {
        _currentLocation = location;
        if (location != null) {
            refreshPizzaPlaces(_currentLocation);
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setSwipedToRefreshProgressVisible(boolean swipedToRefreshProgressVisible) {
        _swipedToRefreshProgressVisible.setValue(swipedToRefreshProgressVisible);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setProgressViewVisible(int visibility){
        _progressViewVisible.set(visibility);
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setRecyclerViewVisibility(int visibility) {
        _recyclerViewVisible.set(visibility);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setEmptyLayoutVisibility(int visibility) {
        _emptyViewVisible.set(visibility);
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void refreshPizzaPlaces(Location location) {
        if (location != null) {
            loadDataFromWebService(location);
        }
    }

    //-------------------------------------------------------------------------
    // Private class methods

    private void turnSwipeToRefreshProgressOffIfOn() {
        Boolean swipeProgressIsVisible = _swipedToRefreshProgressVisible.getValue();
        if ((swipeProgressIsVisible != null) && swipeProgressIsVisible) {
            _swipedToRefreshProgressVisible.setValue(false);
        }
    }
    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (_locationProviderClient == null) {
            _locationProviderClient = new FusedLocationProviderClient(getApplication().getApplicationContext());
        }
        return _locationProviderClient;
    }

    @SuppressWarnings({"MissingPermission"})
    private void requestNewLocation() {
        // If we have already checked and location services are turned on in settings
        if (_locationSettingsEnabled.get()) {
            // check if we have permissions
            if (PermissionUtils.hasPermission(getApplication().getApplicationContext(), PermissionUtils.WhichPermission.REQUEST_LOCATION)) {
                Task<Location> task = getFusedLocationProviderClient().getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    public void onSuccess(Location location) {
                        if (location == null) {
                            startLocationUpdates();
                        }
                        setCurrentLocation(location);
                    }
                });
            }
            else {
                // We don't have permissions, need to ask for them
                setLocationPermissionsRequested(true);
            }
        }
        else {
            // Need to check of location services is turned on in settings
            checkLocationSettings();
        }
    }

    private void loadDataFromWebService(Location location) {
        // We need to call the WebService from a background thread

        if (_webService == null) {
            _webService = new WebService(getApplication().getApplicationContext());
        }

        new LoadPizzaPlacesTask(this, _webService).execute(location);
    }


    @SuppressWarnings({"MissingPermission"})
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (_myLocationCallback == null) {
            _myLocationCallback = new MyLocationCallback();
        }

        if (PermissionUtils.hasPermission(getApplication().getApplicationContext(), PermissionUtils.WhichPermission.REQUEST_LOCATION)) {
            getFusedLocationProviderClient().requestLocationUpdates(locationRequest, _myLocationCallback, Looper.myLooper());
        }
    }

    private void stopLocationUpdates() {
        if (_myLocationCallback != null) {
            getFusedLocationProviderClient().removeLocationUpdates(_myLocationCallback);
        }
    }


    private void checkLocationSettings() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getApplication().getApplicationContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception ex) {
                _locationSettingsEnabled.set(false);
                if (ex instanceof ApiException) {
                    int statusCode = ((ApiException) ex).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            _locationSettingsFailedMsg.setValue(resolvable);
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

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                _locationSettingsEnabled.set(true);
            }
        });
    }


    //-------------------------------------------------------------------------
    // Private helper classes

    private class MyLocationCallback extends LocationCallback {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            // This will only get called if getLastLocation returns null, in which case
            // we want to force a location update, as soon as we get one, we can stop
            Location location = locationResult.getLastLocation();
            if (location != null) {
                stopLocationUpdates();
                setCurrentLocation(location);
            }
        }
    }
}
