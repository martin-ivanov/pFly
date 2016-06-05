package com.unisofia.fmi.pfly.api.request.delete;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;

import java.util.Map;

public abstract class BaseDeleteRequest<T> extends BaseGsonRequest<T> {

	protected abstract Map<String, String> getPostParams();

	private static String url;

	public BaseDeleteRequest(Context context, String methodName, ErrorListener listener) {
		super(Method.DELETE, buildUrl(methodName), new RequestErrorListener(context, listener));
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> postParams = getPostParams();

		Log.i("Request", "===== Delete Request =====");
		Log.i("Request", "Delete URL: " + url);
		Log.i("Request", "Params: " + postParams);
		Log.i("Request", "===== End of Delete Request =====");

		return postParams;
	}


	private static String buildUrl(String methodName) {
		url = methodName;
		return ApiConstants.API_BASE_URL + methodName;
	}
}
