package com.tblauer.pizzame.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.tblauer.pizzame.R;

import java.util.concurrent.atomic.AtomicBoolean;


public class PermissionUtils {

    // -------------------------------------------------------------------------
    // Variables

    // Request Codes

    public final static int PERMISSIONS_LOCATION_REQUEST_CODE = 100;

    public enum WhichPermission {
        REQUEST_LOCATION(PERMISSIONS_LOCATION_REQUEST_CODE);

        private int _requestCode = -1;
        static WhichPermission[] vals = values();

        WhichPermission(int permissionRequestCode) {
            _requestCode = permissionRequestCode;
        }

        public int getRequestCode() {
            return _requestCode;
        }

        public static WhichPermission fromOrdinal(int ordinal) {
            if (ordinal > 0 && ordinal < vals.length) {
                return vals[ordinal];
            }
            return null;
        }
    }


    private static final int PERMS_REQUEST_NOTIFICATION_ID = 3;

    private static final String LOG_TAG = "PermissionUtils";

    private AtomicBoolean _locationPermissionsRequested = new AtomicBoolean(false);

    private static PermissionUtils s_instance = new PermissionUtils();

    //---------------------------------------------------------------------------
    // Static factory method

    public static PermissionUtils getInstance() {
        return s_instance;
    }

    //---------------------------------------------------------------------------
    // Private constructor - Object is a singleton, should not have a public constructor
    private PermissionUtils() {
    }

    // --------------------------------------------------------------------------
    // Singleton class methods

    // Flag that checks if the app has requested the specific permission, say, from the
    // background. This allows us to check to make sure we don't ask multiple times from
    // different places

    public boolean permissionsAlreadyRequested(WhichPermission permission) {
        switch (permission) {
            case REQUEST_LOCATION:
                return _locationPermissionsRequested.get();
        }
        return false;
    }

    public void setPermissionsAlreadyRequested(WhichPermission permission, boolean requested) {
        switch (permission) {
            case REQUEST_LOCATION:
                _locationPermissionsRequested.set(requested);
                break;
        }
    }

    // -------------------------------------------------------------------------
    // Public Static Methods


    /**
     * This method should only be called from a NON UI component when permissions are required and not
     * currently granted
     *
     * @param ctx
     * @param whichPermission
     */
    public static void broadcastPermissionRequest(Context ctx, PermissionUtils.WhichPermission whichPermission) {
     //   Intent intent = new Intent(PizzaMeIntents.REQUEST_PERMISSION_ACTION);
      //  intent.putExtra(PizzaMeIntents.REQUEST_PERMISSION_WHICH_ONE_EXTRA, whichPermission.ordinal());
      //  ctx.sendOrderedBroadcast(intent, null);
    }




    public static boolean hasPermission(@NonNull Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return true;
    }

    public static boolean hasPermission(@NonNull Context context, WhichPermission permissionInQuestion) {

        String [] permissions = getActualPermissionsForEnum(permissionInQuestion);
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Utility method to request a needed permission FROM an activity.
     *
     * @param activity Activity object.
     * @param view Top level view used as a parameter for Snackbar messages. If null, no messages will be displayed.
     * @param permissionsToRequest The enum value for which to request permissions
     * @param returnRequestCode The integer requestCode value to be sent back to the activity after permissions have been requested
     */
    public static void requestPermissionsFromActivity(final FragmentActivity activity,
                                                      View view,
                                                      WhichPermission permissionsToRequest,
                                                      final int returnRequestCode) {
        int rationaleResId = -1;
        final String [] permissions = getActualPermissionsForEnum(permissionsToRequest);

        // set reason for request
        switch(permissionsToRequest) {
            case REQUEST_LOCATION:
                rationaleResId = R.string.permission_location_rationale;
        }


        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                // if the requested permission is part of a bundle of permissions requested, ensure that we put out
                // no messages about it if it has already been granted.
                continue;
            }
            else if (view != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Provide a rationale to the user if the permission was not granted and the user would benefit
                // from additional context for the use of the permission.
                int snackbarLength = Snackbar.LENGTH_LONG;
                if (permissionsToRequest == WhichPermission.REQUEST_LOCATION) {
                    snackbarLength = Snackbar.LENGTH_INDEFINITE;
                }
                Snackbar.make(view, rationaleResId, snackbarLength)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(activity, permissions, returnRequestCode);
                            }
                        })
                        .show();
            }
            else {
                // Request permission(s) directly
                ActivityCompat.requestPermissions(activity, permissions, returnRequestCode);
            }
        }
    }



    /**
     * Utility method to request a needed permission FROM a Fragment.
     *
     * @param fragment Fragment from which the permissions are being requested object.
     * @param view Top level view used as a parameter for Snackbar messages. If null, no messages will be displayed.
     * @param permissionsToRequest The enum value for which to request permissions
     * @param returnRequestCode The integer requestCode value to be sent back to the fragment after permissions have been requested
     */


    public static void requestPermissionsFromFragment(final Fragment fragment, View view, WhichPermission permissionsToRequest, final int returnRequestCode) {
        Context fragmentContext = fragment.getContext();
        int rationaleResId = -1;
        final String [] permissions = getActualPermissionsForEnum(permissionsToRequest);

        // set reason for request
        switch(permissionsToRequest) {
            case REQUEST_LOCATION:
                rationaleResId = R.string.permission_location_rationale;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(fragmentContext, permission) == PackageManager.PERMISSION_GRANTED) {
                // if the requested permission is part of a bundle of permissions requested, ensure that we put out
                // no messages about it if it has already been granted.
                continue;
            }
            else if (view != null && fragment.shouldShowRequestPermissionRationale(permission)) {
                // Provide a rationale to the user if the permission was not granted and the user would benefit
                // from additional context for the use of the permission.
                int snackbarLength = Snackbar.LENGTH_LONG;
                if (permissionsToRequest == WhichPermission.REQUEST_LOCATION) {
                    snackbarLength = Snackbar.LENGTH_INDEFINITE;
                }
                Snackbar.make(view, rationaleResId, snackbarLength)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fragment.requestPermissions(permissions, returnRequestCode);
                            }
                        })
                        .show();
            }
            else {
                // Request permission(s) directly
                fragment.requestPermissions(permissions, returnRequestCode);
            }
        }
    }


    private static String [] getActualPermissionsForEnum(WhichPermission permission) {
        switch(permission) {
            case REQUEST_LOCATION:
                return new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
            default:
                throw new IllegalArgumentException("Invalid or unhandled request code");
        }
    }


    /**
     * This method will verify permissions for results to multiple permission requests
     * @param grantResults Results of the permission requests
     * @return <b>true</b> if all permissions have been granted, <b>false</b> otherwise
     */
    public static boolean verifyPermissions(int [] grantResults) {
        // at least one result must be checked
        if (grantResults.length < 1) {
            return false;
        }

        // verify that each required permission has been granted, otherwise return false
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
