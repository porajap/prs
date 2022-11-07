package com.phc.prs.Models;

public class RegisterDropdownModel {

    /**
     * optionTextTh : ผู้ใช้ทั่วไป
     * optionTextEng : Normal
     * optionId : "0"
     */

    private String optionTextTh;
    private String optionTextEng;
    private String optionId;

    public String getOptionTextTh() {
        return optionTextTh;
    }

    public void setOptionTextTh(String optionTextTh) {
        this.optionTextTh = optionTextTh;
    }

    public String getOptionTextEng() {
        return optionTextEng;
    }

    public void setOptionTextEng(String optionTextEng) {
        this.optionTextEng = optionTextEng;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
