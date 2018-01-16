package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/1/16.
 */

public class RaidersDetailData<T,V> {
    private int id;
    private String title;//标题
    private int status;//私有状态
    private T raiders_content;//攻略内容
    private V raiders_type;//攻略类型
    private String cover;//攻略封面
    private String introduction;//简介

    public int getId() {
        return id;
    }

    public RaidersDetailData setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public RaidersDetailData setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public RaidersDetailData setStatus(int status) {
        this.status = status;
        return this;
    }

    public T getRaiders_content() {
        return raiders_content;
    }

    public RaidersDetailData setRaiders_content(T raiders_content) {
        this.raiders_content = raiders_content;
        return this;
    }

    public V getRaiders_type() {
        return raiders_type;
    }

    public RaidersDetailData setRaiders_type(V raiders_type) {
        this.raiders_type = raiders_type;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public RaidersDetailData setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public RaidersDetailData setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    @Override
    public String toString() {
        return "RaidersDetailData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", raiders_content=" + raiders_content +
                ", raiders_type=" + raiders_type +
                ", cover='" + cover + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
