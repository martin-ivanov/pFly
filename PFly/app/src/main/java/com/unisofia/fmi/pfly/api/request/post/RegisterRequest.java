package com.unisofia.fmi.pfly.api.request.post;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.model.Profile;

public class RegisterRequest extends BasePostRequest<Profile> {

	private static final String METHOD_NAME = "register";

	private static final String PARAM_NAME = "name";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_GCM_ID = "gcmId";

	private String name;
	private String email;
	private String password;
	private String gcmId;

	public RegisterRequest(Context context, String name, String email, String password,
			String gcmId, ErrorListener listener) {
		super(context, METHOD_NAME, listener);

		this.name = name;
		this.email = email;
		this.password = password;
		this.gcmId = gcmId;
	}

	@Override
	protected Map<String, String> getPostParams() {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(PARAM_NAME, name);
		paramsMap.put(PARAM_EMAIL, email);
		paramsMap.put(PARAM_PASSWORD, password);
		paramsMap.put(PARAM_GCM_ID, gcmId);

		return paramsMap;
	}

	@Override
	protected Class<Profile> getResponseClass() {
		return Profile.class;
	}

}
