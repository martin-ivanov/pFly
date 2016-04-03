package com.unisofia.fmi.pfly.ui.activity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.adapter.TasksAdapter;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends BaseActivity {

    public static final String EXTRA_CATEGORY = "category";

    private ListView mTasksListView;
    private TasksAdapter mTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Project category = (Project) getIntent().getSerializableExtra(
                EXTRA_CATEGORY);
        // TODO get all tasks for this category
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 50; i++) {
            tasks.add(new Task());
        }

        mTasksListView = (ListView) findViewById(R.id.listview_tasks);
        mTasksAdapter = new TasksAdapter(tasks);
        mTasksListView.setAdapter(mTasksAdapter);
        mTasksListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Task task = mTasksAdapter.getItem(position);
                showDetails(task);
            }
        });

    }

    protected void showDetails(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getName());
        builder.setMessage(task.getDescription());
        builder.setNeutralButton("OK", null);

        builder.show();
    }
}
