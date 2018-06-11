package com.example.android.myfyp;

public class event_list {

    private String eventOwner;
    private String eventName;
    private String studentName;
    private String status;
    private String ownerName;

    public event_list() {
    }

    public event_list(String eventOwner, String eventName, String studentName, String status, String ownerName){
        this.eventOwner = eventOwner;
        this.eventName = eventName;
        this.studentName = studentName;
        this.status = status;
        this.ownerName = ownerName;
    }
    public String getEventOwner() { return eventOwner; }

    public void setEventOwner(String eventOwner) { this.eventOwner = eventOwner; }

    public String getEventName() { return eventName; }

    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getStudentName() { return studentName; }

    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }

    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
