package com.tblauer.pizzame.utils;

import android.content.Context;
import android.content.Intent;

import com.tblauer.pizzame.view.ui.activities.MainActivity;

public class AppIntents {

    public static final String SHOW_PLACE_DETAILS_ACTION = "com.tblauer.pizzame.SHOW_PLACE_DETAILS";

    // All methods in here are static, no need to construct this object
    private AppIntents() {}

    public static Intent getShowDetailsIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(SHOW_PLACE_DETAILS_ACTION);
        return intent;
    }
}
