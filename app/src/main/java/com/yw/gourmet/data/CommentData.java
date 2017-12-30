package com.yw.gourmet.data;

/**
 * Created by Lewis-v on 2017/12/30.
 */

public class CommentData {
    private String id;
    private String act_id;
    private String user_id;
    private String type;
    private String content;
    private String nickname;
    private String img_header;
    private String create_time;

    public String getId() {
        return id;
    }

    public CommentData setId(String id) {
        this.id = id;
        return this;
    }

    public String getAct_id() {
        return act_id;
    }

    public CommentData setAct_id(String act_id) {
        this.act_id = act_id;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public CommentData setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getType() {
        return type;
    }

    public CommentData setType(String type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CommentData setContent(String content) {
        this.content = content;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public CommentData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public CommentData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public CommentData setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }

    @Override
    public String toString() {
        return "CommentData{" +
                "id='" + id + '\'' +
                ", act_id='" + act_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", nickname='" + nickname + '\'' +
                ", img_header='" + img_header + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
