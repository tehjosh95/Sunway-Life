package com.example.android.myfyp;

public class ListOfClubs {

    private String name;
    private String image;
    private String contact;
    private String desc;
    private String myUid;

    private String userType;

    public ListOfClubs(){
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

    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

    public String getContact() { return contact; }

    public void setContact(String contact) { this.contact = contact; }

    public String getUserType() { return userType; }

    public void setUserType(String userType) { this.userType = userType; }

    public String getMyUid() { return myUid; }

    public void setMyUid(String myUid) { this.myUid = myUid; }

    public ListOfClubs(String name, String image, String contact, String desc, String userType, String myUid) {
        this.name = name;
        this.image = image;
        this.contact = contact;
        this.desc = desc;
        this.userType = userType;
        this.myUid = myUid;
    }
}
