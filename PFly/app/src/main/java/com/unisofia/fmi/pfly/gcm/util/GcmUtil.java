package com.unisofia.fmi.pfly.notification.gcm.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GcmUtil {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private GcmUtil() {
		// forbid instantiation
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(GcmConstants.DEBUG_TAG, "This device is not supported.");
				activity.finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID in the application's {@code SharedPreferences}
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		Log.i(GcmConstants.DEBUG_TAG, "Saving regId = " + regId);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(GcmConstants.PROPERTY_REG_ID, regId);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(GcmConstants.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(GcmConstants.DEBUG_TAG, "Registration not found.");
			return "";
		}

		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGcmPreferences(Context context) {
		return context.getSharedPreferences(GcmConstants.GCM_PREFS, Context.MODE_PRIVATE);
	}

}
