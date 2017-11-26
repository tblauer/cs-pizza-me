package com.tblauer.pizzame.view.ui.fragments;

import android.arch.lifecycle.Observer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.tblauer.pizzame.R;
import com.tblauer.pizzame.databinding.PlaceListItemBinding;
import com.tblauer.pizzame.databinding.PlacesFragmentLayoutBinding;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.utils.PermissionUtils;
import com.tblauer.pizzame.view.ui.SpacingItemDecoration;
import com.tblauer.pizzame.viewmodel.LocationSettingsFailedMessage;
import com.tblauer.pizzame.viewmodel.PlaceItemViewModel;
import com.tblauer.pizzame.viewmodel.PlacesViewModel;
import com.tblauer.pizzame.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;


// TODO
// Add a better initial UI for when we don't have a location yet
// Right now it just shows the empty list UI, which isn't pretty at all
// Not a good first view of the app

// TODO
// Getting multiple popups for permissions and location settings on configuration changed
// Do something to fix this  Maybe -  Save something in savedInstanceState indicating whether or not
// the dialog is popped up, reset the value in onRequestPermissionResult or OnActivityResult
// and only pop it up if it's not already there
public class PlacesFragment extends Fragment {

    //---------------------------------------------------------------------------
    // Member variables

    private PlacesViewModel _viewModel = null;
    private SharedViewModel _sharedViewModel = null;
    private PlacesFragmentLayoutBinding _fragmentBinding = null;

    private PlacesListAdapter _placesListAdapter = null;

    private final Observer<List<PizzaPlace>> _pizzaPlacesObserver;
    private final Observer<Boolean> _locationPermissionsRequestedObserver;
    private final Observer<Boolean> _swipedToRefreshProgressObserver;
    private final LocationSettingsFailedMessage.LocationSettingsFailedObserver _locationSettingsFailedObserver;
    private final Observer<Boolean> _userDeniedLocationSettingsObserver;


    private View _snackBarView = null;

    private final String LOG_TAG = getClass().getName();

    //---------------------------------------------------------------------------
    // Constructor

