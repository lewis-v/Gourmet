package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/26.
 */

public class MessageListData {
    private String id;
    private String nickname;
    private String content;
    private String put_time;
    private String img_header;

    public String getId() {
        return id;
    }

    public MessageListData setId(String id) {
        this.id = id;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public MessageListData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageListData setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTime() {
        return put_time;
    }

    public MessageListData setTime(String time) {
        this.put_time = time;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public MessageListData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    @Override
    public String toString() {
        return "MessageListData{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", time='" + put_time + '\'' +
                ", img_header='" + img_header + '\'' +
                '}';
    }
}
