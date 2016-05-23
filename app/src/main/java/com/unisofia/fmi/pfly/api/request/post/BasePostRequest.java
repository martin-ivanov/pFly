package com.unisofia.fmi.pfly.api.request.post;

import java.util.HashMap;
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
	private String jsonBody;

	public BasePostRequest(Context context, String methodName, String body, ErrorListener listener) {
		super(Method.POST, buildUrl(methodName), new RequestErrorListener(context, listener));
		this.jsonBody = body;
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

//	@Override
//	public Map<String, String> getHeaders() throws AuthFailureError {
//		Map<String,String> params = new HashMap<String, String>();
//		params.put("Content-Type","application/json");
//		params.put("Accept","application/json");
//		return params;
//	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		Log.d("POST TASK", jsonBody);
		return jsonBody.getBytes();
	}
//
	@Override
	public String getBodyContentType() {
		Log.d("body content type", "body content");
		return "application/json";
	}

	private static String buildUrl(String methodName) {
		url = methodName;
		return ApiConstants.API_BASE_URL + methodName;
	}
}
