package com.unisofia.fmi.pfly.api.request;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public abstract class BaseGsonRequest<T> extends Request<T> {

	private Gson mGson;
	private Listener<T> mListener;

	protected abstract Class<T> getResponseClass();

	public BaseGsonRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		mGson = new Gson();
	}

	public BaseGsonRequest(int method, String url, ErrorListener listener, Gson gson) {
		super(method, url, listener);
		this.mGson = gson;
	}

	public void setResponseListener(Listener<T> listener) {
		this.mListener = listener;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
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
}
