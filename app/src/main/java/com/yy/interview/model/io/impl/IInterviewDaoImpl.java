package com.yy.interview.model.io.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.interview.model.entity.Interview;
import com.yy.interview.model.io.IInterviewDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public class IInterviewDaoImpl implements IInterviewDao {
    @Override
    public List<Interview> getAllInterviews(SQLiteDatabase db, Integer pageNo, Integer pageSize,OrderBy orderBy) {
        String o = "";
        switch (orderBy.toString()){
            case "interviewTime":
                o = "interviewTime asc";
                break;
            case "state":
                o = "state asc";
                break;
        }
        List<Interview> interviews = new ArrayList<>();
        Cursor cursor = db.query("interview", null, "", new String[]{}, null, null, o,pageNo+","+pageSize);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Interview interview = new Interview();
            interview.setId(cursor.getInt(cursor.getColumnIndex("id")));
            interview.setCompanyName(cursor.getString(cursor.getColumnIndex("companyName")));
            interview.setInterviewAddress(cursor.getString(cursor.getColumnIndex("interviewAddress")));
            interview.setInterviewTime(cursor.getString(cursor.getColumnIndex("interviewTime")));
            interview.setLinkedPhone(cursor.getString(cursor.getColumnIndex("linkedPhone")));
            interview.setState(cursor.getInt(cursor.getColumnIndex("state")));
            interviews.add(interview);
            cursor.moveToNext();
        }
        return interviews;
    }

    @Override
    public Long addInterview(SQLiteDatabase db,String companyName, String interviewTime, String interviewAddress, String linkedPhone) {
        ContentValues values = new ContentValues();
        values.put("companyName",companyName);
        values.put("interviewTime",interviewTime);
        values.put("interviewAddress",interviewAddress);
        values.put("linkedPhone",linkedPhone);
        values.put("state",0);
        return db.insert("interview",null,values);
    }

    @Override
    public Integer updateState(SQLiteDatabase db, Integer id, Integer state) {
        ContentValues values = new ContentValues();
        values.put("state",state);
        return db.update("interview",values,"id=?",new String[]{id+""});
    }

    @Override
    public Integer delById(SQLiteDatabase db,Integer id) {
        return db.delete("interview","id=?",new String[]{id+""});
    }
}
