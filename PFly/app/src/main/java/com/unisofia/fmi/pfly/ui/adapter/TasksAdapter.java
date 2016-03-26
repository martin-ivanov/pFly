package com.unisofia.fmi.pfly.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;

import java.util.List;

public class TasksAdapter extends BaseAdapter {

    private List<Task> mTasks;

    public TasksAdapter(List<Task> tasks) {
        mTasks = tasks;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Task getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTasks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_details, parent,
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

        Task task = getItem(position);
        holder.mTitleTextView.setText(task.getName());
        holder.mDescriptionTextView.setText(task.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
    }

}
