package com.tblauer.pizzame.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.tblauer.pizzame.SingleLiveEvent;
import com.tblauer.pizzame.utils.AppRequestCodes;


public class LocationSettingsFailedMessage extends SingleLiveEvent<ResolvableApiException> {

    private final Observer<ResolvableApiException> _innerObserver = new Observer<ResolvableApiException>() {
        public void onChanged(@Nullable ResolvableApiException resolvable) {
            doHandleOnChange(resolvable);
        }
    };

    private LocationSettingsFailedObserver _locationSettingsFailedObserver;

    public void removeObserver(final LocationSettingsFailedObserver observer) {
        _locationSettingsFailedObserver = null;
        super.removeObserver(_innerObserver);
    }

    public void observe(LifecycleOwner owner, final LocationSettingsFailedObserver observer) {
        _locationSettingsFailedObserver = observer;
        super.observe(owner, _innerObserver);
    }

    private void doHandleOnChange(ResolvableApiException resolvable) {
        if (_locationSettingsFailedObserver != null) {
            _locationSettingsFailedObserver.onHandleResolvableError(resolvable, AppRequestCodes.REQUEST_CHECK_LOCATION_SETTINGS);
        }
    }

    public interface LocationSettingsFailedObserver {
        /**
         Will get called if a locationRequest failed, but is resolvable
         with some user interaction
         Should call the <code>ResolvableApiException.startResolutionForResult</code> method with
         the specified <code>activityResultRequestCode</code>
         */
        void onHandleResolvableError(ResolvableApiException resolvable, int activityResultRequestCode);
    }
}