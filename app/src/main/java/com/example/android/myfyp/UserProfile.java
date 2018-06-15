package com.example.android.myfyp;

public class UserProfile {
    public String studentID;
    public String studentName;
    public String studentCourse;
    public String studentPhone;

    public String userType;

    public UserProfile() {
    }

    public UserProfile(String studentID, String studentName, String studentCourse, String studentPhone, String userType) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentCourse = studentCourse;
        this.studentPhone = studentPhone;
        this.userType = userType;
    }

    public String getStudentID() { return studentID; }

    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getStudentName() { return studentName; }

    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCourse() { return studentCourse; }

    public void setStudentCourse(String studentCourse) { this.studentCourse = studentCourse; }

    public String getStudentPhone() { return studentPhone; }

    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }

    public String getUserType() { return userType; }

    public void setUserType(String userType) { this.userType = userType; }
}
