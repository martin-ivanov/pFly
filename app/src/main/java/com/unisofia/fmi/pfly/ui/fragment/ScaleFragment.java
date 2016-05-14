package com.unisofia.fmi.pfly.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.activity.TasksActivity;
import com.unisofia.fmi.pfly.ui.adapter.ProjectsAdapter;

import java.util.ArrayList;
import java.util.List;

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
		executeTextView.setText(Task.TaskAction.EXECUTE.toString());

	}
}
