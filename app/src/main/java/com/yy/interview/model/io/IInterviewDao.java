package com.yy.interview.model.io;

import android.database.sqlite.SQLiteDatabase;

import com.yy.interview.model.entity.Interview;

import java.util.List;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public interface IInterviewDao {
    enum OrderBy{
        id,interviewTime,state
    }

    List<Interview> getAllInterviews(SQLiteDatabase db, Integer pageNo, Integer pageSize, OrderBy orderBy);

    Long addInterview(SQLiteDatabase db, String companyName, String interviewTime, String interviewAddress, String linkedPhone);

    Integer updateState(SQLiteDatabase db,Integer id,Integer state);

    Integer delById(SQLiteDatabase db,Integer id);

}
