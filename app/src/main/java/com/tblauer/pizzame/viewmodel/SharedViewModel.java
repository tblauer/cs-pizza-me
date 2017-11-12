package com.tblauer.pizzame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.view.View;

import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.utils.AppIntents;


public class SharedViewModel extends AndroidViewModel {

    private final MutableLiveData<PizzaPlace> _selected = new MutableLiveData<PizzaPlace>();

    private String LOG_TAG = getClass().getName();

    public SharedViewModel(Application application) {
        super(application);
    }
    public void itemSelected(PizzaPlace item) {
        _selected.setValue(item);
    }

    public void onItemClicked(View view) {
        Log.d(LOG_TAG, "onItemClicked called");
    }

    public LiveData<PizzaPlace> getSelected() {
        return _selected;
    }

    public void onPizzaPlaceClicked(View view, PizzaPlace pizzaPlace) {
        Log.d(LOG_TAG, "Pizza place clicked");
        itemSelected(pizzaPlace);
        getApplication().getApplicationContext().startActivity(AppIntents.getShowDetailsIntent(view.getContext()));
    }
}
