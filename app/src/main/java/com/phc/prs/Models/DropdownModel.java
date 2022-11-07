package com.phc.prs.Models;
public class DropdownModel {


    /**
     * data : เครื่องไม่ทำงาน
     * value : 1
     * ProblemNameEng : The machine does not work
     */

    private String data;
    private String value;
    private String ProblemNameEng;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProblemNameEng() {
        return ProblemNameEng;
    }

    public void setProblemNameEng(String ProblemNameEng) {
        this.ProblemNameEng = ProblemNameEng;
    }
}
