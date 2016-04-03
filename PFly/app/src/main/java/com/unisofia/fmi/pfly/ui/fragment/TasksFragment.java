package com.unisofia.fmi.pfly.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.activity.TasksActivity;
import com.unisofia.fmi.pfly.ui.adapter.TasksAdapter;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends BaseMenuFragment {

    private ListView mTasksListView;
    private TasksAdapter mTasksAdapter;

    CoordinatorLayout rootLayout;
    FloatingActionButton fabBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//		TODO get from server
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 8; i++) {
            tasks.add(new Task());
        }

        mTasksAdapter = new TasksAdapter(tasks);
        mTasksListView = (ListView) view
                .findViewById(R.id.listview_tasks);
        mTasksListView.setAdapter(mTasksAdapter);
        mTasksListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), TasksActivity.class);
                intent.putExtra(TasksActivity.EXTRA_CATEGORY, mTasksAdapter.getItem(position));
                startActivity(intent);
            }
        });

        rootLayout = (CoordinatorLayout) view.findViewById(R.id.rootLayout);
        fabBtn = (FloatingActionButton) view.findViewById(R.id.addTask);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new TaskFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
