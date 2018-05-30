package com.example.android.myfyp;

public class join_list {

    private String status;

    private String clubname;

    private String myname;

    public join_list(){

    }

    public join_list(String status, String clubname, String myname){
        this.clubname = clubname;
        this.myname = myname;
        this.status = status;
    }

    public String getMyname() { return myname; }

    public void setMyname(String myname) { this.myname = myname; }

    public String getClubname() { return clubname; }

    public void setClubname(String clubname) { this.clubname = clubname; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
