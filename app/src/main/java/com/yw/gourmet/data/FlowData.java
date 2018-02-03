package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/2/2.
 */

public class FlowData {
    private String img;//照片地址
    private String url;//跳转链接
    private String title;//标题

    public String getImg() {
        return img;
    }

    public FlowData setImg(String img) {
        this.img = img;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public FlowData setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public FlowData setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return "FlowData{" +
                "img='" + img + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
