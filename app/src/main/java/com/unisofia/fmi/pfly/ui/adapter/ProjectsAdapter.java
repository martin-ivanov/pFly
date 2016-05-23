package com.unisofia.fmi.pfly.ui.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Api;
import com.rey.material.widget.Spinner;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;

public class ProjectsAdapter extends BaseAdapter {


    private List<Project> mProjects;

    public ProjectsAdapter(List<Project> projects) {
        mProjects = projects;
    }


    public ProjectsAdapter() {
        super();
    }

    public List<Project> getProjecs() {
        return mProjects;
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

        Project project = getItem(position);
        holder.mTitleTextView.setText(project.getName());
        holder.mDescriptionTextView.setText(project.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
    }

}