    public PlacesFragment() {
        super();

        // Initialize the final observers
        _pizzaPlacesObserver = new Observer<List<PizzaPlace>>() {
            public void onChanged(List<PizzaPlace> places) {
                // Tell the adapter the pizza places changed
                if (_placesListAdapter != null) {
                    _placesListAdapter.setPlaces(places);
                }
            }
        };

        _locationPermissionsRequestedObserver = new Observer<Boolean>() {
            public void onChanged(Boolean locationPermissionsRequested) {
                if (locationPermissionsRequested) {
                    PermissionUtils.requestPermissionsFromFragment(PlacesFragment.this,
                            _snackBarView,
                            PermissionUtils.WhichPermission.REQUEST_LOCATION,
                            PermissionUtils.PERMISSIONS_LOCATION_REQUEST_CODE);
                }
            }
        };

        _swipedToRefreshProgressObserver = new Observer<Boolean>() {
            public void onChanged(Boolean swipedToRefresh) {
                // If the value changed to false, then stop showing that the view is refreshing
                if (!swipedToRefresh) {
                    if (_fragmentBinding.swipeRefreshLayout.isRefreshing()) {
                        _fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        };

        _locationSettingsFailedObserver = new LocationSettingsFailedMessage.LocationSettingsFailedObserver() {
            public void onHandleResolvableError(ResolvableApiException resolvable, int resultCodeToUse) {
                if (resolvable != null) {
                    doHandleResolvableError(resolvable, resultCodeToUse);
                }
            }
        };

        _userDeniedLocationSettingsObserver = new Observer<Boolean>() {
            public void onChanged(Boolean userDeniedLocationServices) {
                // Pop up an alert dialog telling them that location services needs to be enabled for
                // the app to work
            }
        };
    }

    //---------------------------------------------------------------------------
    // Fragment overrides

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _fragmentBinding = DataBindingUtil.inflate(inflater,
                R.layout.places_fragment_layout, container, false);

        View v = _fragmentBinding.getRoot();
        _snackBarView = v;

        // Set up the list view
        RecyclerView placesRecyclerView = _fragmentBinding.placesRecyclerView;
        placesRecyclerView.setHasFixedSize(true);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        placesRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        placesRecyclerView.addItemDecoration(new SpacingItemDecoration(LinearLayoutManager.VERTICAL, 20, 20));
        _placesListAdapter = new PlacesListAdapter();
        placesRecyclerView.setAdapter(_placesListAdapter);

        setHasOptionsMenu(false);
        displayHomeAsDisabled();

        // Set up the swipe refresher to make a new request for data
        SwipeRefreshLayout swipe = _fragmentBinding.swipeRefreshLayout;
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (_fragmentBinding.getPlacesViewModel() != null) {
                    _viewModel.onSwipeToRefreshCalled();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        // Use the activity instead of the fragment as the ViewModels LifeCycle so
        // we can share it with the PlaceDetailsFragment
        _sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        _fragmentBinding.setPlacesViewModel(_viewModel);
        _fragmentBinding.setSharedViewModel(_sharedViewModel);
        _fragmentBinding.executePendingBindings();

        setUpObservers();
    }


    @Override
    public void onStart() {
        super.onStart();
        _viewModel.onLocationDependentViewStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        _viewModel.onLocationDependentViewStopped();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSIONS_LOCATION_REQUEST_CODE:
                _viewModel.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            default:
                _viewModel.handleActivityResult(requestCode, resultCode, data);
        }
    }


    //-------------------------------------------------------------------------
    // Private methods

    private void displayHomeAsDisabled() {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.app_name);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    private void setUpObservers() {
        // TODO
        // Replace the anonymous observers with named ones
        // Add them and then remove them in onDestroyView
        // Or unsubscribe right before subscribing again.
        // I haven't noticed it yet, but there may be a bug where multiple listeners get added
        // during onActivityCreated, but they don't get removed when fragment is detached and reattached
        //https://github.com/googlesamples/android-architecture-components/issues/47

        _viewModel.getPizzaPlaces().removeObserver(_pizzaPlacesObserver);
        _viewModel.getPizzaPlaces().observe(this, _pizzaPlacesObserver);

        _viewModel.getLocationPermissionsRequested().removeObserver(_locationPermissionsRequestedObserver);
        _viewModel.getLocationPermissionsRequested().observe(this, _locationPermissionsRequestedObserver);

        _viewModel.getSwipedToRefreshProgressVisible().removeObserver(_swipedToRefreshProgressObserver);
        _viewModel.getSwipedToRefreshProgressVisible().observe(this, _swipedToRefreshProgressObserver);

        _viewModel.getLocationSettingsFailedMessge().removeObserver(_locationSettingsFailedObserver);
        _viewModel.getLocationSettingsFailedMessge().observe(this, _locationSettingsFailedObserver);

        _viewModel.getUserDeniedLocationServicesEvent().removeObserver(_userDeniedLocationSettingsObserver);
        _viewModel.getUserDeniedLocationServicesEvent().observe(this, _userDeniedLocationSettingsObserver);
    }

    private void doHandleResolvableError(ResolvableApiException resolvable, int resultCodeToUse) {
        try {
            startIntentSenderForResult(resolvable.getResolution().getIntentSender(), resultCodeToUse, null, 0, 0, 0, null);
        }
        catch (IntentSender.SendIntentException sendEx) {
            Log.e(LOG_TAG, sendEx.getMessage(), sendEx);
        }
    }

    //---------------------------------------------------------------------------
    // Private Helper classes


    // TODO
    // Try to pass in SharedVIewModel and Resources to the constructor
    // pass them to the ViewModelHolder,
    //      Don't *think* I need to create a new SharedViewModel for each view holder
    // If that works, this does not need to be an inner class, and I can make the ViewHolder class static
    private class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PizzaPlaceViewHolder> {
        private List<PizzaPlace> _pizzaPlaces = new ArrayList<>();
        PlacesListAdapter() {
            super();
            setHasStableIds(true);
        }

        @Override
        public PizzaPlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            PlaceListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.place_list_item, viewGroup, false);
            return new PizzaPlaceViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(PizzaPlaceViewHolder holder, int position) {
          //  final PizzaPlace pizzaPlace = _pizzaPlaces.get(position);
            holder.bindPlace(_pizzaPlaces.get(position));
        }

        @Override
        public int getItemCount() {
            return _pizzaPlaces.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public void setPlaces(List<PizzaPlace> pizzaPlaces) {
            // If we got sent null, create an empty list for the adapter
            _pizzaPlaces = pizzaPlaces == null ? new ArrayList<>() : pizzaPlaces;
            notifyDataSetChanged();
        }

        //---------------------------------------------------------------------
        // private ViewHolder class

         class PizzaPlaceViewHolder extends RecyclerView.ViewHolder {
            private PlaceListItemBinding _binding;

            PizzaPlaceViewHolder(PlaceListItemBinding binding) {
                super(binding.placeItemTopView);
                _binding = binding;
                // I cannot for the life of me get the onClick to work through databinding in the xml
                // Do this for now and figure it out later
                // TODO Try this again
                _binding.placeItemTopView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        int position = getLayoutPosition();
                        PizzaPlace pplace = _pizzaPlaces.get(position);
                        binding.getSharedViewModel().onPizzaPlaceClicked(pplace);
                    }
                });
            }

            public void bindPlace(@NonNull PizzaPlace pplace) {
                if (_binding.getPlaceItemViewModel() == null) {
                    PlaceItemViewModel placeItemViewModel = new PlaceItemViewModel(getResources());
                    _binding.setPlaceItemViewModel(placeItemViewModel);
                }
                if (_binding.getSharedViewModel() == null) {
                    SharedViewModel sharedViewModel= ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
                    _binding.setSharedViewModel(sharedViewModel);
                }

                _binding.getPlaceItemViewModel().setPizzaPlace(pplace);
            }
        }
    }
}
