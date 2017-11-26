package com.tblauer.pizzame.viewmodel;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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


class LoadPizzaPlacesTask extends AsyncTask<Location, Void, Void> implements ResponseHandler<JSONObject> {

    private PlacesViewModel _viewModel = null;
    private WebService _theWebService;
    private String LOG_TAG = getClass().getName();

    public LoadPizzaPlacesTask(PlacesViewModel viewModel, WebService webService) {
        _viewModel = viewModel;
        _theWebService = webService;
    }

    @Override
    protected Void doInBackground(Location... params) {
        if (params.length == 1) {
            Location location = params[0];
            publishProgress();
            _theWebService.refreshPizzaPlaces(location, this);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void ...progress) {
        _viewModel.setIsLoadingData(true);
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
            // parse the response and set the list of places on the viewmodel

            Type collectionType = new TypeToken<List<PizzaPlace>>(){}.getType();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String resultsArrayStr = resultsArray.toString();
            List<PizzaPlace> pizzaPlaces = gson.fromJson(resultsArray.toString(), collectionType);
            // The response comes back on the UI thread, so we call setValue
            // If it was in the background thread, we would call postValue
            _viewModel.setPizzaPlaces(pizzaPlaces);
        }
        catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json response: " + ex.getMessage(), ex);
        }
        finally {
            _viewModel.setIsLoadingData(false);
        }
    }
}

