package com.unisofia.fmi.pfly.ui.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;

public class ProjectsAdapter extends BaseAdapter {

	private List<Project> mProjects;

	public ProjectsAdapter(List<Project> projects) {
		mProjects = projects;
	}

	@Override
	public int getCount() {
		return mProjects.size();
	}

	@Override
	public Project getItem(int position) {
		return mProjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mProjects.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.list_item_project, parent,
					false);

			holder = new ViewHolder();
			holder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.textview_title);
			holder.mDescriptionTextView = (TextView) convertView
					.findViewById(R.id.textview_description);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Project project= getItem(position);
		holder.mTitleTextView.setText(project.getName());
		holder.mDescriptionTextView.setText(project.getDescription());

		return convertView;
	}

	static class ViewHolder {
		TextView mTitleTextView;
		TextView mDescriptionTextView;
	}

}
