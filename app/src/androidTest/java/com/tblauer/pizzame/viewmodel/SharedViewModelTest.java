package com.tblauer.pizzame.viewmodel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.model.TestPizzaPlaceFactory;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test class for SharedViewModel
 */
@RunWith(AndroidJUnit4.class)
public class SharedViewModelTest {

    //-------------------------------------------------------------------------
    // Member variables

    @Mock
    private MockApplication mockApplication;
    @Mock
    private MockContext mockContext;
    @Mock
    private Observer<PizzaPlace> mockObserver;

    // Tells mockito to create the mocks based on the @Mock annotation
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //A JUnit Test Rule that swaps the background executor used by the Architecture Components with
    // a different one which executes each task synchronously.
    //You can use this rule for your host side tests that use Architecture Components.
    @Rule
    public TestRule testRule = new InstantTaskExecutorRule();

    private PizzaPlace testPizzaPlace;

    //-------------------------------------------------------------------------
    // JUnit Overrides

    @Before
    public void setUp() throws Exception {
        testPizzaPlace = TestPizzaPlaceFactory.createPizzaPlace();
    }

    //-------------------------------------------------------------------------
    // Test methods

    @Test
    public void itemSelectedCausesEvent() throws Exception {
        // This should make sure that an onChanged event gets fired when it's called
        SharedViewModel testViewModel = new SharedViewModel(mockApplication);

        // Have to have an observer or no events will get fired
        // so add the mock observer
        testViewModel.getSelected().observeForever(mockObserver);

        testViewModel.itemSelected(testPizzaPlace);
        verify(mockObserver).onChanged(testPizzaPlace);
    }

    @Test
    public void getSelectedValueSame() throws Exception {
        SharedViewModel testViewModel = new SharedViewModel(mockApplication);

        // Have to have an observer or no events will get fired
        // so add the mock observer
        testViewModel.getSelected().observeForever(mockObserver);
        testViewModel.itemSelected(testPizzaPlace);
        LiveData<PizzaPlace> pp = testViewModel.getSelected();

        // Make sure the actual value is what we just set
        assertThat(pp.getValue(), equalTo(testPizzaPlace));
    }


    @Test
    public void onPizzaPlaceClickedItemSelected() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockContext);

        SharedViewModel testViewModel = new SharedViewModel(mockApplication);

        // Have to have an observer or no events will get fired
        // so add the mock observer
        testViewModel.getSelected().observeForever(mockObserver);

        testViewModel.onPizzaPlaceClicked(testPizzaPlace);
        verify(mockObserver).onChanged(testPizzaPlace);
    }

    @Test
    public void onPizzaPlaceClickedActivityStarted() throws Exception {
        when (mockApplication.getApplicationContext()).thenReturn(mockContext);

        SharedViewModel testViewModel = new SharedViewModel(mockApplication);

        // Test whether or not startActivity got called when the pizza place was clicked
        testViewModel.onPizzaPlaceClicked(testPizzaPlace);
        verify(mockContext).startActivity(any(Intent.class));
    }
}