package com.tblauer.pizzame.model;


import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class PizzaPlace {
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


    public PizzaPlace(String name, String address, String city, String state, String phone, double latitude, double longitude, float distance, String businessUrl, PlaceRating rating) {
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
    public PlaceRating getRating() { return _rating; }


    public class PlaceRating {
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

        private PlaceRating(float avgRating, int numRatings, int numReviews, long lastReviewDate, String lastReviewIntro) {
            _averageRating = avgRating;
            _numRatings = numRatings;
            _numReviews = numReviews;
            _lastReviewDate = lastReviewDate;
            _lastReviewIntro = lastReviewIntro;
        }

        public float getAverageRating() { return _averageRating; }
        public int getNumRatings() { return _numRatings; }
        public int getNumReviews() { return _numReviews; }
        public long getLastReviewDate() { return _lastReviewDate; }
        public String getLastReviewIntro() { return _lastReviewIntro; }
    }

}
