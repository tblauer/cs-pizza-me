package com.tblauer.pizzame.view.ui.fragments;

import android.arch.lifecycle.Observer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tblauer.pizzame.R;
import com.tblauer.pizzame.databinding.PlaceListItemBinding;
import com.tblauer.pizzame.databinding.PlacesFragmentLayoutBinding;
import com.tblauer.pizzame.model.PizzaPlace;
import com.tblauer.pizzame.utils.PermissionUtils;
import com.tblauer.pizzame.view.ui.SpacingItemDecoration;
import com.tblauer.pizzame.viewmodel.PlaceItemViewModel;
import com.tblauer.pizzame.viewmodel.PlacesViewModel;
import com.tblauer.pizzame.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlacesFragment extends Fragment {

    //---------------------------------------------------------------------------
    // Member variables

    private PlacesViewModel _viewModel = null;
    private SharedViewModel _sharedViewModel = null;
    private PlacesFragmentLayoutBinding _fragmentBinding = null;

    private MyLocationListener _myLocationListener = null;

    private PlacesListAdapter _placesListAdapter = null;

    private View _snackBarView = null;

    //---------------------------------------------------------------------------
    // Constructor

    public PlacesFragment() {
        super();
        // Required empty public constructor
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
        /*
        SwipeRefreshLayout swipe = _fragmentBinding.swipeRefreshLayout;
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (_fragmentBinding.getPlacesViewModel() != null) {
                    _fragmentBinding.getPlacesViewModel().onSwipeToRefreshCalled();
                }
            }
        });
*/
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _myLocationListener = new MyLocationListener(getContext());

        _viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        // Use the activity instead of the fragment as the ViewModels LifeCycle so
        // we can share it with the PlaceDetailsFragment
        _sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        _fragmentBinding.setPlacesViewModel(_viewModel);
        _fragmentBinding.setSharedViewModel(_sharedViewModel);

        setUpObservers();
    }


    @Override
    public void onStart() {
        super.onStart();
        _myLocationListener.connect();
        requestLocation();
    }

    @Override
    public void onStop() {
        _myLocationListener.disconnect();
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSIONS_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0)
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        requestLocation();
                }
                break;
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

    private void requestLocation() {
        if (PermissionUtils.hasPermission(getContext(), PermissionUtils.WhichPermission.REQUEST_LOCATION)) {
            Location location = _myLocationListener.retrieveLastLocation();
            _viewModel.setCurrentLocation(location);
        }
        else {
            PermissionUtils.requestPermissionsFromFragment(this, _snackBarView,
                    PermissionUtils.WhichPermission.REQUEST_LOCATION,
                    PermissionUtils.PERMISSIONS_LOCATION_REQUEST_CODE);
        }
    }

    /*
    private void updatePizzaPlacesByMe() {
        if (PermissionUtils.hasPermission(getContext(), PermissionUtils.WhichPermission.REQUEST_LOCATION)) {
            Location location = _myLocationListener.retrieveLastLocation();
            if (location != null) {
                _viewModel.refreshPizzaPlaces(location);
            }
        }
    }
    */

    private void setUpObservers() {
        // Set up listeners on the view model
        _viewModel.getPizzaPlaces().observe(this, new Observer<List<PizzaPlace>>() {
            public void onChanged(List<PizzaPlace> places) {
                // Tell the adapter the pizza places changed
                _placesListAdapter.setPlaces(places);
            }
        });

        _viewModel.getLocationRequested().observe(this, new Observer<Boolean>() {
            public void onChanged(Boolean locationRequested) {
                if (locationRequested) {
                    requestLocation();
                }
            }
        });
/*
        _viewModel.getSwipedToRefresh().observe(this, new Observer<Boolean>() {
            public void onChanged(Boolean swipedToRefresh) {
                // If the value changed to false, then stop showing that the view is refreshing
                if (!swipedToRefresh) {
                    if (_fragmentBinding.swipeRefreshLayout.isRefreshing()) {
                        _fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
        */
    }


    //---------------------------------------------------------------------------
    // Private Helper classes


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
            _pizzaPlaces = pizzaPlaces;
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
                // Plus I can't make the class static (and avoid a memory leak if I have to set
                // the SharedViewModel on it since the SharedViewModel is using the activities context
                // to keep it alive for both fragments
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

    // Ensuring we have a connection to GooglePlayServices
    // Sigh...
    // Can switch this out now for the FusedLocationProviderClient
    // It was advised not to use it by google wben I wrote this like 4 days ago
    // A new version of google play services fixed it
    //
    private class MyLocationListener implements  GoogleApiClient.ConnectionCallbacks,
                                                GoogleApiClient.OnConnectionFailedListener {

        private final String LOG_TAG = getClass().getName();
        private GoogleApiClient _googleApiClient;

        public MyLocationListener(Context context) {
            _googleApiClient = new GoogleApiClient.Builder(context)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
        }

        //-------------------------------------------------------------------------
        // Class methods

        // This method should not be called unless we have been granted permissions for
        // FINE_LOCATION, or it will throw a SecurityException
        public Location retrieveLastLocation() throws SecurityException {
            // This says it deprecated, but in the Google documentation states:
            // Warning: Please continue using the FusedLocationProviderApi class and
            // don't migrate to the FusedLocationProviderClient class until Google Play services version 12.0.0
            // is available, which is expected to ship in early 2018.
            // Using the FusedLocationProviderClient before version 12.0.0 causes the client app to crash
            // when Google Play services is updated on the device.
            // We apologize for any inconvenience this may have caused.they ask that
            // it continue to be used until GooglePlayServices version 12.0.0 is available

            // Looks like this was actually fixed in the 11.6 release of google play services
            // stated in release notes on November 6
            // Can switch this to use FusedLocationProviderClient
            return LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        }

        //-------------------------------------------------------------------------
        // GoogleApi.ConnectionCallbacks implementation

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            requestLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {
            // hope it comes back
        }

        //-------------------------------------------------------------------------
        // GoogleApi.OnConnectionFailedListener implementation

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // Just log it for now, can do a bunch of stuff if it's resolvable
            // with the connectionResult
            Log.e(LOG_TAG, "Failed to connect to googleAPIClient due to " + connectionResult.getErrorMessage());
        }

        //-------------------------------------------------------------------------
        // Private helper methods

        private void connect() {
            if (!_googleApiClient.isConnected()) {
                _googleApiClient.connect();
            }
        }

        private void disconnect() {
            if (_googleApiClient.isConnected()) {
                _googleApiClient.disconnect();
            }
        }
    }
}
