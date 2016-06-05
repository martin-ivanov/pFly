package com.unisofia.fmi.pfly.api.request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.unisofia.fmi.pfly.ui.activity.BaseActivity;

import java.lang.ref.WeakReference;

public class RequestErrorListener implements ErrorListener {

    private WeakReference<BaseActivity> mActivity;
    private ErrorListener mErrorListener;

    private Gson mGson;

    public RequestErrorListener(Context context, ErrorListener listener) {
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            mActivity = new WeakReference<BaseActivity>(activity);
        }

        // Used to handle the case, when the user provides a
        // ErrorListener
        // to the constructor. This prevents the infinite loop of calling
        // onErrorResponse to itself
        if (listener instanceof ErrorListener) {
            mErrorListener = null;
        } else {
            mErrorListener = listener;
        }

        mGson = new Gson();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        BaseActivity safeActivity = getSafeActivity();
        if (safeActivity != null) {
            safeActivity.hideProgress();
        }

        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        } else {
            handleError(error);
        }

        Log.e("PFLY", error.toString());
        if (mActivity != null && mActivity.get() != null) {
            Toast.makeText(mActivity.get(), "Response error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
//		TODO
//		try {
//			byte[] data = error.networkResponse.data;
//			RequestError fromJson = mGson.fromJson(new InputStreamReader(new ByteArrayInputStream(
//					data)), RequestError.class);
//			DialogUtil.showNeutralAlertDialog(getSafeActivity(), fromJson.getMessage(), null);
//		} catch (Exception e) {
//			Log.e("Error", "" + e.getMessage());
//			DialogUtil.showNeutralAlertDialog(getSafeActivity(), "No internet!", null);
//		}

    }

    /**
     * Check if the activity is recycled or finishing.
     *
     * @return {@link BaseActivity} if it is active, <code>null</code> otherwise
     */
    private BaseActivity getSafeActivity() {
        if (mActivity != null) {
            BaseActivity activity = mActivity.get();
            if (activity != null && !activity.isFinishing()) {
                return activity;
            }
        }

        return null;
    }

}
