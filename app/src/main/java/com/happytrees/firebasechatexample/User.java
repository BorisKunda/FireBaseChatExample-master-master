package com.happytrees.firebasechatexample;

public class User {

    public String name;
    public String image;
    public String status;
    public String thumb_image;

    public User(String name, String image, String status, String thumb_image) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
    }


    /*
    Default constructor required for calls to
    DataSnapshot.getValue(User.class)
     */
    public User() {
    }


}

