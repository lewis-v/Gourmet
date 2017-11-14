package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/14.
 */

public class UserData {
    private String sex;
    private String like;
    private String address;
    private String nike_name;
    private String introduction;
    private String token;

    public String getSex() {
        return sex;
    }

    public UserData setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getLike() {
        return like;
    }

    public UserData setLike(String like) {
        this.like = like;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getNike_name() {
        return nike_name;
    }

    public UserData setNike_name(String nike_name) {
        this.nike_name = nike_name;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public UserData setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public String getToken() {
        return token;
    }

    public UserData setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "sex='" + sex + '\'' +
                ", like='" + like + '\'' +
                ", address='" + address + '\'' +
                ", nike_name='" + nike_name + '\'' +
                ", introduction='" + introduction + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
