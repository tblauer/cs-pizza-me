package com.tblauer.pizzame.model.dataprovider;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.tblauer.pizzame.utils.ResponseHandler;
import com.tblauer.pizzame.utils.VolleyNetworkApi;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;



public class WebService {

    private static final String LOG_TAG = WebService.class.getName();
    private static final String YAHOO_URL = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json&diagnostics=true&callback=";
    private static final String LAT_LON_QUERY_FORMAT = "select * from local.search where latitude='%3.5f' and longitude='%3.5f' and query='pizza'";

    private VolleyNetworkApi _volleyApi = null;

    public WebService(Context appContext) {
        _volleyApi = VolleyNetworkApi.getInstance(appContext.getApplicationContext().getCacheDir());
    }

    // This should be called from a background thread
    public void refreshPizzaPlaces(Location location,
                                   ResponseHandler responseHandler) {
        String uriStr = buildURI(location.getLatitude(), location.getLongitude());
        _volleyApi.doJsonObjectRequest(VolleyNetworkApi.HttpMethod.GET,
                                        uriStr,
                                        new JSONObject(),
                                        responseHandler);
    }

    //-------------------------------------------------------------------------
    // private methods

    private String buildURI(double latitude, double longitude) {
        // Format the query with values
        String queryStr = String.format(Locale.US, LAT_LON_QUERY_FORMAT, latitude, longitude);

        try {
            // Encode the query, then add it to the whole url
            String urlQuery = URLEncoder.encode(queryStr, "UTF-8");
            return String.format(YAHOO_URL, urlQuery).replaceAll("\\+", "%20");
        }
        catch (UnsupportedEncodingException ex) {
            Log.e(LOG_TAG, "Failed to encode the urlString + " + queryStr + " due to: " + ex.getLocalizedMessage(), ex);
        }
        // Return the query without encoding, most likely won't work but will get an error message
        return String.format(YAHOO_URL, queryStr);
    }

}
