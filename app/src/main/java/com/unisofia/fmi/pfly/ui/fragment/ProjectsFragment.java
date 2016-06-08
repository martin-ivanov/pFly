package com.unisofia.fmi.pfly.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.ui.adapter.ProjectsAdapter;

public class ProjectsFragment extends BaseMenuFragment {

	private ListView mProjectsListView;
	private ProjectsAdapter mProjectsAdapter;
	private OnProjectSelectedListener mListener;
	private FloatingActionButton addProjectBtn;



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
		mProjectsListView.setEmptyView(view.findViewById(R.id.emptyResults));
		mProjectsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onProjectSelected(mProjectsAdapter.getItem(position));
			}
		});


		addProjectBtn = (FloatingActionButton) view.findViewById(R.id.addProject);
		addProjectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectFragment projectFragment = new ProjectFragment();
				projectFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						mProjectsAdapter.fetchProjects();
					}
				});
				projectFragment.show(getActivity().getSupportFragmentManager(), "ProjectDialog");
			}
		});
	}

	// Container Activity must implement this interface
	public interface OnProjectSelectedListener {
		void onProjectSelected(Project project);
	}

}
