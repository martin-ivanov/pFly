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
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.delete.BaseDeleteRequest;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;
import com.unisofia.fmi.pfly.ui.filter.AndCriteria;
import com.unisofia.fmi.pfly.ui.filter.ClearnessCriteria;
import com.unisofia.fmi.pfly.ui.filter.ClosenessCriteria;
import com.unisofia.fmi.pfly.ui.filter.Criteria;
import com.unisofia.fmi.pfly.ui.filter.ExtImportanceCriteria;
import com.unisofia.fmi.pfly.ui.filter.IntImportanceCriteria;
import com.unisofia.fmi.pfly.ui.filter.SimplicityCriteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public TasksAdapter(final Context context) {
        mTasks = new ArrayList<>();
        mContext = context;
        mSelectedItemsIds = new SparseBooleanArray();
        fetchTasks();
    }

    public void fetchTasks() {
        BaseGsonRequest<Task[]> taskGetRequest = new BaseGetRequest<>(
                mContext,
                ApiConstants.TASK_API_METHOD,
                null,
                Task[].class,
                new RequestErrorListener(mContext, null)
        );

        RequestManager.sendRequest(
                mContext,
                null,
                taskGetRequest,
                new Response.Listener<Task[]>() {
                    @Override
                    public void onResponse(Task[] response) {
                        mTasks.clear();
                        mTasks.addAll(Arrays.asList(response));
                        originalTasks = mTasks;
                        Toast.makeText(mContext, "Response successful", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
    }

    public List<Task> getTasks() {
        return mTasks;
    }

    public void setTasks(List<Task> mTasks) {
        this.mTasks = mTasks;
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
        return mTasks.get(position).getTaskId();
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
        if (task.getTakenAction() != null) {
            holder.mActionIndicator.setColor(mContext.getResources().getColor(
                    Task.TaskAction.getAction(task.getTakenAction()).getColor()));
        }
        //TODO: set value to 0 once there is a backend distribution of tasks
        if (task.getFlyScore() > 10) {
            holder.mFlyIndicator.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void remove(final Task task) {

        Gson gson = new Gson();
        BaseGsonRequest<String> taskDeleteRequest = new BaseDeleteRequest<String>(
                mContext,
                ApiConstants.TASK_API_METHOD + "/" + task.getTaskId(),
                new RequestErrorListener(mContext, null)) {
            @Override
            protected Class<String> getResponseClass() {
                return String.class;
            }

            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }
        };

        RequestManager.sendRequest(mContext, null, taskDeleteRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mContext, "Tasks deleted", Toast.LENGTH_SHORT).show();
                mTasks.remove(task);
                notifyDataSetChanged();
            }
        });
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
            List<Criteria> criterias = new ArrayList<>();
            if (constraint != null && constraint.length() > 0) {
                String[] constraints = constraint.toString().split(",");

                int id;
                for (int i = 0; i < constraints.length; i++) {
                    id = Integer.parseInt(constraints[i]);
                    if (id == R.id.intImportanceFilter) {
                        criterias.add(new IntImportanceCriteria());
                    }
                    if (id == R.id.extImportanceFilter) {
                        criterias.add(new ExtImportanceCriteria());
                    }
                    if (id == R.id.clearness) {
                        criterias.add(new ClearnessCriteria());
                    }
                    if (id == R.id.closeness) {
                        criterias.add(new ClosenessCriteria());
                    }
                    if (id == R.id.simplicity) {
                        criterias.add(new SimplicityCriteria());
                    }
                }

                Criteria endCriteria = new AndCriteria(criterias);
                List<Task> taskList = endCriteria.meetCriteria(originalTasks);

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
