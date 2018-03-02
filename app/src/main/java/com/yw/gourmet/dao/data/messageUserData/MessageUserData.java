package com.yw.gourmet.dao.data.messageUserData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * auth: lewis-v
 * time: 2018/3/2.
 */
@Entity
public class MessageUserData {
    @Id(autoincrement = true)
    private Long _id;//数据库id
    private String user_id;//用户id
    private String nickname;//发送者昵称
    private String img_header;//发送者头像

    @Generated(hash = 1187361037)
    public MessageUserData(Long _id, String user_id, String nickname,
            String img_header) {
        this._id = _id;
        this.user_id = user_id;
        this.nickname = nickname;
        this.img_header = img_header;
    }

    @Generated(hash = 723898635)
    public MessageUserData() {
    }

    public Long get_id() {
        return _id;
    }

    public MessageUserData set_id(Long _id) {
        this._id = _id;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public MessageUserData setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public MessageUserData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getImg_header() {
        return img_header;
    }

    public MessageUserData setImg_header(String img_header) {
        this.img_header = img_header;
        return this;
    }

    @Override
    public String toString() {
        return "MessageUserData{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", img_header='" + img_header + '\'' +
                '}';
    }
}
