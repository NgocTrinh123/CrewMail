package com.dazone.crewemail.webservices;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class WebServiceManager<T> {

    private Map<String, String> mHeaders;

    private Request.Priority mPriority;

    public WebServiceManager() {
    }

    public void doJsonObjectRequest(int requestMethod, final String url, final JSONObject bodyParam, final RequestListener<String> listener) {
        if(Statics.WRITE_HTTP_REQUEST) {
            Util.printLogs("url : " + url);
            Util.printLogs("bodyParam.toString() : "+ bodyParam.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, bodyParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(Statics.WRITE_HTTP_REQUEST) {
                    Util.printLogs("response.toString() : "+ response.toString());
                }
                try {
                    JSONObject json;
                    if(url.contains(Urls.URL_GET_LOGIN)||url.contains(Urls.URL_CHECK_DEVICE_TOKEN)||url.contains(Urls.URL_CHECK_SESSION)) {
                        json = new JSONObject(response.getString("d"));
                    }
                    else
                    {
                        json = new JSONObject(response.getString("d"));
                    }
                    int isSuccess;
                    try {
                        isSuccess = json.getInt("success");
                    }catch (Exception e){
                        isSuccess = json.getBoolean("success")?1:0;
                    }
                    if (isSuccess ==1) {

                        listener.onSuccess(json.getString("data"));
                    } else {
                        ErrorData errorDto = new Gson().fromJson(json.getString("error"), ErrorData.class);
                        if (errorDto == null) {

                            errorDto = new ErrorData();
                            errorDto.setMessage(Util.getString(R.string.no_network_error));
                        } else
                        {
                            if (errorDto.getCode() ==0 && !url.contains(Urls.URL_CHECK_DEVICE_TOKEN) && !url.contains(Urls.URL_CHECK_SESSION)
                                    && !url.contains(Urls.URL_INSERT_PHONE_TOKEN)
                                    && !url.contains(Urls.URL_GET_USER_INFO))
//                                    && !url.contains(CrewUrls.URL_GET_USER_INFO) && !url.contains(Urls.URL_REG_GCMID) )
                            {
                                new Prefs().putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, true);
                                DaZoneApplication.getInstance().getPrefs().clearLogin();
//                                BaseActivity.Instance.startSingleActivity(LoginActivity.class);
                            }
                        }

                        listener.onFailure(errorDto);
                    }

                } catch (JSONException e) {

                    ErrorData errorDto = new ErrorData();
                    errorDto.setMessage(Util.getString(R.string.no_network_error));
                    listener.onFailure(errorDto);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ErrorData errorDto = new ErrorData();
                if (null != error) {

                    listener.onFailure(errorDto);
                }


//                String body;
//                //get status code here
//                String statusCode = String.valueOf(error.networkResponse.statusCode);
//                //get response body and parse with appropriate encoding
//                if(error.networkResponse.data!=null) {
//                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//                        Util.printLogs("Thanh Tung "+body);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }


            }
        });
        DaZoneApplication.getInstance().addToRequestQueue(jsonObjectRequest, url);
    }

    public interface RequestListener<T> {
        void onSuccess(T response);
        void onFailure(ErrorData error);
    }
}
