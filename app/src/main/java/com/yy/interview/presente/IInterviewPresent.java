package com.yy.interview.presente;

import com.yy.interview.model.entity.Interview;

import java.util.List;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public interface IInterviewPresent {

    void showInterviews(List<Interview> interviews);

    void addCallback(boolean result);
}
