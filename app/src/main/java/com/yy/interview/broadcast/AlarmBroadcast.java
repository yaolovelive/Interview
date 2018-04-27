package com.yy.interview.broadcast;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.yy.interview.R;
import com.yy.interview.presente.InterviewPresent;

import java.text.ParseException;

/**
 * Created by yao on 18-4-27.
 */

public class AlarmBroadcast extends BroadcastReceiver {
    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {
        String date = intent.getStringExtra("date");
        Notification notification = null;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"1");
        builder.setContentText("将有一个计划于3小时后开始").setContentTitle("便签通知").setSmallIcon(R.mipmap.ic_launcher).setPriority(Notification.PRIORITY_MAX).setVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1","name",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("1");
        }
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
        try {
            notification.when = InterviewPresent.dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0x001,notification);
    }
}
