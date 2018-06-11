package com.example.android.myfyp;

public class clubModel {

    private String item_name;
    private String item_place;
    private String item_price;
    private String item_owner;

    private String imgName;

    private String ownerName;

    public String imageLink;
    private int item_position;

    private String parentkey;

    public clubModel(String item_name, String item_place, String item_price, String item_owner, int item_position, String imageLink, String imgName, String parentkey) {
        this.item_name = item_name;
        this.item_place = item_place;
        this.item_price = item_price;
        this.item_owner = item_owner;
        this.item_position = item_position;
        this.imageLink = imageLink;
        this.imgName = imgName;
        this.parentkey = parentkey;
    }

    public clubModel(String item_name, String item_place, String item_price, String ownerName, String item_owner, int item_position, String imageLink, String imgName, String parentkey) {
        this.item_name = item_name;
        this.item_place = item_place;
        this.item_price = item_price;
        this.ownerName = ownerName;
        this.item_owner = item_owner;
        this.item_position = item_position;
        this.imageLink = imageLink;
        this.imgName = imgName;
        this.parentkey = parentkey;
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

    public String getItem_place() {
        return item_place;
    }

    public void setItem_place(String item_place) {
        this.item_place = item_place;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
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
}
