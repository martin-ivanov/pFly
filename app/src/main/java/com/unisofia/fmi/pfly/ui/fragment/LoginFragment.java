package com.unisofia.fmi.pfly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
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
                Account account = new Account();
                account.setName(name);
                account.setPassword(pass);
//            account.setDeviceId(mRegId);
                UserManager.loginUser(account);
                UserManager.registerInBackground((BaseActivity) getActivity(), account);
                UserManager.loginAccountToBackend((WelcomeActivity) getActivity(), account);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
