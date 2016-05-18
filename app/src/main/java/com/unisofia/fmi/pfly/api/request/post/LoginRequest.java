package com.unisofia.fmi.pfly.api.request.post;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.unisofia.fmi.pfly.api.model.Account;

public class LoginRequest extends BasePostRequest<Account> {

	private static final String METHOD_NAME = "login";

	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_PASSWORD = "password";

	private String username;
	private String password;

	public LoginRequest(Context context, String username, String password, ErrorListener listener) {
		super(context, METHOD_NAME, listener);

		this.username = username;
		this.password = password;
	}

	@Override
	protected Map<String, String> getPostParams() {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(PARAM_USERNAME, username);
		paramsMap.put(PARAM_PASSWORD, password);

		return paramsMap;
	}

	@Override
	protected Class<Account> getResponseClass() {
		return Account.class;
	}

}
