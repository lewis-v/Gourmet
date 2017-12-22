package com.yw.gourmet.data;

/**
 * Created by Lewis-v on 2017/12/22.
 */

public class MenuDetailData<T> {
    private String id;
    private String put_time;//发布时间
    private String create_time;//创建时间
    private String time;//记录时间
    private String title;//标题
    private String address;//地址
    private String content;//内容
    private String status;//发布的可见状态
    private String lat;//坐标
    private String lng;//坐标
    private int type;//分享类型
    private String nickname;//分享者姓名
    private String img_header;//分享者头像
    private int comment_num;//评论数量
    private int good_num;//点赞数量
    private int bad_num;//踩数量
    private String cover;//封面
    private T practice;//步骤

    public String getId() {
        return id;
    }

    public MenuDetailData setId(String id) {
        this.id = id;
        return this;
    }

    public String getPut_time() {
        return put_time;
    }

    public MenuDetailData setPut_time(String put_time) {
        this.put_time = put_time;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public MenuDetailData setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }

    public String getTime() {
        return time;
    }

    public MenuDetailData setTime(String time) {
        this.time = time;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MenuDetailData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MenuDetailData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MenuDetailData setContent(String content) {
        this.content = content;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public MenuDetailData setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getLat() {
        return lat;
    }

    public MenuDetailData setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public String getLng() {
        return lng;
    }

    public MenuDetailData setLng(String lng) {
        this.lng = lng;
        return this;
    }

    public int getType() {
        return type;
    }

    public MenuDetailData setType(int type) {
        this.type = type;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public MenuDetailData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public MenuDetailData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    public int getComment_num() {
        return comment_num;
    }

    public MenuDetailData setComment_num(int comment_num) {
        this.comment_num = comment_num;
        return this;
    }

    public int getGood_num() {
        return good_num;
    }

    public MenuDetailData setGood_num(int good_num) {
        this.good_num = good_num;
        return this;
    }

    public int getBad_num() {
        return bad_num;
    }

    public MenuDetailData setBad_num(int bad_num) {
        this.bad_num = bad_num;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public MenuDetailData setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public T getPractice() {
        return practice;
    }

    public MenuDetailData setPractice(T practice) {
        this.practice = practice;
        return this;
    }

    @Override
    public String toString() {
        return "MenuDetailData{" +
                "id='" + id + '\'' +
                ", put_time='" + put_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", type=" + type +
                ", nickname='" + nickname + '\'' +
                ", img_header='" + img_header + '\'' +
                ", comment_num=" + comment_num +
                ", good_num=" + good_num +
                ", bad_num=" + bad_num +
                ", cover='" + cover + '\'' +
                ", practice=" + practice +
                '}';
    }
}
