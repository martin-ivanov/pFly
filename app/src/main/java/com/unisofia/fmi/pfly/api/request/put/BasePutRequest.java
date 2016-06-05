package com.unisofia.fmi.pfly.api.request.put;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;

import java.util.HashMap;
import java.util.Map;

public class BasePutRequest<T> extends BaseGsonRequest<T> {

	private Class<T> mResponse;

	public BasePutRequest(Context context, String methodName, Map<String, String> params, Class<T> responseClass,
						  ErrorListener listener) {
		super(Method.PUT, buildUrl(methodName, params), new RequestErrorListener(context, listener));

		this.mResponse = responseClass;

		Log.i("Request", "===== PUT Request =====");
		Log.i("Request", "PUT URL: " + buildUrl(methodName, params));
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
