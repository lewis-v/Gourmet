package com.yw.gourmet.dao.data.saveData;

import com.yw.gourmet.dao.data.ListMenuPracticeConverter;
import com.yw.gourmet.dao.data.ListRaidersListConverter;
import com.yw.gourmet.dao.data.StringConverter;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.data.RaidersListData;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * auth: lewis-v
 * time: 2018/1/22.
 */
@Entity
public class SaveData {
    @Id(autoincrement = true)
    private long _id;
    private int type;//分享类型
    private long change_time;//最后修改时间
    private int status;//发布的可见状态
    private int difficult_level;//困难程度
    private double lat;//坐标
    private double lng;//坐标
    private String user_id;//用户id
    private String address;//地址
    private String content;//内容
    private String time;//记录时间
    private String title;//标题
    private String cover;//封面
    private String play_time;//耗时
    private String introduction;//简介
    private String tip;//小贴士
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> img;//照片数组
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> ingredient;//用料
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> raiders_type;//攻略类型
    @Convert(columnType = String.class, converter = ListMenuPracticeConverter.class)
    private List<MenuPracticeData<List<String>>> practice;//步骤
    @Convert(columnType = String.class, converter = ListRaidersListConverter.class)
    private List<RaidersListData<List<String>>> raiders_content;//攻略内容


    public SaveData() {
    }



    @Generated(hash = 1162626406)
    public SaveData(long _id, int type, long change_time, int status, int difficult_level,
            double lat, double lng, String user_id, String address, String content,
            String time, String title, String cover, String play_time, String introduction,
            String tip, List<String> img, List<String> ingredient, List<String> raiders_type,
            List<MenuPracticeData<List<String>>> practice,
            List<RaidersListData<List<String>>> raiders_content) {
        this._id = _id;
        this.type = type;
        this.change_time = change_time;
        this.status = status;
        this.difficult_level = difficult_level;
        this.lat = lat;
        this.lng = lng;
        this.user_id = user_id;
        this.address = address;
        this.content = content;
        this.time = time;
        this.title = title;
        this.cover = cover;
        this.play_time = play_time;
        this.introduction = introduction;
        this.tip = tip;
        this.img = img;
        this.ingredient = ingredient;
        this.raiders_type = raiders_type;
        this.practice = practice;
        this.raiders_content = raiders_content;
    }

    

    public long get_id() {
        return _id;
    }

    public SaveData set_id(long _id) {
        this._id = _id;
        return this;
    }

    public int getType() {
        return type;
    }

    public SaveData setType(int type) {
        this.type = type;
        return this;
    }

    public long getChange_time() {
        return change_time;
    }

    public SaveData setChange_time(long change_time) {
        this.change_time = change_time;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public SaveData setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public SaveData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SaveData setContent(String content) {
        this.content = content;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public SaveData setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public SaveData setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public String getTime() {
        return time;
    }

    public SaveData setTime(String time) {
        this.time = time;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SaveData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public SaveData setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public String getPlay_time() {
        return play_time;
    }

    public SaveData setPlay_time(String play_time) {
        this.play_time = play_time;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public SaveData setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public String getTip() {
        return tip;
    }

    public SaveData setTip(String tip) {
        this.tip = tip;
        return this;
    }

    public int getDifficult_level() {
        return difficult_level;
    }

    public SaveData setDifficult_level(int difficult_level) {
        this.difficult_level = difficult_level;
        return this;
    }

    public List<String> getImg() {
        return img;
    }

    public SaveData setImg(List<String> img) {
        this.img = img;
        return this;
    }

    public List<String> getIngredient() {
        return ingredient;
    }

    public SaveData setIngredient(List<String> ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public List<String> getRaiders_type() {
        return raiders_type;
    }

    public SaveData setRaiders_type(List<String> raiders_type) {
        this.raiders_type = raiders_type;
        return this;
    }

    public List<MenuPracticeData<List<String>>> getPractice() {
        return practice;
    }

    public SaveData setPractice(List<MenuPracticeData<List<String>>> practice) {
        this.practice = practice;
        return this;
    }

    public List<RaidersListData<List<String>>> getRaiders_content() {
        return raiders_content;
    }

    public SaveData setRaiders_content(List<RaidersListData<List<String>>> raiders_content) {
        this.raiders_content = raiders_content;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public SaveData setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "_id=" + _id +
                ", type=" + type +
                ", change_time=" + change_time +
                ", status=" + status +
                ", difficult_level=" + difficult_level +
                ", user_id='" + user_id + '\'' +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", play_time='" + play_time + '\'' +
                ", introduction='" + introduction + '\'' +
                ", tip='" + tip + '\'' +
                ", img=" + img +
                ", ingredient=" + ingredient +
                ", raiders_type=" + raiders_type +
                ", practice=" + practice +
                ", raiders_content=" + raiders_content +
                '}';
    }
}
