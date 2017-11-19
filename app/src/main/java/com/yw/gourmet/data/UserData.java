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
    private String img_header;
    private String personal_back;

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

    public String getImg_header() {
        return img_header;
    }

    public UserData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    public String getPersonal_back() {
        return personal_back;
    }

    public UserData setPersonal_back(String personal_back) {
        this.personal_back = personal_back;
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
                ", img_header='" + img_header + '\'' +
                ", personal_back='" + personal_back + '\'' +
                '}';
    }
}
