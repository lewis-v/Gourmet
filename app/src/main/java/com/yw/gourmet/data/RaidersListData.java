package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/1/9.
 */

public class RaidersListData<T> {
    private String title;
    private String img_cover;
    private T type;
    private String address;
    private double lng;
    private double lat;

    public String getTitle() {
        return title;
    }

    public RaidersListData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getImg_cover() {
        return img_cover;
    }

    public RaidersListData setImg_cover(String img_cover) {
        this.img_cover = img_cover;
        return this;
    }

    public T getType() {
        return type;
    }

    public RaidersListData setType(T type) {
        this.type = type;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public RaidersListData setAddress(String address) {
        this.address = address;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public RaidersListData setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public RaidersListData setLat(double lat) {
        this.lat = lat;
        return this;
    }

    @Override
    public String toString() {
        return "RaidersListData{" +
                "title='" + title + '\'' +
                ", img_cover='" + img_cover + '\'' +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
