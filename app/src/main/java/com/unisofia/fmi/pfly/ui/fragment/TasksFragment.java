package com.unisofia.fmi.pfly.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.adapter.TaskPagerAdapter;
import com.unisofia.fmi.pfly.ui.adapter.TasksAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TasksFragment extends BaseMenuFragment {

    private View fragmentView;
    private ListView mTasksListView;
    private TasksAdapter mTasksAdapter;


    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private OnTaskSelectedListener mListener;
    private CoordinatorLayout rootLayout;
    private FloatingActionButton fabBtn;
    private Dialog filterDialog;
    private List<CheckBox> listFilterCriteria;
    private Long projectId;
    private Boolean getClosedTasks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnTaskSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnTaskSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        filterDialog = new Dialog(getActivity());
        filterDialog.setContentView(R.layout.dialog_tasks_filter);
        filterDialog.setTitle("Filter by:");
        listFilterCriteria = initFilterDialog(filterDialog);
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pfly_tasks_options, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_tasks:
                listFilterCriteria = initFilterDialog(filterDialog);
                mTasksAdapter.fetchTasks(projectId, getClosedTasks);
                return true;
            case R.id.sort_tasks:
                sortTasks();
                mTasksAdapter.notifyDataSetChanged();
                return true;
            case R.id.filter_tasks:
                filterTasks();
                return true;
            case R.id.action_search:
                searchTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String setCriteria(List<CheckBox> listCheckBox) {
        StringBuilder criteria = new StringBuilder();
        for (CheckBox checkBox : listCheckBox) {
            if (checkBox.isChecked()) {
                criteria.append(checkBox.getId())
                        .append(",");
            }
        }
        return criteria.toString();
    }

    private List<CheckBox> initFilterDialog(Dialog dialog) {
        List<CheckBox> listCheckBox = new ArrayList<>();
        CheckBox intImportanceFilter = (CheckBox) dialog.findViewById(R.id.intImportanceFilter);
        intImportanceFilter.setChecked(false);
        listCheckBox.add(intImportanceFilter);
        CheckBox extImportanceFilter = (CheckBox) dialog.findViewById(R.id.extImportanceFilter);
        extImportanceFilter.setChecked(false);
        listCheckBox.add(extImportanceFilter);
        CheckBox simplicityFilter = (CheckBox) dialog.findViewById(R.id.simplicityFilter);
        simplicityFilter.setChecked(false);
        listCheckBox.add(simplicityFilter);
        CheckBox closenessFilter = (CheckBox) dialog.findViewById(R.id.closenessFilter);
        closenessFilter.setChecked(false);
        listCheckBox.add(closenessFilter);
        CheckBox clearnessFilter = (CheckBox) dialog.findViewById(R.id.clearnessFilter);
        clearnessFilter.setChecked(false);
        listCheckBox.add(clearnessFilter);
        return listCheckBox;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;

        Bundle args = this.getArguments();
        if (args != null) {
            Log.d("Marto", args.toString());
            projectId = args.getLong("projectId");
            Object object = args.get("getClosedTasks");
            if (object != null){
                getClosedTasks = Boolean.parseBoolean(object.toString());
            }
        }
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.rootLayout);
        fabBtn = (FloatingActionButton) view.findViewById(R.id.addTask);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new TaskFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mTasksAdapter = new TasksAdapter(getActivity(), projectId, getClosedTasks);
        mTasksListView = (ListView) fragmentView.findViewById(R.id.listview_tasks);
        mTasksListView.setAdapter(mTasksAdapter);
        mTasksListView.setEmptyView(fragmentView.findViewById(R.id.emptyResults));
        mTasksListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mListener.onTaskSelected(mTasksAdapter.getItem(position));
            }
        });

        mTasksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mTasksListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                final int checkedCount = mTasksListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " selected");
                mTasksAdapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_task:
                        deleteTasks();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_pfly_delete_task, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mTasksAdapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });
    }

    private void deleteTasks() {
        SparseBooleanArray selected = mTasksAdapter
                .getSelectedIds();
        List<Task> removedTasks = new ArrayList<>();
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                removedTasks.add(mTasksAdapter
                        .getItem(selected.keyAt(i)));
            }
        }
        if (removedTasks.size() > 0) {
            for (Task task : removedTasks) {
                mTasksAdapter.remove(task);
            }
        }
    }

    private void sortTasks() {
//        Toast.makeText(getActivity(), "Sorting....", Toast.LENGTH_SHORT).show();
        List<Task> tasks = mTasksAdapter.getTasks();
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                //TODO: make closed tasks last
                    return rhs.getFlyScore() - lhs.getFlyScore();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int limit = Integer.parseInt(prefs.getString("listLimitPref", "10"));

        if (limit < tasks.size()) {
            tasks = tasks.subList(0, limit);
            Toast.makeText(getActivity(), "Limiting first " + limit + " task(s)", Toast.LENGTH_SHORT).show();
        }
        mTasksAdapter.setTasks(tasks);
    }

    private void filterTasks() {
        Toast.makeText(getActivity(), "Filter dialog....", Toast.LENGTH_LONG).show();

        Button dialogButton = (Button) filterDialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                StringBuilder criteriaQuery = new StringBuilder();
                criteriaQuery.append(setCriteria(listFilterCriteria));
                if (mSearchView.getQuery().length() > 0) {
                    criteriaQuery.append(mSearchView.getQuery());
                }
                mTasksAdapter.getFilter()
                        .filter(criteriaQuery);
            }
        });
        filterDialog.show();
    }

    private void searchTasks() {
//        searchMenuItem.setVisible(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getActivity(), newText, Toast.LENGTH_LONG).show();
                StringBuilder criteriaQuery = new StringBuilder();
                if (setCriteria(listFilterCriteria).length() > 0) {
                    criteriaQuery.append(setCriteria(listFilterCriteria));
                }
                criteriaQuery.append(newText);
                mTasksAdapter.getFilter()
                        .filter(criteriaQuery);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mTasksAdapter.getFilter()
                        .filter(setCriteria(listFilterCriteria));
                return false;
            }
        });
    }

    // Container Activity must implement this interface
    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }

}
