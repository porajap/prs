package com.phc.prs.Models;

public class RegisterModel {


    /**
     * cusLogin : 42
     * fName : บจก.แอดวานซ์เมดิคอลคลินิก
     * permission : 0
     * name : เทสแผนก
     * departmentNameEng : Dental Center
     * departmentNameTh : ศูนย์ทันตกรรม 
     * date : 21/09/2020
     * timeTh : 11:37
     * timeEng : 11:37 AM
     */

    private String cusLogin;
    private String fName;
    private String permission;
    private String name;
    private String departmentNameEng;
    private String departmentNameTh;
    private String date;
    private String timeTh;
    private String timeEng;

    public String getCusLogin() {
        return cusLogin;
    }

    public void setCusLogin(String cusLogin) {
        this.cusLogin = cusLogin;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentNameEng() {
        return departmentNameEng;
    }

    public void setDepartmentNameEng(String departmentNameEng) {
        this.departmentNameEng = departmentNameEng;
    }

    public String getDepartmentNameTh() {
        return departmentNameTh;
    }

    public void setDepartmentNameTh(String departmentNameTh) {
        this.departmentNameTh = departmentNameTh;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeTh() {
        return timeTh;
    }

    public void setTimeTh(String timeTh) {
        this.timeTh = timeTh;
    }

    public String getTimeEng() {
        return timeEng;
    }

    public void setTimeEng(String timeEng) {
        this.timeEng = timeEng;
    }
}
