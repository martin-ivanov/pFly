package com.unisofia.fmi.pfly.ui.adapter;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TasksAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Task> mTasks;
    private List<Task> originalTasks;
    private SparseBooleanArray mSelectedItemsIds;
    private TaskFilter mFilter = new TaskFilter();

    public TasksAdapter(List<Task> tasks, Context context) {
        mTasks = tasks;
        originalTasks = tasks;
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;
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
            convertView = inflater.inflate(R.layout.list_item_task, parent,
                    false);

            holder = new ViewHolder();
            holder.mTitleTextView = (TextView) convertView
                    .findViewById(R.id.textview_title);
            holder.mDescriptionTextView = (TextView) convertView
                    .findViewById(R.id.textview_description);
            holder.mScoreTextView = (TextView) convertView.findViewById(R.id.fly_score_holder);
            holder.mFlyIndicator = (ImageView) convertView.findViewById(R.id.fly_indicator);
            holder.mActionIndicator = (GradientDrawable) convertView.findViewById(R.id.action_indicator).getBackground();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);

        holder.mTitleTextView.setText(task.getName());
        holder.mDescriptionTextView.setText(task.getDescription());
        holder.mScoreTextView.setText(task.getFlyScore() + "");
        holder.mActionIndicator.setColor(mContext.getResources().getColor(
                task.getActionTaken().getColor()));

        //TODO: set value to 0 once there is a backend distribution of tasks
        if (task.getFlyScore() > 10) {
            holder.mFlyIndicator.setVisibility(View.VISIBLE);
        }

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

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    static class ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mScoreTextView;
        ImageView mFlyIndicator;
        GradientDrawable mActionIndicator;
    }

    class TaskFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                String[] constraints = constraint.toString().split(",");
                Set<Task> filtered = new HashSet<>();
                int id;
                for (int i = 0; i < constraints.length; i++) {
                    id = Integer.parseInt(constraints[i]);
                    for (Task t : mTasks) {
                        if (id == R.id.intImportanceFilter && t.isIntImportant()) {
                            filtered.add(t);
                        }
                        if (id == R.id.extImportanceFilter && t.isExtImportant()) {
                            filtered.add(t);
                        }
                        if (id == R.id.clearness && t.isCleared()) {
                            filtered.add(t);
                        }
                        if (id == R.id.closeness && t.isClosed()) {
                            filtered.add(t);
                        }
                        if (id == R.id.simplicity && t.isSimplified()) {
                            filtered.add(t);
                        }
                    }
                }
                List<Task> taskList = new ArrayList<>();
                taskList.addAll(filtered);

                results.count = taskList.size();
                results.values = taskList;
            } else

            {
                results.count = originalTasks.size();
                results.values = originalTasks;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mTasks = (List<Task>) results.values;
            notifyDataSetChanged();
        }
    }

}
