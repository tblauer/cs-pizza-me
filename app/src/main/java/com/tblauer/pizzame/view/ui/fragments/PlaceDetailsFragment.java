package com.tblauer.pizzame.view.ui.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tblauer.pizzame.R;
import com.tblauer.pizzame.databinding.PlaceDetailsLayoutBinding;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.viewmodel.PlaceDetailsViewModel;
import com.tblauer.pizzame.viewmodel.SharedViewModel;

public class PlaceDetailsFragment extends Fragment {

    //---------------------------------------------------------------------------
    // Member variables

    private PlaceDetailsViewModel _viewModel = null;
    private SharedViewModel _sharedViewModel = null;

    private PlaceDetailsLayoutBinding _binding = null;

    //---------------------------------------------------------------------------
    // Constructor

    public PlaceDetailsFragment() {
        super();
    }


    //---------------------------------------------------------------------------
    // Fragment overrides

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _binding = DataBindingUtil.inflate(inflater,
                R.layout.place_details_layout, container, false);

        View v = _binding.getRoot();

      //  _snackBarView = v;

        setHasOptionsMenu(false);
        displayHomeAsEnabled();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the activity instead of the fragment as the ViewModels LifeCycle so
        // we can share it with the PlacesFragment
        _sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        // This one is just for our fragment, no need to use the activity
        _viewModel = ViewModelProviders.of(this).get(PlaceDetailsViewModel.class);
        _viewModel.setPizzaPlace(_sharedViewModel.getSelected().getValue());
        _binding.setPlaceDetailsViewModel(_viewModel);
        _binding.executePendingBindings();

     //   setUpObservers();
    }

    @Override
    public void onStart() {
        super.onStart();
        setActionBarTitle(_viewModel.getName());
    }

    //---------------------------------------------------------------------------
    // Private class methods

    private void displayHomeAsEnabled() {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    private void setActionBarTitle(String title) {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    private void setUpObservers() {
        _sharedViewModel.getSelected().observe(this, new Observer<PizzaPlace>() {
            public void onChanged(PizzaPlace pplace) {
                // Tell the adapter the pizza places changed
                _viewModel.setPizzaPlace(pplace);
                _binding.notifyChange();
                _binding.executePendingBindings();
            }
        });
    }
}
