package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/14.
 */

public class UserData {
    private String id;
    private String sex;
    private String like;
    private String address;
    private String nickname;
    private String introduction;
    private String token;
    private String img_header;
    private String personal_back;

    public String getId() {
        return id;
    }

    public UserData setId(String id) {
        this.id = id;
        return this;
    }

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

    public String getNickname() {
        return nickname;
    }

    public UserData setNickname(String nickname) {
        this.nickname = nickname;
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
                "id='" + id + '\'' +
                ", sex='" + sex + '\'' +
                ", like='" + like + '\'' +
                ", address='" + address + '\'' +
                ", nickname='" + nickname + '\'' +
                ", introduction='" + introduction + '\'' +
                ", token='" + token + '\'' +
                ", img_header='" + img_header + '\'' +
                ", personal_back='" + personal_back + '\'' +
                '}';
    }
}
