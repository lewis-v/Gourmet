package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/1/14.
 */

public class InitData {
    private int area_version;
    private String flash_cover;
    private long time;
    private String android_version;
    private String update_path;
    private String update_content;
    private String update_path_reserve;

    public int getArea_version() {
        return area_version;
    }

    public InitData setArea_version(int area_version) {
        this.area_version = area_version;
        return this;
    }

    public String getFlash_cover() {
        return flash_cover;
    }

    public InitData setFlash_cover(String flash_cover) {
        this.flash_cover = flash_cover;
        return this;
    }

    public long getTime() {
        return time;
    }

    public InitData setTime(long time) {
        this.time = time;
        return this;
    }

    public String getAndroid_version() {
        return android_version;
    }

    public InitData setAndroid_version(String android_version) {
        this.android_version = android_version;
        return this;
    }

    public String getUpdatePath() {
        return update_path;
    }

    public InitData setUpdatePath(String updatePath) {
        this.update_path = updatePath;
        return this;
    }

    public String getUpdate_content() {
        return update_content;
    }

    public InitData setUpdate_content(String update_content) {
        this.update_content = update_content;
        return this;
    }

    public String getUpdate_path() {
        return update_path;
    }

    public InitData setUpdate_path(String update_path) {
        this.update_path = update_path;
        return this;
    }

    public String getUpdate_path_reserve() {
        return update_path_reserve;
    }

    public InitData setUpdate_path_reserve(String update_path_reserve) {
        this.update_path_reserve = update_path_reserve;
        return this;
    }

    @Override
    public String toString() {
        return "InitData{" +
                "area_version=" + area_version +
                ", flash_cover='" + flash_cover + '\'' +
                ", time=" + time +
                ", android_version='" + android_version + '\'' +
                ", update_path='" + update_path + '\'' +
                ", update_content='" + update_content + '\'' +
                ", update_path_reserve='" + update_path_reserve + '\'' +
                '}';
    }
}
