package com.unisofia.fmi.pfly.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.activity.TasksActivity;
import com.unisofia.fmi.pfly.ui.adapter.ProjectsAdapter;

public class ProjectsFragment extends BaseMenuFragment {

	private ListView mProjectsListView;
	private ProjectsAdapter mProjectsAdapter;
	private OnProjectSelectedListener mListener;
	private FloatingActionButton fabBtn;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mListener = (OnProjectSelectedListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ " must implement OnProjectSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_projects, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mProjectsAdapter = new ProjectsAdapter(getActivity());
		mProjectsListView = (ListView) view
				.findViewById(R.id.listview_projects);
		mProjectsListView.setAdapter(mProjectsAdapter);
		mProjectsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onProjectSelected(mProjectsAdapter.getItem(position));
			}
		});


		fabBtn = (FloatingActionButton) view.findViewById(R.id.addProject);
		fabBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}


	// Container Activity must implement this interface
	public interface OnProjectSelectedListener {
		void onProjectSelected(Project project);
	}

}
