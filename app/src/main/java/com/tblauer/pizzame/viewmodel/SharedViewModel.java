package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.util.Log;

import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.utils.AppIntents;
import com.tblauer.pizzame.view.ui.activities.MainActivity;


/**
 * This view model is shared between the PlacesFragment and the PlaceDetailsFragment
 * It is used to share the selected item information
 */
public class SharedViewModel extends AndroidViewModel {

    //-------------------------------------------------------------------------
    // Member variables

    private final MutableLiveData<PizzaPlace> _selected = new MutableLiveData<>();

    private String LOG_TAG = getClass().getName();

    //-------------------------------------------------------------------------
    // Constructor

    public SharedViewModel(Application application) {
        super(application);
    }

    //-------------------------------------------------------------------------
    // Class Methods

    public void itemSelected(PizzaPlace item) {
        _selected.setValue(item);
    }

    public LiveData<PizzaPlace> getSelected() {
        return _selected;
    }

    public void onPizzaPlaceClicked(PizzaPlace pizzaPlace) {
        itemSelected(pizzaPlace);

        Intent intent = new Intent(getApplication().getApplicationContext(), MainActivity.class);
        intent.setAction(AppIntents.SHOW_PLACE_DETAILS_ACTION);

        getApplication().getApplicationContext().startActivity(intent);
    }
}
