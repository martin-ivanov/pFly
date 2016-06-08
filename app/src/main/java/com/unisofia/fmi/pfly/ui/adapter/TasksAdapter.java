package com.unisofia.fmi.pfly.ui.adapter;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
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
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.delete.BaseDeleteRequest;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;
import com.unisofia.fmi.pfly.api.util.CalendarUtil;
import com.unisofia.fmi.pfly.ui.filter.AndCriteria;
import com.unisofia.fmi.pfly.ui.filter.ClearnessCriteria;
import com.unisofia.fmi.pfly.ui.filter.ClosenessCriteria;
import com.unisofia.fmi.pfly.ui.filter.Criteria;
import com.unisofia.fmi.pfly.ui.filter.ExtImportanceCriteria;
import com.unisofia.fmi.pfly.ui.filter.IntImportanceCriteria;
import com.unisofia.fmi.pfly.ui.filter.NameCriteria;
import com.unisofia.fmi.pfly.ui.filter.SimplicityCriteria;
import com.unisofia.fmi.pfly.ui.fragment.TaskFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public TasksAdapter(final Context context, Long projectId, Boolean getClosedTasks) {
        mTasks = new ArrayList<>();
        mContext = context;
        mSelectedItemsIds = new SparseBooleanArray();
        fetchTasks(projectId, getClosedTasks);
    }

    public void fetchTasks(Long projectId, Boolean getClosedTasks) {
        StringBuilder sb = new StringBuilder();
        if (projectId != null && projectId != 0) {
            sb.append(ApiConstants.PROJECT_API_METHOD).append("/").append(projectId);
        } else {
            sb.append(ApiConstants.ACCOUNT_API_METHOD).append("/").append(UserManager.getUserId());
        }
        sb.append(ApiConstants.TASK_API_METHOD);

        Map<String, String> params = null;
        if (getClosedTasks != null) {
            params = new HashMap<>();
            params.put("getClosedTasks", getClosedTasks.toString());
        }

        BaseGsonRequest<Task[]> taskGetRequest = new BaseGetRequest<>(
                mContext,
                sb.toString(),
                params,
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
//                        Toast.makeText(mContext, "Response successful", Toast.LENGTH_SHORT).show();
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
            holder.mStatus = (TextView) convertView.findViewById(R.id.task_status);
            holder.mFlyIndicator = (ImageView) convertView.findViewById(R.id.fly_indicator);
            holder.mActionIndicator = (GradientDrawable) convertView.findViewById(R.id.action_indicator).getBackground();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);

        holder.mTitleTextView.setText(task.getName());
        if (task.getDescription() != null) {
            holder.mDescriptionTextView.setText(task.getDescription());
        } else {
            holder.mDescriptionTextView.setVisibility(View.GONE);
        }
        holder.mScoreTextView.setText("Score: " + task.getFlyScore());
        holder.mStatus.setText(Task.TaskStatus.getStatus(task.getStatus()));
        if (task.getTakenAction() != null) {
            holder.mActionIndicator.setColor(mContext.getResources().getColor(
                    Task.TaskAction.getAction(task.getTakenAction()).getColor()));
        }
        if (task.getFlyScore() > 0) {
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
                CalendarUtil.deleteTaskFromCalendar(mContext, task);
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
        TextView mStatus;
        ImageView mFlyIndicator;
        GradientDrawable mActionIndicator;
    }

    class TaskFilter extends Filter {
        private Set<Criteria> criterias = new HashSet<>();

        private String setCriteria(String constraint) {
            try {
                int id = Integer.parseInt(constraint);
                switch (id) {
                    case R.id.intImportanceFilter:
                        criterias.add(IntImportanceCriteria.getInstance());
                        break;
                    case R.id.extImportanceFilter:
                        criterias.add(ExtImportanceCriteria.getInstance());
                        break;
                    case R.id.clearnessFilter:
                        criterias.add(ClearnessCriteria.getInstance());
                        break;
                    case R.id.closenessFilter:
                        criterias.add(ClosenessCriteria.getInstance());
                        break;
                    case R.id.simplicityFilter:
                        criterias.add(SimplicityCriteria.getInstance());
                        break;
                }
            } catch (NumberFormatException nfe) {
                criterias.add(NameCriteria.getInstance());
                return constraint;
            }
            return "";
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                String[] constraints = constraint.toString().split(",");
                String filterString = "";
                for (int i = 0; i < constraints.length; i++) {
                    filterString = setCriteria(constraints[i]);
                }

                Criteria endCriteria = new AndCriteria(criterias);
                List<Task> taskList = endCriteria.meetCriteria(originalTasks, filterString);

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
