package com.tblauer.pizzame.viewmodel;

import android.util.Log;

import com.tblauer.pizzame.model.PizzaPlace;


public class PizzaPlaceDisplayUtils {

    // All methods are static, no need for a constructor
    private static final String EMPTY_STR = "";

    private PizzaPlaceDisplayUtils() {}

    public static String getName(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            return pizzaPlace.getName();
        }
        return EMPTY_STR;
    }

    public static float getAverageRating(PizzaPlace pizzaPlace) {
        if (pizzaPlace != null) {
            PizzaPlace.PlaceRating rating = pizzaPlace.getRating();
            if (rating != null) {
                float avg = rating.getAverageRating();
                if (!Float.isNaN(avg)) {
                    return avg;
                }
            }
        }
        return 0.0f;
    }

    public static String getAverageRatingStr(PizzaPlace pizzaPlace) {
        return String.valueOf(getAverageRating(pizzaPlace));
    }

    public static String getFormattedNumRatingsStr(PizzaPlace pizzaPlace, String formatStr) {
        if (pizzaPlace != null && formatStr != null) {
            PizzaPlace.PlaceRating rating = pizzaPlace.getRating();
            if (rating != null) {
                return String.format(formatStr,rating.getNumRatings());
            }
        }
        return String.valueOf(0);
    }

    public static String getFormattedNumReviewsStr(PizzaPlace pizzaPlace, String format) {
        if (pizzaPlace != null && format != null) {
            PizzaPlace.PlaceRating rating = pizzaPlace.getRating();
            if (rating != null) {
                return String.format(format, rating.getNumReviews());
            }
        }
        return String.valueOf(0);
    }

    public static String getFormattedDistanceStr(PizzaPlace pizzaPlace, String formatStr) {
        if (pizzaPlace != null && formatStr != null) {
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

    public static String getFormattedCityAndState(PizzaPlace pizzaPlace, String formatStr) {
        if (pizzaPlace != null && formatStr != null) {
            return String.format(formatStr, pizzaPlace.getCity(), pizzaPlace.getState());
        }
        return EMPTY_STR;
    }

    public static String getFormattedAddressCityState(PizzaPlace pizzaPlace, String formatStr) {
        if (pizzaPlace != null && formatStr != null) {
            return String.format(formatStr, pizzaPlace.getAddress(), pizzaPlace.getCity(), pizzaPlace.getState());
        }
        return EMPTY_STR;
    }
}
