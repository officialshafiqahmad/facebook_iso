package com.example.facebook_iso.common;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.facebook_iso.api_manager.constants;
import com.example.facebook_iso.common.UIToast;

import okhttp3.Response;
import java.io.IOException;

public class ResponseHandler {
    public static String handleResponse(Context context, Response response, String endPoint) {
        String responseBody = null;

        if (response == null) {
            Log.d("Debug123: URL: " + constants.baseUrl + endPoint + "\nstatus: ", "Response is null");
            UIToast.showToast(context, "Response is null");
            return null;
        }

        int responseCode = response.code();
        Log.d("Debug123: URL: " + constants.baseUrl + endPoint + "\nstatus: ", String.valueOf(responseCode));

        try {
            if (response.body() != null) {
                responseBody = response.body().string();
                Log.d("Debug123: Body: ", responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("message")) {
                    UIToast.showToast(context, jsonObject.getString("message"));
                } else if (jsonObject.has("error")){
                    UIToast.showToast(context, jsonObject.getString("error"));
                }
                else
                {
                    switch (responseCode) {
                        case 200:
                            UIToast.showToast(context, "Success");
                            break;
                        case 401:
                            UIToast.showToast(context, "");
                        case 404:
                            UIToast.showToast(context, "Not Found");
                            break;
                        case 500:
                            UIToast.showToast(context, "Internal Server Error");
                            break;
                        default:
                            UIToast.showToast(context, "Response code " + responseCode + ": Unhandled response code");
                            break;
                    }
                }
            }

        } catch (IOException e) {
            Log.d("Debug123: URL: " + constants.baseUrl + endPoint, "Error processing response: " + e.getMessage());
            UIToast.showToast(context, "Error processing response");
        } catch (JSONException e) {
            Log.d("Debug123: URL: " + constants.baseUrl + endPoint, "Error parsing JSON: " + e.getMessage());
            UIToast.showToast(context, "Error parsing JSON");
        }

        return responseBody;
    }
}
