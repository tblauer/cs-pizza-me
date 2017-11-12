package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.tblauer.pizzame.R;
import com.tblauer.pizzame.model.PizzaPlace;

import java.util.Locale;


public class PlaceDetailsViewModel extends AndroidViewModel {

    //-------------------------------------------------------------------------
    // Member Variables

    private String _addressFormatStr = null;
    private String _numRatingsFormatStr = null;
    private String _numReviewsFormatStr = null;
    private String _distanceFormatStr = null;

    private final String LOG_TAG = getClass().getName();

    // Have both of these because we are using databinding in the xml, and DataBinding does
    // not seem to support LiveData yet
    // Don't *really& need it though cause data within the pizza place isn't going to change
    public MutableLiveData<PizzaPlace> _observablePizzaPlace = new MutableLiveData<PizzaPlace>();
    public PizzaPlace _pizzaPlace = null;



    //-------------------------------------------------------------------------
    // Constructor

    public PlaceDetailsViewModel(Application application) {
        super(application);
        Resources resources = application.getResources();
        _addressFormatStr = resources.getString(R.string.whole_address_format);
        _numRatingsFormatStr = resources.getString(R.string.num_ratings_format);
        _numReviewsFormatStr = resources.getString(R.string.num_reviews_format);
        _distanceFormatStr = resources.getString(R.string.distance_with_units_format);
    }

    //-------------------------------------------------------------------------
    // Public class methods

    public void setPizzaPlace(PizzaPlace pizzaPlace) {
        _pizzaPlace = pizzaPlace;
        _observablePizzaPlace.setValue(pizzaPlace);
    }

    public PizzaPlace getPizzaPlace() {
        return _pizzaPlace;
    }

    public LiveData<PizzaPlace> getObservablePizzaPlace() {
        return _observablePizzaPlace;
    }


    // A bunch of getters for Databinding
    public String getName() {
        return PizzaPlaceDisplayUtils.getName(_pizzaPlace);
    }

    public String getAddress() {
        return PizzaPlaceDisplayUtils.getFormattedAddressCityState(_pizzaPlace, _addressFormatStr);
    }

    public String getPhoneNumber() {
        return PizzaPlaceDisplayUtils.getPhoneNumber(_pizzaPlace);
    }

    public float getAverageRating() {
        return PizzaPlaceDisplayUtils.getAverageRating(_pizzaPlace);
    }

    public String getAverageRatingStr() {
        return PizzaPlaceDisplayUtils.getAverageRatingStr(_pizzaPlace);
    }

    public String getNumRatingsStr() {
        return PizzaPlaceDisplayUtils.getFormattedNumRatingsStr(_pizzaPlace, _numRatingsFormatStr);
    }

    public String getDistanceStr() {
        return PizzaPlaceDisplayUtils.getFormattedDistanceStr(_pizzaPlace, _distanceFormatStr);
    }

    public String getNumReviewsStr() {
        return PizzaPlaceDisplayUtils.getFormattedNumReviewsStr(_pizzaPlace, _numReviewsFormatStr);
    }

    public void onCallClicked(View view) {
        // Check to make sure the device can make phone calls
        PackageManager packageManager = getApplication().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (_pizzaPlace != null) {
                Log.d(LOG_TAG, "OnCallClicked");
                //   Uri uri = Uri.parse("tel:" + _pizzaPlace.getPhoneNumber());
                // Take this out when done testing, don't want to accentally keep calling pizza hut
                // Althought I am kinda hungry
                Uri uri = Uri.parse("tel:" + "(512) 924-5722");

                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                getApplication().getApplicationContext().startActivity(intent);
                // getApplication().getApplicationContext().startActivity();
            }
        }
    }

    public void onShowMapClicked(View view) {
        if (_pizzaPlace != null) {
            String uriStr = String.format(Locale.US, "geo:0,0?q=%3.5f,%3.5f (%s)",
                    _pizzaPlace.getLatitude(),
                    _pizzaPlace.getLongitude(),
                    _pizzaPlace.getName());

            Uri locUri = Uri.parse(uriStr);
            Intent intent = new Intent(Intent.ACTION_VIEW, locUri);
            intent.setPackage("com.google.android.apps.maps");
            getApplication().getApplicationContext().startActivity(intent);
        }
    }
}
