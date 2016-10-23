package com.unisofia.fmi.pfly.api.request;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unisofia.fmi.pfly.usermanagement.UserManager;
import com.unisofia.fmi.pfly.api.util.JsonDateDeserializer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseGsonRequest<T> extends Request<T> {

    private static final String TOKEN_HEADER = "X-Auth-Token";

    private Gson mGson;
    private Listener<T> mListener;

    protected abstract Class<T> getResponseClass();

    public BaseGsonRequest(int method, String url, ErrorListener listener) {
        super(method, url, listener);
        mGson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
        setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public BaseGsonRequest(int method, String url, ErrorListener listener, Gson gson) {
        super(method, url, listener);
        this.mGson = gson;
        setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void setResponseListener(Listener<T> listener) {
        this.mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
//		Type listType = new TypeToken<List<getResponseClass()>>(){}.getType();
        return Response.success(mGson.fromJson(getJsonResponse(response.data), getResponseClass()),
                HttpHeaderParser.parseCacheHeaders(response));
    }

    private Reader getJsonResponse(byte[] data) {
        return new InputStreamReader(new ByteArrayInputStream(data));
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (UserManager.isUserLoggedIn()) {
            String token = UserManager.getLoggedUser().getToken();
            if (token != null && !token.equals("")) {
                Map<String, String> authHeaders = new HashMap<>();
                authHeaders.putAll(headers);
                authHeaders.put(TOKEN_HEADER, token);
                headers = authHeaders;
            }
        }
        return headers;
    }
}
