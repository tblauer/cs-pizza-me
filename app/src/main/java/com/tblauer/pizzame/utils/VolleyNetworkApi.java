package com.tblauer.pizzame.utils;

import android.support.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VolleyNetworkApi {

    public enum HttpMethod {
        GET(Request.Method.GET),
        POST(Request.Method.POST);

        private int _type;
        HttpMethod(int t) { _type = t; }
        public int value() { return _type; }
    }

    private RequestQueue _requestQueue;

    private ExecutorService _pipeline = Executors.newSingleThreadExecutor();

    private VolleyNetworkApi(@NonNull File cacheDir) {
        Cache cache = new DiskBasedCache(cacheDir, 1024*1024);
        Network network = new BasicNetwork(new HurlStack());
        _requestQueue = new RequestQueue(cache, network);
        _requestQueue.start();
    }

    private static VolleyNetworkApi s_instance = null;

    public synchronized static VolleyNetworkApi getInstance(@NonNull File cacheDir) {
        if(s_instance == null)
            synchronized (VolleyNetworkApi.class) {
                s_instance = new VolleyNetworkApi(cacheDir);
            }
        return s_instance;
    }

    public Request<JSONObject> doJsonObjectRequest(HttpMethod methodType,
                                                   String url,
                                                   JSONObject data,
                                                   ResponseHandler<JSONObject> respHandler)
    {
        JsonObjectRequest request =  new JsonObjectRequest(methodType.value(),
                                                    url,
                                                    data,
                                                    respHandler,
                                                    respHandler);
        return _requestQueue.add(request);
    }

    public Request<JSONArray> doJsonArrayRequest(HttpMethod methodType,
                                          String url,
                                          JSONArray data,
                                          ResponseHandler<JSONArray> responseHandler) {
        JsonArrayRequest request = new JsonArrayRequest(methodType.value(),
                                                        url,
                                                        data,
                                                        responseHandler,
                                                        responseHandler);
        return _requestQueue.add(request);
    }

}
