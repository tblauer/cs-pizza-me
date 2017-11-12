package com.tblauer.pizzame.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class for testing, will generate testable PizzaPlaces
 */
public class TestPizzaPlaceFactory {

    //-------------------------------------------------------------------------
    // Member variables

    private PizzaPlace TEST_PP;

    public static final String TEST_PP_NAME = "Pizza Day";
    public static final String TEST_PP_ADDRESS = "10225 Research Blvd, Ste 110";
    public static final String TEST_PP_CITY = "Austin";
    public static final String TEST_PP_STATE = "TX";
    public static final String TEST_PP_PHONE_NUM = "123-(512) 345-7492-7890";
    public static final double TEST_PP_LATITUDE = 30.39537;
    public static final double TEST_PP_LONGITUDE = -97.74545;
    public static final float TEST_PP_DISTANCE = 2.0f;
    public static final String TEST_PP_URL = "http://www.pizzadaytx.com/";

    public static final float expectedAvgRating = 3.5f;
    public static final int expectedNumRatings = 6;
    public static final int expectedNumReviews = 22;
    public static final long expectedLastReviewDate = 12345678;
    public static final String expectedReviewIntro = "It was awesome!";

    private static final PizzaPlace.PlaceRating expectedRating =
            new PizzaPlace.PlaceRating(expectedAvgRating,
                    expectedNumRatings,
                    expectedNumReviews,
                    expectedLastReviewDate,
                    expectedReviewIntro);

    //-------------------------------------------------------------------------
    // Static methods

    public static PizzaPlace createPizzaPlace() {
        return new PizzaPlace(TEST_PP_NAME,
                TEST_PP_ADDRESS,
                TEST_PP_CITY,
                TEST_PP_STATE,
                TEST_PP_PHONE_NUM,
                TEST_PP_LATITUDE,
                TEST_PP_LONGITUDE,
                TEST_PP_DISTANCE,
                TEST_PP_URL,
                expectedRating);
    }

    public static List<PizzaPlace> createPizzsPlaceList() {
        List<PizzaPlace> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(createPizzaPlace());
        }
        return list;
    }
}
