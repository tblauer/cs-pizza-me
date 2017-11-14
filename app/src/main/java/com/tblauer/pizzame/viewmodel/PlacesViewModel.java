package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.model.dataprovider.WebService;
import com.tblauer.pizzame.utils.ResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PlacesViewModel extends AndroidViewModel {

    //-------------------------------------------------------------------------
    // Member variables

    private WebService _webService;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public final ObservableInt _progressViewVisible = new ObservableInt(View.GONE);
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public final ObservableInt _recyclerViewVisible = new ObservableInt(View.GONE);
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public final ObservableInt _emptyViewVisible = new ObservableInt(View.VISIBLE);
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public final ObservableBoolean _isInProgress = new ObservableBoolean(false);

    private MutableLiveData<Boolean> _swipedToRefresh = new MutableLiveData<>();

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public MutableLiveData<Location> _currentLocation = new MutableLiveData<>();

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public MutableLiveData<Boolean> _locationRequested = new MutableLiveData<>();

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public MutableLiveData<List<PizzaPlace>> _pizzaPlaces = new MutableLiveData<>();

    private final String LOG_TAG = getClass().getName();

    //-------------------------------------------------------------------------
    // Constructor

    public PlacesViewModel(Application application) {
        super(application);
        _locationRequested.setValue(false);
        _isInProgress.set(false);
        _swipedToRefresh.setValue(false);

        // Listen for changes the isInProgress state and toggle the
        // visibility for whatever progress is getting displayed
        _isInProgress.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                setIsLoadingData(_isInProgress.get());
            }
        });
    }

    //-------------------------------------------------------------------------
    // Class methods


    public LiveData<List<PizzaPlace>> getPizzaPlaces() {
        if (_pizzaPlaces == null) {
            _pizzaPlaces = new MutableLiveData<>();
        }
        return _pizzaPlaces;
    }

    public void setPizzaPlaces(List<PizzaPlace> places) {
        if (places == null) {
            _pizzaPlaces.setValue(new ArrayList<PizzaPlace>());
        }
        else {
            _pizzaPlaces.setValue(places);
        }

        // Now update the visibiilty
        if (places.isEmpty()) {
            _emptyViewVisible.set(View.VISIBLE);
            _recyclerViewVisible.set(View.GONE);
        }
        else {
            _recyclerViewVisible.set(View.VISIBLE);
            _emptyViewVisible.set(View.GONE);
        }
    }

    public LiveData<Boolean> getLocationRequested() {
        return _locationRequested;
    }

    public LiveData<Boolean> getSwipedToRefresh() { return _swipedToRefresh; }

    public ObservableInt getProgressViewVisible() {
        return _progressViewVisible;
    }
    public ObservableInt getRecyclerViewVisible() {
        return _recyclerViewVisible;
    }
    public ObservableInt getEmptyLayoutVisible() {
        return _emptyViewVisible;
    }


    public void setCurrentLocation(Location location) {

            _currentLocation.setValue(location);
            if ((_locationRequested.getValue() != null &&_locationRequested.getValue()) || (_pizzaPlaces.getValue() == null) || (_pizzaPlaces.getValue().isEmpty())) {
                if (location != null) {
                    refreshPizzaPlaces(_currentLocation.getValue());
                }
            }
            // If we had requested a location, we got it
            if (_locationRequested.getValue()) {
                _locationRequested.setValue(false);
            }
    }


    public void onSwipeToRefreshCalled() {
        // The user wants to refresh the list of pizza places
        // We want to make the request with an updated location, so let's request that
        // and when we get one we will make the request to get an updated list
        _swipedToRefresh.setValue(true);
        _locationRequested.setValue(true);
    }


    public void onFABRefreshCalled() {
        _locationRequested.setValue(true);
    }

    //-------------------------------------------------------------------------
    // Private class methods

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void refreshPizzaPlaces(Location location) {
        if (location != null) {
            loadDataFromWebService(location, _pizzaPlaces);
        }
    }

    private void loadDataFromWebService(Location location, MutableLiveData<List<PizzaPlace>> data) {
        // We need to call the WebService from a background thread
        // Volley will return the results in the foreground

        _isInProgress.set(true);
        if (_webService == null) {
            _webService = new WebService(getApplication().getApplicationContext());
        }

        new LoadDataTask(this, _webService).execute(location);
    }

    private void setIsLoadingData(boolean isLoadingData) {
        if (isLoadingData) {
            // If the user swiped to refresh, the swipe layout has it's own progress bar
            // don't show it again
            if (!_swipedToRefresh.getValue()) {
                _progressViewVisible.set(View.VISIBLE);
            }
        }
        else {
            if (_swipedToRefresh.getValue()) {
                _swipedToRefresh.setValue(false);
            }
            else {
                _progressViewVisible.set(View.GONE);
            }
        }
    }
    //-------------------------------------------------------------------------
    // Private helper classes

     private static class LoadDataTask extends AsyncTask<Location, Void, Void> implements ResponseHandler<JSONObject> {

        private PlacesViewModel _viewModel = null;
        private WebService _theWebService;
        private String LOG_TAG = getClass().getName();

        private LoadDataTask(PlacesViewModel viewModel, WebService webService) {
             _viewModel = viewModel;
            _theWebService = webService;
        }

        @Override
        protected Void doInBackground(Location... params) {
            if (params.length == 1) {
                Location location = params[0];
                _theWebService.refreshPizzaPlaces(location, this);
            }
            return null;
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            // We should alert the user if this is an error they can do anything about

            _viewModel.setIsLoadingData(false);
            Log.d(LOG_TAG, error.getLocalizedMessage(), error.getCause());
        }

        @Override
        public void onResponse(JSONObject response) {
            // We are going to set the return object live data so the viewModel gets told
            // it got updated
            try {
                JSONObject query = response.getJSONObject("query");
                JSONObject results = query.getJSONObject("results");
                JSONArray resultsArray = results.getJSONArray("Result");
                // parse the response and
                //  _data.setValue(resulting list);
                Type collectionType = new TypeToken<List<PizzaPlace>>(){}.getType();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                String resultsArrayStr = resultsArray.toString();
                List<PizzaPlace> pizzaPlaces = gson.fromJson(resultsArray.toString(), collectionType);
                // The response comes back on the UI thread, so we call setValue
                // If it was in the background thread, we would call postValue
            //    __data.setValue(pizzaPlaces);
                _viewModel.setPizzaPlaces(pizzaPlaces);

                // stop displaying progress
               // _viewModel.setProgressVisibility(false);

            }
            catch (JSONException ex) {
                Log.e(LOG_TAG, "Error parsing json response: " + ex.getMessage(), ex);
            }
            finally {
                _viewModel.setIsLoadingData(false);
            }
        }
    }
}
