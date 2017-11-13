package com.tblauer.pizzame.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;
import android.test.mock.MockResources;
import android.view.View;

import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.model.TestPizzaPlaceFactory;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class PlacesViewModelTest {

    @Mock
    private MockApplication mockApplication;
    @Mock
    private MockContext mockAppContext;
    @Mock
    private Observer<Boolean> mockBooleanObserver;
    @Mock
    private Observer<Location> mockLocationObserver;
    @Mock
    private Observer<PizzaPlace> mockPizzaPlaceObserver;

    //A JUnit Test Rule that swaps the background executor used by the Architecture Components with
    // a different one which executes each task synchronously.
    //You can use this rule for your host side tests that use Architecture Components.
    @Rule
    public TestRule testRule = new InstantTaskExecutorRule();

    // Tells mockito to create the mocks based on the @Mock annotation
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private List<PizzaPlace> testPizzaPlaces;

    private Location testLocation = null;

    private PlacesViewModel mockViewModel;

    //-------------------------------------------------------------------------
    // JUnit Overrides

    @Before
    public void setUp() throws Exception {
        testPizzaPlaces = TestPizzaPlaceFactory.createPizzsPlaceList();
        testLocation = new Location("TestLocation");
        testLocation.setLatitude(30.912058);
        testLocation.setLongitude( -98.026106);
    }

    //-------------------------------------------------------------------------
    // Test methods


    @Test
    public void getPizzaPlaces() throws Exception {
    }

    @Test
    public void getLocationRequestedObservableCausesEvent() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        // This should make sure that an onChanged event gets fired when it's called
        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        // Have to have an observer or no events will get fired
        // so add the mock observer
        LiveData<Boolean> myObj = testViewModel.getLocationRequested();
        myObj.observeForever(mockBooleanObserver);

        testViewModel._locationRequested.setValue(true);
        verify(mockBooleanObserver).onChanged(Boolean.TRUE);
    }

    @Test
    public void setProgressViewVisibleCausedEvent() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);

        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        // Set up the listener
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        ObservableInt observableVal = testViewModel.getProgressViewVisible();
        observableVal.addOnPropertyChangedCallback(callback);

        // Set the value on the object
        testViewModel._progressViewVisible.set(View.VISIBLE);
        // Verify that the onPropertyChange callback got called on the observablePP
        verify(callback).onPropertyChanged(any(ObservableInt.class), anyInt());
    }


    @Test
    public void setProgressViewVisibleValueChanged() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);

        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        ObservableInt observableVal = testViewModel.getProgressViewVisible();
        // Set the value on the object
        testViewModel._progressViewVisible.set(View.GONE);
        assertThat(testViewModel.getProgressViewVisible().get(), equalTo(View.GONE));
        // Make sure it gets changed
        testViewModel._progressViewVisible.set(View.VISIBLE);
        assertThat(testViewModel.getProgressViewVisible().get(), equalTo(View.VISIBLE));
    }


    @Test
    public void setRecyclerViewVisibleCausedEvent() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        // Set up the listener
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        ObservableInt observableVal = testViewModel.getRecyclerViewVisible();
        observableVal.addOnPropertyChangedCallback(callback);

        // Set the value on the object
        testViewModel._recyclerViewVisible.set(View.INVISIBLE);
        // Verify that the onPropertyChange callback got called on the observablePP
        verify(callback).onPropertyChanged(any(ObservableInt.class), anyInt());
    }

    @Test
    public void setRecyclerViewVisibleValueChanged() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        ObservableInt observableVal = testViewModel.getRecyclerViewVisible();
        // Set the value on the object
        testViewModel._recyclerViewVisible.set(View.GONE);
        assertThat(testViewModel.getRecyclerViewVisible().get(), equalTo(View.GONE));
        // Make sure it gets changed
        testViewModel._recyclerViewVisible.set(View.VISIBLE);
        assertThat(testViewModel.getRecyclerViewVisible().get(), equalTo(View.VISIBLE));
    }

    @Test
    public void setEmptyLayoutVisibleCausedEvent() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        // Set up the listener
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        ObservableInt observableVal = testViewModel.getEmptyLayoutVisible();
        observableVal.addOnPropertyChangedCallback(callback);

        // Set the value on the object
        testViewModel._emptyViewVisible.set(View.INVISIBLE);
        // Verify that the onPropertyChange callback got called on the observablePP
        verify(callback).onPropertyChanged(any(ObservableInt.class), anyInt());
    }

    @Test
    public void setEmptyViewVisibleValueChanged() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);

        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);

        ObservableInt observableVal = testViewModel.getEmptyLayoutVisible();
        // Set the value on the object
        testViewModel._emptyViewVisible.set(View.GONE);
        assertThat(testViewModel.getEmptyLayoutVisible().get(), equalTo(View.GONE));
        // Make sure it gets changed
        testViewModel._emptyViewVisible.set(View.VISIBLE);
        assertThat(testViewModel.getEmptyLayoutVisible().get(), equalTo(View.VISIBLE));
    }

    @Test
    public void setCurrentLocation() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);
        PlacesViewModel testViewModel = new PlacesViewModel(mockApplication);
        // Set this up initially so it wont call refreshPizzaPlaces
        testViewModel._locationRequested.setValue(false);
        testViewModel._pizzaPlaces.setValue(testPizzaPlaces);

        // Have to have an observer or no events will get fired
        // so add the mock observer
        LiveData<Location> myObj = testViewModel._currentLocation;
        myObj.observeForever(mockLocationObserver);

        testViewModel._currentLocation.setValue(testLocation);
        verify(mockLocationObserver).onChanged(testLocation);
    }

    @Test
    public void setCurrentLocationCausesRefreshPizzaPlaces() throws Exception {
        // use the doAnswer mock below
    }

    @Test
    public void onFABRefreshCalled() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockAppContext);

        // TODO Finish this test
/*
        mockViewModel = Mockito.mock(PlacesViewModel.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                //Mock mock = invocation.getMock();
                return null;
            }
        }).when(mockViewModel).refreshPizzaPlaces(testLocation);

        doNothing().when(mockViewModel).refreshPizzaPlaces(testLocation);
*/
        //when (testViewModel.refreshPizzaPlaces()).then


        // This will end up calling the WebService
        // Need to figure out what to do to mock this

        // Test that the following happens
        // LocationRequested gets set to true
        // After some time, _currentLocation should change
        // Pizza places will get updated (after we call the web service)
        // LocationRequested get set to false

        // The visibility observables will get updated before / after the web request
    }
}