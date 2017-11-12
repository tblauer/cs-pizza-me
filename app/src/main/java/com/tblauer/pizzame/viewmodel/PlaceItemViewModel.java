package com.tblauer.pizzame.viewmodel;

import android.content.res.Resources;
import android.databinding.BaseObservable;

import com.tblauer.pizzame.R;
import com.tblauer.pizzame.model.PizzaPlace;

public class PlaceItemViewModel extends BaseObservable {

    //-------------------------------------------------------------------------
    // Member variables

    private PizzaPlace _pizzaPlace;
    private final String LOG_TAG = getClass().getName();
    private String _distanceFormatStr = null;
    private String _cityStateFormatStr = null;
    private String _numRatingsFormatStr = null;


    //-------------------------------------------------------------------------
    // Constructor

    public PlaceItemViewModel(Resources resources) {
        _distanceFormatStr = resources.getString(R.string.distance_with_abbreviated_units_format);
        _cityStateFormatStr = resources.getString(R.string.city_state_format);
        _numRatingsFormatStr = resources.getString(R.string.num_ratings_format);
    }

    //-------------------------------------------------------------------------
    // Class methods


    // This will get called in the RecyclerView view binder
    public void setPizzaPlace(PizzaPlace pizzaPlace) {
        _pizzaPlace = pizzaPlace;
        notifyChange();
    }


    // Getters for DataBinding
    public String getName() {
        return PizzaPlaceDisplayUtils.getName(_pizzaPlace);
    }

    public String getAddress() {
        return PizzaPlaceDisplayUtils.getAddress(_pizzaPlace);
    }

    public String getCityAndState() {
        return PizzaPlaceDisplayUtils.getFormattedCityAndState(_pizzaPlace, _cityStateFormatStr);
    }

    public String getPhoneNumber() {
        return PizzaPlaceDisplayUtils.getPhoneNumber(_pizzaPlace);
    }

    public String getDistanceStr() {
        return PizzaPlaceDisplayUtils.getFormattedDistanceStr(_pizzaPlace, _distanceFormatStr);
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
}
