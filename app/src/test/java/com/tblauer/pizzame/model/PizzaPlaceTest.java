package com.tblauer.pizzame.model;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class PizzaPlaceTest {
    //-------------------------------------------------------------------------
    // Member variables

    private PizzaPlace TEST_PP;

    private String expectedName = "Tom";
    private String expectedAddress = "123 Sesame Street";
    private String expectedCity = "AnyTown";
    private String expectedState = "WI";
    private String expectedPhoneNum = "123-456-7890";
    private double expectedLat = 44.095807;
    private double expectedLon = -87.662055;
    private float expectedDistance = 2.0f;
    private String expectedBusinessUrl = "http://www.googlemaps.com/";

    private float expectedAvgRating = 3.5f;
    private int expectedNumRatings = 6;
    private int expectedNumReviews = 22;
    private long expectedLastReviewDate = 12345678;
    private String expectedReviewIntro = "It was awesome!";

    private PizzaPlace.PlaceRating expectedRating =
            new PizzaPlace.PlaceRating(expectedAvgRating,
                                        expectedNumRatings,
                                        expectedNumReviews,
                                        expectedLastReviewDate,
                                        expectedReviewIntro);

    //-------------------------------------------------------------------------
    // JUnit overrides

    @Before
    public void setUp() throws Exception {
        TEST_PP = new PizzaPlace(expectedName,
                                 expectedAddress,
                                 expectedCity,
                                 expectedState,
                                 expectedPhoneNum,
                                 expectedLat,
                                 expectedLon,
                                 expectedDistance,
                                 expectedBusinessUrl,
                                 expectedRating);
    }

    //-------------------------------------------------------------------------
    // Test methods

    @Test
    public void getName() throws Exception {
        assertThat(TEST_PP.getName(), equalTo(expectedName));
    }

    @Test
    public void getAddress() throws Exception {
        assertThat(TEST_PP.getAddress(), equalTo(expectedAddress));
    }

    @Test
    public void getCity() throws Exception {
        assertThat(TEST_PP.getCity(), equalTo(expectedCity));
    }

    @Test
    public void getState() throws Exception {
        assertThat(TEST_PP.getState(), equalTo(expectedState));
    }

    @Test
    public void getPhoneNumber() throws Exception {
        assertThat(TEST_PP.getPhoneNumber(), equalTo(expectedPhoneNum));
    }

    @Test
    public void getLatitude() throws Exception {
        assertThat(TEST_PP.getLatitude(), closeTo(expectedLat, .0005));
    }

    @Test
    public void getLongitude() throws Exception {
        assertThat(TEST_PP.getLongitude(), closeTo(expectedLon, .0005));
    }

    @Test
    public void getDistance() throws Exception {
        assertThat(TEST_PP.getDistance(), equalTo(expectedDistance));
    }

    @Test
    public void getBusinessUrl() throws Exception {
        assertThat(TEST_PP.getBusinessUrl(), equalTo(expectedBusinessUrl));
    }

    @Test
    public void getRating() throws Exception {
        assertThat(TEST_PP.getRating(), equalTo(expectedRating));
    }

}