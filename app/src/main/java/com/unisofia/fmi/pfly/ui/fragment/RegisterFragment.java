package com.unisofia.fmi.pfly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.usermanagement.User;
import com.unisofia.fmi.pfly.usermanagement.UserManager;
import com.unisofia.fmi.pfly.api.model.Account;
import com.unisofia.fmi.pfly.ui.activity.BaseActivity;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

public class RegisterFragment extends BaseMenuFragment {

	private Button registerButton;
	private TextView username, email, password, confirmPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_register, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		username = (TextView) view.findViewById(R.id.username);
		email = (TextView) view.findViewById(R.id.email);
		password = (TextView) view.findViewById(R.id.password);
		confirmPassword = (TextView) view.findViewById(R.id.confirmPassword);
		registerButton = (Button) view.findViewById(R.id.register);

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccountInformation();

			}
		});
	}

	private boolean validateFields(String name, String mail, String pass, String confirmPass) {
		String msg;
		if (name.equals("")) {
			msg = "Name is required";
			username.setError(msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (mail.equals("")) {
			msg = "Email is required";
			email.setError(msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (pass.equals("")) {
			msg = "Password is required";
			password.setError(msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (confirmPass.equals("") || !pass.equals(confirmPass)) {
			msg = "Passwords not match";
			password.setError(msg);
			confirmPassword.setError(msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}



	private void getAccountInformation() {
		try {
			String name = username.getText().toString();
			String mail = email.getText().toString();
			String pass = password.getText().toString();
			String confirmPass = confirmPassword.getText().toString();

			if (validateFields(name, mail, pass, confirmPass)) {
				User user = new User();
				user.setName(name);
				user.setPassword(pass);
				user.setEmail(mail);
//				UserManager.loginUser(account);
				UserManager.registerInBackground((BaseActivity) getActivity(), user);
				UserManager.saveAccount((WelcomeActivity) getActivity(), user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
