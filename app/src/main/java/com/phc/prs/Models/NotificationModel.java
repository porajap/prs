package com.phc.prs.Models;

public class NotificationModel {

    /**
     * ID : 229
     * Barcode2d : 00900049
     * repairStatus : 2
     * dateReceive : 07/08/2020
     * timeReceiveTh : 16:47
     * timeReceiveEng : 16:47 PM
     * dateRepair : 10/08/2020
     * timeRepairTh : 08:00
     * timeRepairEng : 08:00 AM
     * dayDiff : 3
     * timeDiff : 78
     * dayOfWeek : 0
     * Notification_Read_Scroll : 2
     * Notification_Read : 2
     */

    private String ID;
    private String dispenserID;
    private String repairStatus;
    private String dateReceive;
    private String timeReceiveTh;
    private String timeReceiveEng;
    private String dateRepair;
    private String timeRepairTh;
    private String timeRepairEng;
    private String dayDiff;
    private String timeDiff;
    private String dayOfWeek;
    private String Notification_Read_Scroll;
    private String Notification_Read;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDispenserID() {
        return dispenserID;
    }

    public void setDispenserID(String Barcode2d) {
        this.dispenserID = Barcode2d;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    public String getDateReceive() {
        return dateReceive;
    }

    public void setDateReceive(String dateReceive) {
        this.dateReceive = dateReceive;
    }

    public String getTimeReceiveTh() {
        return timeReceiveTh;
    }

    public void setTimeReceiveTh(String timeReceiveTh) {
        this.timeReceiveTh = timeReceiveTh;
    }

    public String getTimeReceiveEng() {
        return timeReceiveEng;
    }

    public void setTimeReceiveEng(String timeReceiveEng) {
        this.timeReceiveEng = timeReceiveEng;
    }

    public String getDateRepair() {
        return dateRepair;
    }

    public void setDateRepair(String dateRepair) {
        this.dateRepair = dateRepair;
    }

    public String getTimeRepairTh() {
        return timeRepairTh;
    }

    public void setTimeRepairTh(String timeRepairTh) {
        this.timeRepairTh = timeRepairTh;
    }

    public String getTimeRepairEng() {
        return timeRepairEng;
    }

    public void setTimeRepairEng(String timeRepairEng) {
        this.timeRepairEng = timeRepairEng;
    }

    public String getDayDiff() {
        return dayDiff;
    }

    public void setDayDiff(String dayDiff) {
        this.dayDiff = dayDiff;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getNotification_Read_Scroll() {
        return Notification_Read_Scroll;
    }

    public void setNotification_Read_Scroll(String Notification_Read_Scroll) {
        this.Notification_Read_Scroll = Notification_Read_Scroll;
    }

    public String getNotification_Read() {
        return Notification_Read;
    }

    public void setNotification_Read(String Notification_Read) {
        this.Notification_Read = Notification_Read;
    }
}
