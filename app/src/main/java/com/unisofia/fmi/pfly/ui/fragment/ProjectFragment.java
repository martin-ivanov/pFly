
package com.unisofia.fmi.pfly.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.api.util.DateSerializer;
import com.unisofia.fmi.pfly.notification.reminder.RemindersContentProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ProjectFragment extends DialogFragment {
    private EditText mProjectName;
    private EditText mProjectDesc;
    private Button createProjectButton;
    private Button cancelButton;

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.new_project);
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProjectName = (EditText) view.findViewById(R.id.projectName);
        mProjectDesc = (EditText) view.findViewById(R.id.projectDesc);

        createProjectButton = (Button) view.findViewById(R.id.create_project);
        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = mProjectName.getText().toString();
                String projectDescription = mProjectDesc.getText().toString();
                if (projectName.trim().equals("")) {
                    mProjectName.setError("Reminder name is required");
                } else if (projectDescription.trim().equals("")) {
                    mProjectDesc.setError("Reminder date is required");
                } else {
                    createProject(projectName, projectDescription);
                }
            }
        });
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void createProject(String projectName, String projectDesc){
        Project project = new Project();
        project.setName(projectName);
        project.setDescription(projectDesc);
        project.setDateCreated(Calendar.getInstance().getTime());
        project.setCreatorId(UserManager.getUserId());

        postProjectToServer(project);
    }

    private void postProjectToServer(Project project) {

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
        Gson gson = gsonBuilder.create();


        BaseGsonRequest<Project> projectPostRequest = new BasePostRequest<Project>(getActivity(), ApiConstants.PROJECT_API_METHOD,
                gson.toJson(project), new RequestErrorListener(getActivity(), null)) {
            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }

            @Override
            protected Class<Project> getResponseClass() {
                return Project.class;
            }
        };

        RequestManager.sendRequest(getActivity(), null, projectPostRequest, new Response.Listener<Project>() {
            @Override
            public void onResponse(Project response) {
                Toast.makeText(getActivity(), "Project saved.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
