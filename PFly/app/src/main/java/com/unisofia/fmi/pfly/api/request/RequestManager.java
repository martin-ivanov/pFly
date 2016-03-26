package com.unisofia.fmi.pfly.api.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.unisofia.fmi.pfly.PFlyApp;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.ui.activity.BaseActivity;

public class RequestManager {

	private static RequestQueue sRequestQueue;

	private RequestManager() {
		// forbid instantiation
	}

	public static RequestQueue getRequestQueue() {
		if (sRequestQueue == null) {
			sRequestQueue = Volley.newRequestQueue(PFlyApp.getAppContext());
		}

		return sRequestQueue;
	}

	public static <T> void sendRequest(final Context context, Object tag,
			BaseGsonRequest<T> request, final Listener<T> listener) {

		request.setResponseListener(new Listener<T>() {

			@Override
			public void onResponse(T response) {
				Log.d("Request", "===== Response =====");
				Log.d("Request", "Reponse: " + response.toString());
				Log.d("Request", "===== End of Response =====");

				hideProgressDialogIfFromUi(context);

				if (listener != null) {
					listener.onResponse(response);
				}
			}
		});

		request.setTag(tag);
		getRequestQueue().add(request);
		showProgressDialogIfFromUi(context);
	}

	private static void showProgressDialogIfFromUi(Context context) {
		if (context instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) context;
			activity.showProgress();
		}
	}

	private static void hideProgressDialogIfFromUi(Context context) {
		if (context instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) context;
			if (!activity.isFinishing()) {
				activity.hideProgress();
			}
		}
	}
}
