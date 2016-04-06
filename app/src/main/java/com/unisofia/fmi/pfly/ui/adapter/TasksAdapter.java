package com.unisofia.fmi.pfly.ui.adapter;


import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;

import org.w3c.dom.Text;

import java.util.List;

public class TasksAdapter extends BaseAdapter {

    private List<Task> mTasks;
    private SparseBooleanArray mSelectedItemsIds;

    public TasksAdapter(List<Task> tasks) {
        mTasks = tasks;
        mSelectedItemsIds = new SparseBooleanArray();
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
            holder.mScoreTextView = (TextView) convertView.findViewById(R.id.fly_score_holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);

        holder.mTitleTextView.setText(task.getName());
        holder.mDescriptionTextView.setText(task.getDescription());
        holder.mScoreTextView.setText(task.getFlyScore()+"");

        return convertView;
    }

    public void remove(Task object) {
        mTasks.remove(object);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mScoreTextView;
    }

}
