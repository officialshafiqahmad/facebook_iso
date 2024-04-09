package com.example.facebook_iso.api_manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.facebook_iso.common.CheckNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class api_service {
    private static final String TAG = api_service.class.getSimpleName();

    private final Context mContext;
    private final RequestQueue mRequestQueue;

    public api_service(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
    }

    public void get(String url, String bearerToken, ApiCallback callback) {
        makeRequest(Request.Method.GET, url, null, bearerToken, callback);
    }

    public void post(String url, JSONObject requestBody, String bearerToken, ApiCallback callback) {
        makeRequest(Request.Method.POST, url, requestBody, bearerToken, callback);
    }

    public void patch(String url, JSONObject requestBody, String bearerToken, ApiCallback callback) {
        makeRequest(Request.Method.PATCH, url, requestBody, bearerToken, callback);
    }

    public void delete(String url, String bearerToken, ApiCallback callback) {
        makeRequest(Request.Method.DELETE, url, null, bearerToken, callback);
    }


    private void makeRequest(int method, final String url, final JSONObject requestBody, final String bearerToken, final ApiCallback callback) {
        if (!CheckNetwork.isNetworkAvailable(mContext)) {
            callback.onError("No internet connection");
            return;
        }

        StringRequest stringRequest = new StringRequest(method, constants.baseUrl + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Object jsonResponse = new JSONTokener(response).nextValue();
                            if (jsonResponse instanceof JSONObject) {
                                callback.onSuccess((JSONObject) jsonResponse);
                            } else if (jsonResponse instanceof JSONArray) {
                                callback.onSuccess((JSONArray) jsonResponse);
                            } else {
                                callback.onError("Unknown response type");
                            }
                        } catch (JSONException e) {
                            callback.onError("Error parsing response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(error, callback);
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                if (requestBody != null) {
                    return requestBody.toString().getBytes();
                }
                return super.getBody();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + bearerToken);
                }
                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }



    private void handleErrorResponse(VolleyError error, ApiCallback callback) {
        if (error instanceof NoConnectionError) {
            callback.onError("No internet connection");
        } else if (error instanceof TimeoutError) {
            callback.onError("Request timeout");
        } else if (error instanceof NetworkError) {
            callback.onError("Network error");
        } else if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String message = "Unknown error";
            if (error.networkResponse.data != null) {
                try {
                    message = new String(error.networkResponse.data);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing error response", e);
                }
            }
            callback.onError("Status code: " + statusCode + ", Message: " + message);
        } else {
            callback.onError("Unknown error");
        }
    }

    public interface ApiCallback {
        void onSuccess(JSONObject response);

        void onSuccess(JSONArray response);

        void onError(String errorMessage);
    }
}

