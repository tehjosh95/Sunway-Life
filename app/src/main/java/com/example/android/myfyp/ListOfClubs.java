package com.example.android.myfyp;

public class ListOfClubs {

    private String name;
    private String image;
    private String advisor;
    private String desc;
    private String myUid;

    private String email;

    private String userType;

    public ListOfClubs() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAdvisor() {
        return advisor;
    }

    public void setAdvisor(String advisor) {
        this.advisor = advisor;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public ListOfClubs(String name, String image, String advisor, String desc, String userType, String myUid, String email) {
        this.name = name;
        this.image = image;
        this.advisor = advisor;
        this.desc = desc;
        this.userType = userType;
        this.myUid = myUid;
        this.email = email;
    }
}
