package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/2/23.
 */

public class HotSearch {
    private String title;

    public String getTitle() {
        return title;
    }

    public HotSearch setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return "HotSearch{" +
                "title='" + title + '\'' +
                '}';
    }
}
