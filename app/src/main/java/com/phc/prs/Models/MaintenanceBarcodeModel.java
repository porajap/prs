package com.phc.prs.Models;

public class MaintenanceBarcodeModel {


    /**
     * ID : 90
     * QrCode : 01305970
     * FName : คณะแพทย์ศาสตร์วชิรพยาบาล มหาวิทยาลัยนวมินทราธิราช
     * AreaCode : BB3
     * BuildingName : TestBuilding
     * BuildingFloor : 4
     * DepartmentName : อื่นๆ (อื่นๆ)
     * RoomName : TestRoomName
     * DispenserID : 11
     */

    private String ID;
    private String QrCode;
    private String FName;
    private String AreaCode;
    private String BuildingName;
    private String BuildingFloor;
    private String DepartmentName;
    private String RoomName;
    private String DispenserID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQrCode() {
        return QrCode;
    }

    public void setQrCode(String QrCode) {
        this.QrCode = QrCode;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String AreaCode) {
        this.AreaCode = AreaCode;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String BuildingName) {
        this.BuildingName = BuildingName;
    }

    public String getBuildingFloor() {
        return BuildingFloor;
    }

    public void setBuildingFloor(String BuildingFloor) {
        this.BuildingFloor = BuildingFloor;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    public String getDispenserID() {
        return DispenserID;
    }

    public void setDispenserID(String DispenserID) {
        this.DispenserID = DispenserID;
    }
}
