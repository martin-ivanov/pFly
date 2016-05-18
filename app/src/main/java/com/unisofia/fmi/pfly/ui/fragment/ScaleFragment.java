package com.unisofia.fmi.pfly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.adapter.ProjectsAdapter;

public class ScaleFragment extends BaseMenuFragment {

	private ListView mProjectsListView;
	private ProjectsAdapter mProjectsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_scale, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView executeTextView = (TextView)view.findViewById(R.id.executeScaleItem);
//		executeTextView.setText(TaskBck.TaskAction.EXECUTE.toString());

	}
}
