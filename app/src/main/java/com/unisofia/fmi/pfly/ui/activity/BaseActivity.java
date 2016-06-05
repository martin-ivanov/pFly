package com.unisofia.fmi.pfly.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.fragment.ProgressDialogFragment;

//public abstract class BaseActivity extends FragmentActivity {

public abstract class BaseActivity extends AppCompatActivity{
	private ProgressDialogFragment mProgressDialogFragment;
	private boolean mIsProgressShown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mProgressDialogFragment = new ProgressDialogFragment();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
	}

	public void showProgress() {
		if (!mIsProgressShown) {
			mProgressDialogFragment.show(getFragmentManager(), "tag");
			mIsProgressShown = true;
		}
	}

	public void hideProgress() {
		if (mIsProgressShown) {
			try {
				mProgressDialogFragment.dismiss();
			}catch(Exception ex){
				Log.d("FRAGMENT DISMISS", "failure");
			}

			mIsProgressShown = false;
		}
	}
}
