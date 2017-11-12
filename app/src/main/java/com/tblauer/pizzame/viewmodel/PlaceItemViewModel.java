package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import android.arch.lifecycle.ViewModel;
import android.content.res.Resources;
import android.databinding.BaseObservable;

import com.tblauer.pizzame.R;
import com.tblauer.pizzame.model.PizzaPlace;



public class PlaceItemViewModel extends BaseObservable {

    private PizzaPlace _pizzaPlace;
    private final String LOG_TAG = getClass().getName();
    private String _distanceFormatStr = null;
    private String _cityStateFormatStr = null;
    private String _numRatingsFormatStr = null;


    public PlaceItemViewModel(Resources resources) {
     //   super(application);
     //   Resources resources = application.getResources();
        _distanceFormatStr = resources.getString(R.string.distance_with_abbreviated_units_format);
        _cityStateFormatStr = resources.getString(R.string.city_state_format);
        _numRatingsFormatStr = resources.getString(R.string.num_ratings_format);
    }

    // This will get called in the RecyclerView view binder
    public void setPizzaPlace(PizzaPlace pizzaPlace) {
        _pizzaPlace = pizzaPlace;
        notifyChange();
    }

 //   public PizzaPlace getPizzaPlace() {
 //       return _pizzaPlace;
 //   }

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
