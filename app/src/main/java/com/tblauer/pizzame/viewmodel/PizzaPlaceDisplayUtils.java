package com.tblauer.pizzame.viewmodel;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tblauer.pizzame.model.PizzaPlace;


public class PizzaPlaceDisplayUtils {

    //-------------------------------------------------------------------------
    // Member variables

    private static final String EMPTY_STR = "";

    //-------------------------------------------------------------------------
    // Private constructor

    // All methods are static, no need for a constructor
    private PizzaPlaceDisplayUtils() {}

    //-------------------------------------------------------------------------
    // Static methods

    public static String getName(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            return pizzaPlace.getName();
        }
        return EMPTY_STR;
    }

    public static float getAverageRating(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            return pizzaPlace.getAverageRating();
        }
        return 0.0f;
    }

    public static String getAverageRatingStr(PizzaPlace pizzaPlace) {
        return String.valueOf(getAverageRating(pizzaPlace));
    }

    public static String getFormattedNumRatingsStr(PizzaPlace pizzaPlace, @NonNull String formatStr) {
        if (pizzaPlace != null) {
            int numRatings = pizzaPlace.getNumRatings();
            return String.format(formatStr, numRatings);
        }
        return String.valueOf(0);
    }

    public static String getFormattedNumReviewsStr(PizzaPlace pizzaPlace, @NonNull String format) {
        if (pizzaPlace != null) {
           int numReviews = pizzaPlace.getNumReviews();
           return String.format(format, numReviews);
        }
        return String.valueOf(0);
    }

    public static String getFormattedDistanceStr(PizzaPlace pizzaPlace, @NonNull String formatStr) {
        if (pizzaPlace != null) {
            return String.format(formatStr, pizzaPlace.getDistance());
        }
        return String.valueOf(0);
    }

    public static String getAddress(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            return pizzaPlace.getAddress();
        }
        return EMPTY_STR;
    }

    public static String getPhoneNumber(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            return pizzaPlace.getPhoneNumber();
        }
        return EMPTY_STR;
    }

    public static String getFormattedCityAndState(PizzaPlace pizzaPlace, @NonNull String formatStr) {
        if (pizzaPlace != null) {
            return String.format(formatStr, pizzaPlace.getCity(), pizzaPlace.getState());
        }
        return EMPTY_STR;
    }

    public static String getFormattedAddressCityState(PizzaPlace pizzaPlace, @NonNull String formatStr) {
        if (pizzaPlace != null) {
            return String.format(formatStr, pizzaPlace.getAddress(), pizzaPlace.getCity(), pizzaPlace.getState());
        }
        return EMPTY_STR;
    }
}
