package com.phc.prs.Models;

public class UserLoginModel {

    /**
     * Cus_Login : 11
     * Cus_Code : 491037
     * Cus_Dep_Code : 1
     * UserName : bnh
     * Permission : 2
     * Cus_Name : บ.บีเอ็นเอช เมดิเคิล เซนเตอร์ จก.
     * Cus_Dep_Name : ห้องผ่าตัด
     * organizationName : บ.บีเอ็นเอช เมดิเคิล เซนเตอร์ จก.
     * isManager : 0
     */

    private String Cus_Login;
    private String Cus_Code;
    private String Cus_Dep_Code;
    private String UserName;
    private String Permission;
    private String Cus_Name;
    private String Cus_Dep_Name;
    private String organizationName;
    private String isManager;

    public String getCus_Login() {
        return Cus_Login;
    }

    public void setCus_Login(String Cus_Login) {
        this.Cus_Login = Cus_Login;
    }

    public String getCus_Code() {
        return Cus_Code;
    }

    public void setCus_Code(String Cus_Code) {
        this.Cus_Code = Cus_Code;
    }

    public String getCus_Dep_Code() {
        return Cus_Dep_Code;
    }

    public void setCus_Dep_Code(String Cus_Dep_Code) {
        this.Cus_Dep_Code = Cus_Dep_Code;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPermission() {
        return Permission;
    }

    public void setPermission(String Permission) {
        this.Permission = Permission;
    }

    public String getCus_Name() {
        return Cus_Name;
    }

    public void setCus_Name(String Cus_Name) {
        this.Cus_Name = Cus_Name;
    }

    public String getCus_Dep_Name() {
        return Cus_Dep_Name;
    }

    public void setCus_Dep_Name(String Cus_Dep_Name) {
        this.Cus_Dep_Name = Cus_Dep_Name;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getIsManager() {
        return isManager;
    }

    public void setIsManager(String isManager) {
        this.isManager = isManager;
    }
}
