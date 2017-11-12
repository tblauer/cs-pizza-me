package com.tblauer.pizzame.utils;

import com.android.volley.Response;


public interface ResponseHandler<T> extends Response.Listener<T>,
                                        Response.ErrorListener {

}
