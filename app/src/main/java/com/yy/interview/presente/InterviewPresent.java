package com.yy.interview.presente;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.yy.interview.model.entity.Interview;
import com.yy.interview.model.io.IInterviewDao;
import com.yy.interview.model.io.impl.IInterviewDaoImpl;
import com.yy.interview.model.util.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public class InterviewPresent {
    private IInterviewDao interviewDao = new IInterviewDaoImpl();

    private Context context;
    private IInterviewPresent callback;
    private SQLiteDatabase db;


    private SharedPreferences config;
    private Handler handler;

    public InterviewPresent(Context context, IInterviewPresent callback, Handler handler) {
        this.callback = callback;
        this.context = context;
        DatabaseHelper databaseHelper = new DatabaseHelper(context, "interviewdb", null, 3);
        db = databaseHelper.getReadableDatabase();
        this.handler = handler;
        config = context.getSharedPreferences("config",0);
    }

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void changeState(Integer pageNo, Integer pageSize, IInterviewDao.OrderBy orderBy) {
        new Thread(() -> {
            List<Interview> list = interviewDao.getAllInterviews(db, 0, 1000, orderBy);
            Date date = new Date();
            long now = date.getTime();
            for (int i = 0; i < list.size(); i++) {
                try {
                    Date date1 = dateFormat.parse(list.get(i).getInterviewTime());
                    long tmp = date1.getTime();
                    int house = (int) (now - tmp) / (1000 * 60 * 60);
                    if (house < 0) {
                        interviewDao.updateState(db, list.get(i).getId(), 0);
                    } else if (house < 6) {
                        interviewDao.updateState(db, list.get(i).getId(), 1);
                    } else {
                        interviewDao.updateState(db, list.get(i).getId(), 2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            getNotInterviews(pageNo, pageSize, orderBy);
        }).start();
    }

    public void getNotInterviews(Integer pageNo, Integer pageSize, IInterviewDao.OrderBy orderBy) {
        new Thread(() -> {
            List<Interview> interviews = interviewDao.getAllInterviews(db, (pageNo - 1) * pageSize, pageSize, orderBy);
            handler.post(() -> callback.showInterviews(interviews));
        }).start();
    }

    public void addInterview(String companyName, String interviewTime, String interviewAddress, String linkedPhone) {
        new Thread(() -> {
            Long l = interviewDao.addInterview(db, companyName, interviewTime, interviewAddress, linkedPhone);
            if (l > 0) {
                handler.post(() -> callback.addCallback(true));
                addAlarm(interviewTime);
            } else {
                handler.post(() -> callback.addCallback(false));
            }
        }).start();
    }

    public void cancalInterView(final Integer id) {
        new Thread(() -> interviewDao.delById(db, id)).start();
    }

    public void addAlarm(String interviewTime){
        int ts = config.getInt("ts",3);
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            calendar.setTime(dateFormat.parse(interviewTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY) - ts);
        Intent broadcast = new Intent("receiver.notify");
        broadcast.putExtra("date",interviewTime);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(),0,broadcast,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
    }

}
