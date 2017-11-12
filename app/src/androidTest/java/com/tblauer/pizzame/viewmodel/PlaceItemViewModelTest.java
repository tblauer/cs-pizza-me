package com.tblauer.pizzame.viewmodel;

import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockResources;

import com.tblauer.pizzame.R;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.model.TestPizzaPlaceFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import android.databinding.Observable;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test class for PiaceItemViewModel
 */
@RunWith(AndroidJUnit4.class)
public class PlaceItemViewModelTest {

    //-------------------------------------------------------------------------
    // Member variables

    @Mock private MockResources mockResources;

    // Tells mockito to create the mocks based on the @Mock annotation
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PizzaPlace testPizzaPlace;

    private final static String NUM_RATINGS_FORMAT = "(%s)";
    private final static String CITY_STATE_FORMAT = "%s, %s";
    private final static String DISTANCE_FORMAT = "%.2f mi";

    private String TEST_NAME;
    private String TEST_ADDRESS;
    private String TEST_PHONE_NUM;
    private String TEST_CITY_STATE;
    private String TEST_DISTANCE;
    private String TEST_AVG_RATING_STR;
    private float TEST_AVG_RATING;
    private String TEST_NUM_RATINGS;

    //-------------------------------------------------------------------------
    // JUnitOverrides

    @Before
    public void setUp() throws Exception {

        testPizzaPlace = TestPizzaPlaceFactory.createPizzaPlace();

        TEST_NAME = PizzaPlaceDisplayUtils.getName(testPizzaPlace);
        TEST_ADDRESS = PizzaPlaceDisplayUtils.getAddress(testPizzaPlace);
        TEST_PHONE_NUM = PizzaPlaceDisplayUtils.getPhoneNumber(testPizzaPlace);
        TEST_CITY_STATE = PizzaPlaceDisplayUtils.getFormattedCityAndState(testPizzaPlace, CITY_STATE_FORMAT);
        TEST_DISTANCE = PizzaPlaceDisplayUtils.getFormattedDistanceStr(testPizzaPlace, DISTANCE_FORMAT);
        TEST_AVG_RATING = PizzaPlaceDisplayUtils.getAverageRating(testPizzaPlace);
        TEST_AVG_RATING_STR = PizzaPlaceDisplayUtils.getAverageRatingStr(testPizzaPlace);
        TEST_NUM_RATINGS = PizzaPlaceDisplayUtils.getFormattedNumRatingsStr(testPizzaPlace, NUM_RATINGS_FORMAT);
    }

    //-------------------------------------------------------------------------
    // Test Methods

    @Test
    public void setPizzaPlace() throws Exception {
        // This should fire a property change event when it gets set
        // Lets make sure that happens
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);

        // Set up the listener
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        testViewModel.addOnPropertyChangedCallback(callback);
        // Set the pizza place on the view model
        testViewModel.setPizzaPlace(testPizzaPlace);
        // Verify that the onPropertyChange callback got called with an Observable and propertyId
        verify(callback).onPropertyChanged(any(Observable.class), anyInt());
    }

    @Test
    public void getPizzaPlaceName() throws Exception {
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_NAME, equalTo(testViewModel.getName()));
    }


    @Test
    public void getAddress() throws Exception {
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_ADDRESS, equalTo(testViewModel.getAddress()));
    }

    @Test
    public void getCityAndState() throws Exception {
        when(mockResources.getString(R.string.city_state_format)).thenReturn(CITY_STATE_FORMAT);

        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_CITY_STATE, equalTo(testViewModel.getCityAndState()));
    }

    @Test
    public void getPhoneNumber() throws Exception {
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_PHONE_NUM, equalTo(testViewModel.getPhoneNumber()));
    }

    @Test
    public void getDistanceStr() throws Exception {
        when(mockResources.getString(R.string.distance_with_abbreviated_units_format)).thenReturn(DISTANCE_FORMAT);

        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_DISTANCE, equalTo(testViewModel.getDistanceStr()));
    }

    @Test
    public void getAverageRating() throws Exception {
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_AVG_RATING, equalTo(testViewModel.getAverageRating()));
    }

    @Test
    public void getAverageRatingStr() throws Exception {
        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_AVG_RATING_STR, equalTo(testViewModel.getAverageRatingStr()));
    }

    @Test
    public void getNumRatingsStr() throws Exception {

        when(mockResources.getString(R.string.num_ratings_format)).thenReturn(NUM_RATINGS_FORMAT);

        PlaceItemViewModel testViewModel = new PlaceItemViewModel(mockResources);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_NUM_RATINGS, equalTo(testViewModel.getNumRatingsStr()));
    }
}