package com.unisofia.fmi.pfly.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.unisofia.fmi.pfly.PFlyApp;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.ui.activity.BaseActivity;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class RequestManager {
	private static char[] KEYSTORE_PASSWORD = "pflybe".toCharArray();

	private static RequestQueue sRequestQueue;

	private RequestManager() {
		// forbid instantiation
	}

	public static RequestQueue getRequestQueue() {
		if (sRequestQueue == null) {
			sRequestQueue = Volley.newRequestQueue(PFlyApp.getAppContext());
//			sRequestQueue = Volley.newRequestQueue(PFlyApp.getAppContext(), new HurlStack(null, newSslSocketFactory()));
		}

		return sRequestQueue;
	}

	private static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


	public static <T> void sendRequest(final Context context, Object tag,
			BaseGsonRequest<T> request, final Listener<T> listener) {

		if (isNetworkAvailable(context)) {

			request.setResponseListener(new Listener<T>() {

				@Override
				public void onResponse(T response) {
					if (response != null) {
						Log.d("Request", "===== Response =====");
						Log.d("Request", "Response: " + response.toString());
						Log.d("Request", "===== End of Response =====");
					}
					hideProgressDialogIfFromUi(context);

					if (listener != null) {
						listener.onResponse(response);
					}
				}
			});

			request.setTag(tag);
			getRequestQueue().add(request);
			showProgressDialogIfFromUi(context);
		} else {
			Toast.makeText(context, "Internet connection not available", Toast.LENGTH_LONG ).show();
		}
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

	private static SSLSocketFactory newSslSocketFactory() {
		try {
			// Get an instance of the Bouncy Castle KeyStore format
			KeyStore trusted = KeyStore.getInstance("BKS");
			// Get the raw resource, which contains the keystore with
			// your trusted certificates (root and any intermediate certs)
			InputStream in = PFlyApp.getAppContext().getResources().openRawResource(R.raw.pfly_be);
			try {
				// Initialize the keystore with the provided trusted certificates
				// Provide the password of the keystore
				trusted.load(in, KEYSTORE_PASSWORD);
			} finally {
				in.close();
			}

			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(trusted);

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			SSLSocketFactory sf = context.getSocketFactory();
			return sf;
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}
