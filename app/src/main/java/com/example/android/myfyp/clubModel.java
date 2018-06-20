package com.example.android.myfyp;

public class clubModel {

    private String item_name;
    private String item_desc;
    private String item_date;
    private String item_start_time;
    private String item_end_time;
    private String fee_for_member;
    private String fee_for_nonmember;
    private String venue;

    private Boolean isFinish;
    private String item_owner;
    private String imgName;
    private String ownerName;
    public String imageLink;
    private int item_position;

    private String parentkey;

//    public clubModel(String item_name, String item_desc, String item_date, String item_start_time, String item_end_time, String fee_for_member, String fee_for_nonmember, String venue, String item_owner, int item_position, String imageLink, String imgName, String parentkey) {
//        this.item_name = item_name;
//        this.item_desc = item_desc;
//        this.item_date = item_date;
//        this.item_start_time = item_start_time;
//        this.item_end_time = item_end_time;
//        this.fee_for_member = fee_for_member;
//        this.fee_for_nonmember = fee_for_nonmember;
//        this.venue = venue;
//        this.item_owner = item_owner;
//        this.item_position = item_position;
//        this.imageLink = imageLink;
//        this.imgName = imgName;
//        this.parentkey = parentkey;
//    }

    public clubModel(String item_name, String item_desc, String item_date, String item_start_time, String item_end_time, String fee_for_member, String fee_for_nonmember, String venue, String ownerName, String item_owner, int item_position, String imageLink, String imgName, String parentkey, Boolean isFinish) {
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.item_date = item_date;
        this.item_start_time = item_start_time;
        this.item_end_time = item_end_time;
        this.fee_for_member = fee_for_member;
        this.fee_for_nonmember = fee_for_nonmember;
        this.venue = venue;
        this.ownerName = ownerName;
        this.item_owner = item_owner;
        this.item_position = item_position;
        this.imageLink = imageLink;
        this.imgName = imgName;
        this.parentkey = parentkey;
        this.isFinish = isFinish;
    }

    public clubModel() {
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_owner() {
        return item_owner;
    }

    public void setItem_owner(String item_owner) {
        this.item_owner = item_owner;
    }

    public int getItem_position() {
        return item_position;
    }

    public String getOwnerName() { return ownerName; }

    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getParentkey() {
        return parentkey;
    }

    public void setParentkey(String parentkey) {
        this.parentkey = parentkey;
    }

    public String getItem_desc() { return item_desc; }

    public void setItem_desc(String item_desc) { this.item_desc = item_desc; }

    public String getItem_date() { return item_date; }

    public void setItem_date(String item_date) { this.item_date = item_date; }

    public String getItem_start_time() { return item_start_time; }

    public void setItem_start_time(String item_start_time) { this.item_start_time = item_start_time; }

    public String getItem_end_time() { return item_end_time; }

    public void setItem_end_time(String item_end_time) { this.item_end_time = item_end_time; }

    public String getFee_for_member() { return fee_for_member; }

    public void setFee_for_member(String fee_for_member) { this.fee_for_member = fee_for_member; }

    public String getFee_for_nonmember() { return fee_for_nonmember; }

    public void setFee_for_nonmember(String fee_for_nonmember) { this.fee_for_nonmember = fee_for_nonmember; }

    public String getVenue() { return venue; }

    public void setVenue(String venue) { this.venue = venue; }

    public Boolean getFinish() { return isFinish; }

    public void setFinish(Boolean finish) { isFinish = finish; }
}
