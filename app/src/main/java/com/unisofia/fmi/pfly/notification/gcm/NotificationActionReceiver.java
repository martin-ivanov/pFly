package com.unisofia.fmi.pfly.notification.gcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.delete.BaseDeleteRequest;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.api.request.put.BasePutRequest;
import com.unisofia.fmi.pfly.ui.activity.HomeActivity;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

import java.util.Map;

/**
 * Created by martin.ivanov on 2016-06-04.
 */
public class NotificationActionReceiver extends BroadcastReceiver {
    Context context = WelcomeActivity.getAppContext();
    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int notificationId = intent.getIntExtra("notificationId", 0);

        if (action.equalsIgnoreCase("declineIntent")){
            declineTask(intent.getStringExtra("taskId"), intent.getStringExtra("taskActionType"));
        } else {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeIntent);
        }

        manager.cancel(notificationId);
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    private void declineTask(String taskId, String taskActionType) {
        if (taskId != null && taskActionType !=null) {
            Gson gson = new Gson();
            StringBuilder sb = new StringBuilder();
            sb.append(ApiConstants.TASK_API_METHOD)
                    .append("/")
                    .append(taskId)
                    .append("/")
                    .append(taskActionType)
                    .append("/")
                    .append("decline");



            BaseGsonRequest<String> taskDeclineRequest = new BasePutRequest<String>(
                    context,
                    sb.toString(),
                    null,
                    String.class,
                    new RequestErrorListener(context, null)) {

            };

            RequestManager.sendRequest(context, null, taskDeclineRequest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Task declined", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}


