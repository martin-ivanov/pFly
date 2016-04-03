package com.unisofia.fmi.pfly.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.unisofia.fmi.pfly.R;

public class ProgressDialogFragment extends DialogFragment {

	private static boolean sIsDialogShown;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setCancelable(false);
		Activity parentActivity = getActivity();
		View content = parentActivity.getLayoutInflater().inflate(R.layout.dialog_progress, null);
		Dialog dialog = new Dialog(parentActivity, R.style.CustomDialogTheme);
		dialog.setContentView(content);
		
		return dialog;
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		if (!sIsDialogShown) {
			sIsDialogShown = true;
			super.show(manager, tag);
		}
	}

	@Override
	public void dismiss() {
		sIsDialogShown = false;
		super.dismiss();
	}
}