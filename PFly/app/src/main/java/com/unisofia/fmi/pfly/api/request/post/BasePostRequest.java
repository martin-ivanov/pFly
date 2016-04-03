package com.unisofia.fmi.pfly.api.request.post;

import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;

public abstract class BasePostRequest<T> extends BaseGsonRequest<T> {

	protected abstract Map<String, String> getPostParams();

	private static String url;

	public BasePostRequest(Context context, String methodName, ErrorListener listener) {
		super(Method.POST, buildUrl(methodName), new RequestErrorListener(context, listener));
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> postParams = getPostParams();

		Log.i("Request", "===== Post Request =====");
		Log.i("Request", "Post URL: " + url);
		Log.i("Request", "Params: " + postParams);
		Log.i("Request", "===== End of Post Request =====");

		return postParams;
	}

	private static String buildUrl(String methodName) {
		url = methodName;
		return ApiConstants.API_BASE_URL + methodName;
	}
}
