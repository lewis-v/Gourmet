package com.yw.gourmet.data;

/**
 * auth: lewis-v
 * time: 2018/1/16.
 */

public class RaidersDetailData<T,V> {
    private String id;
    private String title;//标题
    private int status;//私有状态
    private T raiders_content;//攻略内容
    private V raiders_type;//攻略类型
    private String cover;//攻略封面
    private String introduction;//简介
    private String put_time;//发布时间
    private String create_time;//创建时间
    private int type;//分享类型
    private String nickname;//分享者姓名
    private String img_header;//分享者头像
    private int comment_num;//评论数量
    private int good_num;//点赞数量
    private int bad_num;//踩数量
    private String is_collection;//是否收藏,长度大于0则收藏
    private String good_act;//点赞情况,1/0,空则没有
    private String is_comment;//是否评论,长度大于0则评论

    public String getId() {
        return id;
    }

    public RaidersDetailData setId(String id) {
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

    public String getPut_time() {
        return put_time;
    }

    public RaidersDetailData setPut_time(String put_time) {
        this.put_time = put_time;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public RaidersDetailData setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }

    public int getType() {
        return type;
    }

    public RaidersDetailData setType(int type) {
        this.type = type;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public RaidersDetailData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public RaidersDetailData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    public int getComment_num() {
        return comment_num;
    }

    public RaidersDetailData setComment_num(int comment_num) {
        this.comment_num = comment_num;
        return this;
    }

    public int getGood_num() {
        return good_num;
    }

    public RaidersDetailData setGood_num(int good_num) {
        this.good_num = good_num;
        return this;
    }

    public int getBad_num() {
        return bad_num;
    }

    public RaidersDetailData setBad_num(int bad_num) {
        this.bad_num = bad_num;
        return this;
    }

    public String getIs_collection() {
        return is_collection;
    }

    public RaidersDetailData setIs_collection(String is_collection) {
        this.is_collection = is_collection;
        return this;
    }

    public String getGood_act() {
        return good_act;
    }

    public RaidersDetailData setGood_act(String good_act) {
        this.good_act = good_act;
        return this;
    }

    public String getIs_comment() {
        return is_comment;
    }

    public RaidersDetailData setIs_comment(String is_comment) {
        this.is_comment = is_comment;
        return this;
    }

    @Override
    public String toString() {
        return "RaidersDetailData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", raiders_content=" + raiders_content +
                ", raiders_type=" + raiders_type +
                ", cover='" + cover + '\'' +
                ", introduction='" + introduction + '\'' +
                ", put_time='" + put_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", type=" + type +
                ", nickname='" + nickname + '\'' +
                ", img_header='" + img_header + '\'' +
                ", comment_num=" + comment_num +
                ", good_num=" + good_num +
                ", bad_num=" + bad_num +
                ", is_collection='" + is_collection + '\'' +
                ", good_act='" + good_act + '\'' +
                ", is_comment='" + is_comment + '\'' +
                '}';
    }
}
