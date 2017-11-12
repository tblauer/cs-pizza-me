package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.databinding.ObservableInt;
import android.location.Location;
import android.os.AsyncTask;
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
import java.util.List;


public class PlacesViewModel extends AndroidViewModel {

    //-------------------------------------------------------------------------
    // Member variables

    private WebService _webService;

    // May have to create a new object that's LiveData that contains the items I want to make visible
    private final ObservableInt _progressVisible = new ObservableInt(View.GONE);
    private final ObservableInt _recyclerViewVisible = new ObservableInt(View.GONE);
    private final ObservableInt _emptyViewVisible = new ObservableInt(View.VISIBLE);
//    private final ObservableBoolean _swipedToRefresh = new ObservableBoolean(false);

    private MutableLiveData<Location> _currentLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> _locationRequested = new MutableLiveData<>();

    private MutableLiveData<List<PizzaPlace>> _pizzaPlaces = new MutableLiveData<>();


    private final String LOG_TAG = getClass().getName();

    //-------------------------------------------------------------------------
    // Constructor

    public PlacesViewModel(Application application) {
        super(application);
        _webService = new WebService(application.getApplicationContext());
        _locationRequested.setValue(false);
 //       _swipedToRefresh.setValue(false);
    }

    //-------------------------------------------------------------------------
    // Class methods


    public LiveData<List<PizzaPlace>> getPizzaPlaces() {
        if (_pizzaPlaces == null) {
            _pizzaPlaces = new MutableLiveData<>();
        }
        return _pizzaPlaces;
    }

    public LiveData<Boolean> getLocationRequested() {
        return _locationRequested;
    }

//   public LiveData<Boolean> getSwipedToRefresh() { return _swipedToRefresh; }

    public ObservableInt getProgressVisible() {
        return _progressVisible;
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
    //    _swipedToRefresh.setValue(true);
        _locationRequested.setValue(true);
    }


    public void onFABRefreshCalled() {
        _locationRequested.setValue(true);
    }

    public void refreshPizzaPlaces(Location location) {
        if (_currentLocation.getValue() != null) {
            loadDataFromWebService(_currentLocation.getValue(), _pizzaPlaces);
        }
    }

    //-------------------------------------------------------------------------
    // Private class methods

    private void loadDataFromWebService(Location location, MutableLiveData<List<PizzaPlace>> data) {
        // We need to call the WebService from a background thread
        // Volley will return the results in the foreground

        setProgressVisibility(true);
        new LoadDataTask(data).execute(location);
    }

    private void setProgressVisibility(boolean inProgress) {
        if (inProgress) {
            // If the user swiped to refresh, the swipe layout has it's own progress bar
            // don't show it again
        //    if (!_swipedToRefresh.getValue()) {
                _progressVisible.set(View.VISIBLE);
        //    }
        }
        else {
       //     if (_swipedToRefresh.getValue()) {
       //         _swipedToRefresh.setValue(false);
       //     }
        //    else {
                _progressVisible.set(View.GONE);
        //    }
        }
    }
    //-------------------------------------------------------------------------
    // Private helper classes

     private class LoadDataTask extends AsyncTask<Location, Void, Void> implements ResponseHandler<JSONObject> {

        private MutableLiveData<List<PizzaPlace>> _data;
        private LoadDataTask(MutableLiveData<List<PizzaPlace>> data) {
            _data = data;
        }

        @Override
        protected Void doInBackground(Location... params) {
            if (params.length == 1) {
                Location location = params[0];
                _webService.refreshPizzaPlaces(location, this);
            }
            return null;
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            // We should alert the user if this is an error they can do anything about

            // Stop displaying progress
            setProgressVisibility(false);
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
                _data.setValue(pizzaPlaces);

                // stop displaying progress
                setProgressVisibility(false);

                if (pizzaPlaces.size() > 0) {
                    _emptyViewVisible.set(View.GONE);
                    _recyclerViewVisible.set(View.VISIBLE);
                }
                else {
                    _recyclerViewVisible.set(View.GONE);
                    _emptyViewVisible.set(View.VISIBLE);
                }
            }
            catch (JSONException ex) {
                Log.e(LOG_TAG, "Error parsing json response: " + ex.getMessage(), ex);
            }
        }
    }
}
