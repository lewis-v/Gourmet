package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/26.
 */

public class MessageListData {
    //消息类型
    public final static int TEXT = 0;//文本
    public final static int VOICE = 1;//语音
    public final static int IMG = 2;//图片

    private String id;
    private String nickname;
    private String content;
    private String put_time;
    private String img_header;
    private int type;//消息类型,文字,图片,其他特殊分享类型
    private String title;//特殊分享类型标题
    private String cover;//特殊分享类型封面
    private String img;

    public MessageListData() {
    }

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

    public String getPut_time() {
        return put_time;
    }

    public MessageListData setPut_time(String put_time) {
        this.put_time = put_time;
        return this;
    }

    public int getType() {
        return type;
    }

    public MessageListData setType(int type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MessageListData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public MessageListData setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public String getImg() {
        return img;
    }

    public MessageListData setImg(String img) {
        this.img = img;
        return this;
    }

    @Override
    public String toString() {
        return "MessageListData{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", put_time='" + put_time + '\'' +
                ", img_header='" + img_header + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
