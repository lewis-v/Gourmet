package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/17.
 */

public class ShareListData<T> {
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
    private T img;//照片数组
    private int type;//分享类型
    private String nickname;//分享者姓名
    private String img_header;//分享者头像

    public String getId() {
        return id;
    }

    public ShareListData setId(String id) {
        this.id = id;
        return this;
    }

    public String getPut_time() {
        return put_time;
    }

    public ShareListData setPut_time(String put_time) {
        this.put_time = put_time;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public ShareListData setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ShareListData setTime(String time) {
        this.time = time;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ShareListData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ShareListData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ShareListData setContent(String content) {
        this.content = content;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ShareListData setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getLat() {
        return lat;
    }

    public ShareListData setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public String getLng() {
        return lng;
    }

    public ShareListData setLng(String lng) {
        this.lng = lng;
        return this;
    }

    public T getImg() {
        return img;
    }

    public ShareListData setImg(T img) {
        this.img = img;
        return this;
    }

    public int getType() {
        return type;
    }

    public ShareListData setType(int type) {
        this.type = type;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public ShareListData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public ShareListData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    @Override
    public String toString() {
        return "ShareListData{" +
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
                ", img=" + img +
                ", type=" + type +
                ", nickname='" + nickname + '\'' +
                ", img_header='" + img_header + '\'' +
                '}';
    }
}
