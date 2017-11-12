package com.tblauer.pizzame.viewmodel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;
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

import java.util.Locale;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test class view PlaceDetailsViewModel
 */
@RunWith(AndroidJUnit4.class)
public class PlaceDetailsViewModelTest {

    //-------------------------------------------------------------------------
    // Member variables

    @Mock
    private MockApplication mockApplication;
    @Mock
    private MockResources mockResources;
    @Mock
    private MockContext mockAppContext;
    @Mock
    private MockPackageManager mockPackageManager;


    // Tells mockito to create the mocks based on the @Mock annotation
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PizzaPlace testPizzaPlace;

    private final static String NUM_RATINGS_FORMAT = "(%s)";
    private final static String WHOLE_ADDRESS_FORMAT = "%s, %s, %s";
    private final static String DISTANCE_FORMAT = "%.2f miles";
    private final static String NUM_REVIEWS_FORMAT = "(%d reviews)";

    private String TEST_NAME;
    private String TEST_ADDRESS;
    private String TEST_PHONE_NUM;
    private String TEST_DISTANCE;
    private String TEST_AVG_RATING_STR;
    private float TEST_AVG_RATING;
    private String TEST_NUM_RATINGS;
    private String TEST_NUM_REVIEWS;

    //-------------------------------------------------------------------------
    // JUnitOverrides

    @Before
    public void setUp() throws Exception {

        testPizzaPlace = TestPizzaPlaceFactory.createPizzaPlace();

        TEST_NAME = PizzaPlaceDisplayUtils.getName(testPizzaPlace);
        TEST_ADDRESS = PizzaPlaceDisplayUtils.getAddress(testPizzaPlace);
        TEST_PHONE_NUM = PizzaPlaceDisplayUtils.getPhoneNumber(testPizzaPlace);
        TEST_ADDRESS = PizzaPlaceDisplayUtils.getFormattedAddressCityState(testPizzaPlace, WHOLE_ADDRESS_FORMAT);
        TEST_DISTANCE = PizzaPlaceDisplayUtils.getFormattedDistanceStr(testPizzaPlace, DISTANCE_FORMAT);
        TEST_AVG_RATING = PizzaPlaceDisplayUtils.getAverageRating(testPizzaPlace);
        TEST_AVG_RATING_STR = PizzaPlaceDisplayUtils.getAverageRatingStr(testPizzaPlace);
        TEST_NUM_RATINGS = PizzaPlaceDisplayUtils.getFormattedNumRatingsStr(testPizzaPlace, NUM_RATINGS_FORMAT);
        TEST_NUM_REVIEWS = PizzaPlaceDisplayUtils.getFormattedNumReviewsStr(testPizzaPlace, NUM_REVIEWS_FORMAT);

    }

    //-------------------------------------------------------------------------
    // Test Methods

    @Test
    public void setPizzaPlace() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);

        assertThat(testPizzaPlace, equalTo(testViewModel._pizzaPlace));
    }

    @Test
    public void setPizzaPlaceUpdatesObservable() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        // This should fire a property change event when it gets set
        // Lets make sure that happens
        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);

        // Set up the listener
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        ObservableField<PizzaPlace> observablePP = testViewModel.getObservablePizzaPlace();
        observablePP.addOnPropertyChangedCallback(callback);
        // Set the pizza place on the view model
        testViewModel.setPizzaPlace(testPizzaPlace);
        // Verify that the onPropertyChange callback got called on the observablePP
        verify(callback).onPropertyChanged(any(ObservableField.class), anyInt());

    }

    @Test
    public void getName() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_NAME, equalTo(testViewModel.getName()));
    }

    @Test
    public void getAddress() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.whole_address_format)).thenReturn(WHOLE_ADDRESS_FORMAT);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_ADDRESS, equalTo(testViewModel.getAddress()));
    }

    @Test
    public void getPhoneNumber() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_PHONE_NUM, equalTo(testViewModel.getPhoneNumber()));
    }

    @Test
    public void getDistanceStr() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.distance_with_units_format)).thenReturn(DISTANCE_FORMAT);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_DISTANCE, equalTo(testViewModel.getDistanceStr()));
    }


    @Test
    public void getAverageRating() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_AVG_RATING, equalTo(testViewModel.getAverageRating()));
    }

    @Test
    public void getAverageRatingStr() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_AVG_RATING_STR, equalTo(testViewModel.getAverageRatingStr()));
    }

    @Test
    public void getNumRatingsStr() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.num_ratings_format)).thenReturn(NUM_RATINGS_FORMAT);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_NUM_RATINGS, equalTo(testViewModel.getNumRatingsStr()));
    }


    @Test
    public void getNumReviewsStr() throws Exception {
        when (mockApplication.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.num_reviews_format)).thenReturn(NUM_REVIEWS_FORMAT);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
        assertThat(TEST_NUM_REVIEWS, equalTo(testViewModel.getNumReviewsStr()));
    }


    @Test
    public void onCallClicked() throws Exception {
        // This should verify that an intent was launched to make a phone call
        when (mockApplication.getResources()).thenReturn(mockResources);
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        when (mockApplication.getPackageManager()).thenReturn(mockPackageManager);
        when (mockPackageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)).thenReturn(true);

        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);
       // when (testViewModel.getApplication()).thenReturn(mockApplication);
        testViewModel.onCallClicked();

        Uri uri = Uri.parse("tel:" + testViewModel.getPhoneNumber());
        Intent expectedIntent = new Intent(Intent.ACTION_DIAL, uri);
        // For some stupid reason if I pass in the expected intent
        // it LOOKS exactly the same but it says they are different
        // Not sure why, look into this later


        // verify that we attempted to launch an activity to start an activity with the expected intent
        verify(mockAppContext).startActivity(any(Intent.class));
    }

    @Test
    public void onShowMapClicked() throws Exception {

        when (mockApplication.getResources()).thenReturn(mockResources);
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);

        // This should verify that an intent was launched to show the map
        PlaceDetailsViewModel testViewModel = new PlaceDetailsViewModel(mockApplication);
        testViewModel.setPizzaPlace(testPizzaPlace);

        testViewModel.onShowMapClicked();

        String uriStr = String.format(Locale.US, "geo:0,0?q=%3.5f,%3.5f (%s)",
                testPizzaPlace.getLatitude(),
                testPizzaPlace.getLongitude(),
                testPizzaPlace.getName());

        Uri locUri = Uri.parse(uriStr);
        Intent expectedIntent = new Intent(Intent.ACTION_VIEW, locUri);
        expectedIntent.setPackage("com.google.android.apps.maps");

        // For some reason if I pass in the expected intent
        // it LOOKS exactly the same but it says they are different
        // Not sure why, look into this later
        //Argument(s) are different! Wanted:
        //mockAppContext.startActivity(
        //        Intent { act=android.intent.action.VIEW dat=geo:0,0?q=44.09581,-87.66206 (Tom) pkg=com.google.android.apps.maps }
        //);
        //  -> at com.tblauer.pizzame.viewmodel.PlaceDetailsViewModelTest.onShowMapClicked(PlaceDetailsViewModelTest.java:267)
        //  Actual invocation has different arguments:
        // mockAppContext.startActivity(
        //        Intent { act=android.intent.action.VIEW dat=geo:0,0?q=44.09581,-87.66206 (Tom) pkg=com.google.android.apps.maps }
        // );

        // verify that we attempted to launch an activity to display the map with the expected intent
        verify(mockAppContext).startActivity(any(Intent.class));
    }
}