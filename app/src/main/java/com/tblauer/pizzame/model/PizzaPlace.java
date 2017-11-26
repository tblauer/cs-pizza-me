package com.tblauer.pizzame.model;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * PizzaPlace model
 */
public class PizzaPlace {
    //------------------------------------------------------------------------
    // Member variables

    @SerializedName("id")
    private String id = null;

    @SerializedName("Title")
    private String _name = null;

    @SerializedName("Address")
    private String _address = null;

    @SerializedName("City")
    private String _city = null;

    @SerializedName("State")
    private String _state = null;

    @SerializedName("Phone")
    private String _phoneNumber = null;

    @SerializedName("Latitude")
    private double _lat = 0.0;

    @SerializedName("Longitude")
    private double _lon = 0.0;

    @SerializedName("Distance")
    private float _distance = 0.0f;

    @SerializedName("BusinessClickUrl")
    private String _url = null;

    @SerializedName("Rating")
    private PlaceRating _rating = null;

    //------------------------------------------------------------------------
    // Constructor

    public PizzaPlace(@NonNull String name,
                      @NonNull String address,
                      @NonNull String city,
                      @NonNull String state,
                      @NonNull String phone,
                      double latitude,
                      double longitude,
                      float distance,
                      String businessUrl,
                      @NonNull PlaceRating rating) {
        _name = name;
        _address = address;
        _city = city;
        _state = state;
        _phoneNumber = phone;
        _lat = latitude;
        _lon = longitude;
        _distance = distance;
        _url = businessUrl;
        _rating = rating;
    }

    //------------------------------------------------------------------------
    // Class Methods

    public String getName() {
        return _name;
    }
    public String getAddress() { return _address; }
    public String getCity() { return _city; }
    public String getState() { return _state; }
    public String getPhoneNumber() { return _phoneNumber; }
    public double getLatitude() { return _lat; }
    public double getLongitude() { return _lon; }
    public float getDistance() { return _distance; }
    public String getBusinessUrl() { return _url; }

    public float getAverageRating() {
        if (_rating != null) {
            float avg = _rating.getAverageRating();
            if (!Float.isNaN(avg)) {
                return avg;
            }
        }
        return 0.0f;
    }

    public int getNumRatings() {
        if (_rating != null) {
            return _rating.getNumRatings();
        }
        return 0;
    }

    public int getNumReviews() {
        if (_rating != null) {
            return _rating.getNumReviews();
        }
        return 0;
    }

    public long getLastReviewDate() {
        if (_rating != null) {
            return _rating.getLastReviewDate();
        }
        return 0;
    }

    public String getLastReviewIntro() {
        if (_rating != null) {
            return _rating.getLastReviewIntro();
        }
        return null;
    }

    public PlaceRating getRating() { return _rating; }

    //-------------------------------------------------------------------------
    // Private class for the Pizza Placd Rating model

    public static class PlaceRating {

        //----------------------------------------------------------------------
        // Mamber variables
        @SerializedName("AverageRating")
        private float _averageRating = 0.0f;

        @SerializedName("TotalRatings")
        private int _numRatings = 0;

        @SerializedName("TotalReviews")
        private int _numReviews = 0;

        @SerializedName("LastReviewDate")
        private long _lastReviewDate = 0;

        @SerializedName("LastReviewIntro")
        private String _lastReviewIntro = null;

        //----------------------------------------------------------------------
        // Constructor

        public PlaceRating(float avgRating, int numRatings, int numReviews, long lastReviewDate, String lastReviewIntro) {
            _averageRating = avgRating;
            _numRatings = numRatings;
            _numReviews = numReviews;
            _lastReviewDate = lastReviewDate;
            _lastReviewIntro = lastReviewIntro;
        }

        //----------------------------------------------------------------------
        // Class methods

        public float getAverageRating() { return _averageRating; }
        public int getNumRatings() { return _numRatings; }
        public int getNumReviews() { return _numReviews; }
        public long getLastReviewDate() { return _lastReviewDate; }
        public String getLastReviewIntro() { return _lastReviewIntro; }
    }

}
