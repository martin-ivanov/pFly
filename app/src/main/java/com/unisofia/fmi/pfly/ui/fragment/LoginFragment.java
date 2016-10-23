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

public class LoginFragment extends BaseMenuFragment {

    private Button loginButton;
    private TextView username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (TextView) view.findViewById(R.id.username);
        password = (TextView) view.findViewById(R.id.password);
        loginButton = (Button) view.findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccountInformation();
            }
        });
    }

    private boolean validateFields(String name, String pass) {
        String msg;
        if (name.equals("")) {
            msg = "Name is required";
            username.setError(msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pass.equals("")) {
            msg = "Password is required";
            password.setError(msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getAccountInformation() {
        try {
            String name = username.getText().toString();
            String pass = password.getText().toString();

            if (validateFields(name, pass)) {
                User user = new User();
                user.setName(name);
                user.setPassword(pass);
//            user.setDeviceId(mRegId);
                UserManager.loginUser(user);
                UserManager.registerInBackground((BaseActivity) getActivity(), user);
                UserManager.loginAccountToBackend((WelcomeActivity) getActivity(), user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
