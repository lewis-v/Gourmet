package com.yw.gourmet.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by LYW on 2017/11/26.
 */
@Entity
public class MessageListData {
    //消息类型
    public final static int TEXT = 0;//文本
    public final static int VOICE = 1;//语音
    public final static int IMG = 2;//图片
    //消息发送状态
    public final static int SENDING = 3;//发送中
    public final static int SEND_SUCCESS = 4;//发送成功
    public final static int SEND_FAIL = 5;//发送失败

    @Id(autoincrement = true)
    private Long _id;//数据库id
    private int cli_id;//客户端id
    private String user_id;//存储的用户id
    private String id;
    private String put_id;
    private String get_id;
    private String nickname;
    private String content;
    private String put_time;
    private String img_header;
    private int type;//消息类型,文字,图片,其他特殊分享类型
    private String title;//特殊分享类型标题
    private String cover;//特殊分享类型封面
    private String img;
    private int sendStatus = SEND_SUCCESS;//消息发送状态,默认为成功
    private int is_read;//是否已读
    private int un_read_num;//未读数量

    public MessageListData() {
    }


    @Generated(hash = 1778411488)
    public MessageListData(Long _id, int cli_id, String user_id, String id,
            String put_id, String get_id, String nickname, String content,
            String put_time, String img_header, int type, String title,
            String cover, String img, int sendStatus, int is_read,
            int un_read_num) {
        this._id = _id;
        this.cli_id = cli_id;
        this.user_id = user_id;
        this.id = id;
        this.put_id = put_id;
        this.get_id = get_id;
        this.nickname = nickname;
        this.content = content;
        this.put_time = put_time;
        this.img_header = img_header;
        this.type = type;
        this.title = title;
        this.cover = cover;
        this.img = img;
        this.sendStatus = sendStatus;
        this.is_read = is_read;
        this.un_read_num = un_read_num;
    }


    public int getCli_id() {
        return cli_id;
    }

    public MessageListData setCli_id(int cli_id) {
        this.cli_id = cli_id;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public MessageListData setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
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

    public String getPut_id() {
        return put_id;
    }

    public MessageListData setPut_id(String put_id) {
        this.put_id = put_id;
        return this;
    }

    public String getGet_id() {
        return get_id;
    }

    public MessageListData setGet_id(String get_id) {
        this.get_id = get_id;
        return this;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public MessageListData setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public int getIs_read() {
        return is_read;
    }

    public MessageListData setIs_read(int is_read) {
        this.is_read = is_read;
        return this;
    }

    public int getUn_read_num() {
        return un_read_num;
    }

    public MessageListData setUn_read_num(int un_read_num) {
        this.un_read_num = un_read_num;
        return this;
    }

    /**
     * 添加一条未读消息
     * @return
     */
    public MessageListData addUnReadNum(){
        un_read_num++;
        return this;
    }

    @Override
    public String toString() {
        return "MessageListData{" +
                "cli_id='" + cli_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", id='" + id + '\'' +
                ", put_id='" + put_id + '\'' +
                ", get_id='" + get_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", put_time='" + put_time + '\'' +
                ", img_header='" + img_header + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", img='" + img + '\'' +
                ", sendStatus=" + sendStatus +
                ", is_read=" + is_read +
                ", un_read_num=" + un_read_num +
                '}';
    }


    public Long get_id() {
        return this._id;
    }


    public void set_id(Long _id) {
        this._id = _id;
    }
}
