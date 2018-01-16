package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/1/14.
 */

public class InitData {
    private int area_version;
    private String flash_cover;
    private long time;

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

    @Override
    public String toString() {
        return "InitData{" +
                "area_version=" + area_version +
                ", flash_cover='" + flash_cover + '\'' +
                ", time=" + time +
                '}';
    }
}