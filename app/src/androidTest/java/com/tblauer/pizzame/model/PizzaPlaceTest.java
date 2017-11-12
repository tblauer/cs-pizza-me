package com.tblauer.pizzame.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;


/**
 * Test for the PizzaPlace model that will test the Gson capabilities of the class
 */

public class PizzaPlaceTest {

    //-------------------------------------------------------------------------
    // Member variables

    private PizzaPlace GSON_TEST_PP;

    private static final String expectedName = "Pizza Hut";
    private static final String expectedAddress = "12636 Research Blvd, Ste a10";
    private static final String expectedCity = "Austin";
    private static final String expectedState = "TX";
    private static final String expectedPhoneNum = "(512) 257-8686";
    private static final double expectedLat = 30.43077;
    private static final double expectedLon = -97.76458;
    private static final float expectedDistance = 2.26f;
    private static final String expectedBusinessUrl = "http://www.pizzahut.com/";

    private static final float expectedAvgRating = 3f;
    private static final int expectedNumRatings = 2;
    private static final int expectedNumReviews = 2;
    private static final long expectedLastReviewDate = 1228598641;
    private static final String expectedReviewIntro = "It was amazing!";

    private static final PizzaPlace.PlaceRating expectedRating = new PizzaPlace.PlaceRating(expectedAvgRating, expectedNumRatings, expectedNumReviews, expectedLastReviewDate, expectedReviewIntro);

    //-------------------------------------------------------------------------
    // JUnit overrides

    @Before
    public void setUp() throws Exception {

         String responseStr =
                "{" +
                        "\"id\": \"19431733\"," +
                        "\"xmlns\": \"urn:yahoo:lcl\"," +
                        "\"Title\": \"Pizza Hut\"," +
                        "\"Address\": \"12636 Research Blvd, Ste a10\"," +
                        "\"City\": \"Austin\"," +
                        "\"State\": \"TX\"," +
                        "\"Phone\": \"(512) 257-8686\"," +
                        "\"Latitude\": \"30.43077\"," +
                        "\"Longitude\": \"-97.76458\"," +
                        "\"Rating\": {" +
                            "\"AverageRating\": \"3\"," +
                            "\"TotalRatings\": \"2\"," +
                            "\"TotalReviews\": \"2\"," +
                            "\"LastReviewDate\": \"1228598641\"," +
                            "\"LastReviewIntro\": \"It was amazing\" }," +
                        "\"Distance\": \"2.26\"," +
                        "\"Url\": \"https://local.yahoo.com/info-19431733-pizza-hut-austin\"," +
                        "\"ClickUrl\": \"https://local.yahoo.com/info-19431733-pizza-hut-austin\"," +
                        "\"MapUrl\": \"https://local.yahoo.com/info-19431733-pizza-hut-austin?viewtype=map\"," +
                        "\"BusinessUrl\": \"http://www.pizzahut.com/\"," +
                        "\"BusinessClickUrl\": \"http://www.pizzahut.com/\"," +
                        "\"Categories\": {" +
                            "\"Category\": [" +
                            "{" +
                                "\"id\": \"96926243\"," +
                                "\"content\": \"Pizza\"" +
                            "}" +
                            "]" +
                        "}" +
                "}";

        Type theType = new TypeToken<PizzaPlace>(){}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        GSON_TEST_PP = gson.fromJson(responseStr, theType);
    }

    //-------------------------------------------------------------------------
    // Member variables

    @Test
    public void getName() throws Exception {
        // Gson
        assertThat(GSON_TEST_PP.getName(), equalTo(expectedName));
    }

    @Test
    public void getAddress() throws Exception {
        assertThat(GSON_TEST_PP.getAddress(), equalTo(expectedAddress));
    }

    @Test
    public void getCity() throws Exception {
        assertThat(GSON_TEST_PP.getCity(), equalTo(expectedCity));

    }

    @Test
    public void getState() throws Exception {
        assertThat(GSON_TEST_PP.getState(), equalTo(expectedState));
    }

    @Test
    public void getPhoneNumber() throws Exception {
        assertThat(GSON_TEST_PP.getPhoneNumber(), equalTo(expectedPhoneNum));
    }

    @Test
    public void getLatitude() throws Exception {
        assertThat(GSON_TEST_PP.getLatitude(), closeTo(expectedLat, .0005));
    }

    @Test
    public void getLongitude() throws Exception {
        assertThat(GSON_TEST_PP.getLongitude(), closeTo(expectedLon, .0005));
    }

    @Test
    public void getDistance() throws Exception {
        assertThat(GSON_TEST_PP.getDistance(), equalTo(expectedDistance));
    }

    @Test
    public void getBusinessUrl() throws Exception {
        assertThat(GSON_TEST_PP.getBusinessUrl(), equalTo(expectedBusinessUrl));
    }

}