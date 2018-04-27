package com.yy.interview.model.entity;

import java.io.Serializable;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public class Interview implements Serializable {
    private Integer id;
    private String companyName;
    private String interviewTime;
    private String interviewAddress;
    private String linkedPhone;
    private Integer state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getInterviewAddress() {
        return interviewAddress;
    }

    public void setInterviewAddress(String interviewAddress) {
        this.interviewAddress = interviewAddress;
    }

    public String getLinkedPhone() {
        return linkedPhone;
    }

    public void setLinkedPhone(String linkedPhone) {
        this.linkedPhone = linkedPhone;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
