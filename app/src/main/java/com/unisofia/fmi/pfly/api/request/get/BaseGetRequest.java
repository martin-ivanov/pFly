package com.unisofia.fmi.pfly.api.request.get;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;

public class BaseGetRequest<T> extends BaseGsonRequest<T> {

	private Class<T> mResponse;

	public BaseGetRequest(Context context, String methodName, Map<String, String> params, Class<T> responseClass,
			ErrorListener listener) {
		super(Method.GET, buildUrl(methodName, params), new RequestErrorListener(context, listener));

		this.mResponse = responseClass;

		Log.i("Request", "===== Get Request =====");
		Log.i("Request", "GET URL: " + buildUrl(methodName, params));
		Log.i("Request", "===== End of Get Request =====");
	}

	private static String buildUrl(String methodName, Map<String, String> params) {
		if (params == null) {
			params = new HashMap<String, String>();
		}

		StringBuilder sb = new StringBuilder(ApiConstants.API_BASE_URL);
		sb.append(methodName).append("?");

		for (Map.Entry<String, String> paramCouple : params.entrySet()) {
			sb.append(paramCouple.getKey()).append("=").append(paramCouple.getValue()).append("&");
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	@Override
	protected Class<T> getResponseClass() {
		return mResponse;
	}
}
