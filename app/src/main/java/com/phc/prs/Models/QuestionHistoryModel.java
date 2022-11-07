package com.phc.prs.Models;

public class QuestionHistoryModel {


    /**
     * Cus_Name : บ.บีเอ็นเอช เมดิเคิล เซนเตอร์ จก.
     * Date_Request : 09-07-2020
     * timeEng : 09:00:59 AM
     * timeTh : 09:00:59
     * Comment : There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour,
     * Technician_Reply : admnistrator administrator
     * Reply : The standard chunk of Lorem Ipsum used since the 1500s
     * Reply_Date : 2020-07-10 13:34:58
     */

    private String Cus_Name;
    private String Date_Request;
    private String timeEng;
    private String timeTh;
    private String Comment;
    private String Technician_Reply;
    private String Reply;
    private String Reply_Date;

    public String getCus_Name() {
        return Cus_Name;
    }

    public void setCus_Name(String Cus_Name) {
        this.Cus_Name = Cus_Name;
    }

    public String getDate_Request() {
        return Date_Request;
    }

    public void setDate_Request(String Date_Request) {
        this.Date_Request = Date_Request;
    }

    public String getTimeEng() {
        return timeEng;
    }

    public void setTimeEng(String timeEng) {
        this.timeEng = timeEng;
    }

    public String getTimeTh() {
        return timeTh;
    }

    public void setTimeTh(String timeTh) {
        this.timeTh = timeTh;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public String getTechnician_Reply() {
        return Technician_Reply;
    }

    public void setTechnician_Reply(String Technician_Reply) {
        this.Technician_Reply = Technician_Reply;
    }

    public String getReply() {
        return Reply;
    }

    public void setReply(String Reply) {
        this.Reply = Reply;
    }

    public String getReply_Date() {
        return Reply_Date;
    }

    public void setReply_Date(String Reply_Date) {
        this.Reply_Date = Reply_Date;
    }
}
