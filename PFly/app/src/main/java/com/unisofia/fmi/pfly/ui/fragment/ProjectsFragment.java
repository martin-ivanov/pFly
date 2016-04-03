package com.unisofia.fmi.pfly.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.ui.activity.TasksActivity;
import com.unisofia.fmi.pfly.ui.adapter.ProjectsAdapter;

public class ProjectsFragment extends BaseMenuFragment {

	private ListView mProjectsListView;
	private ProjectsAdapter mProjectsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_projects, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

//		TODO get from server
		List<Project> projects = new ArrayList<Project>();
		for (int i = 0; i < 8; i++) {
			projects.add(new Project());
		}

		mProjectsAdapter = new ProjectsAdapter(projects);
		mProjectsListView = (ListView) view
				.findViewById(R.id.listview_projects);
		mProjectsListView.setAdapter(mProjectsAdapter);
		mProjectsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), TasksActivity.class);
				intent.putExtra(TasksActivity.EXTRA_CATEGORY, mProjectsAdapter.getItem(position));
				startActivity(intent);
			}
		});
	}
}
