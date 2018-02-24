package com.yw.gourmet.rxbus;

import com.yw.gourmet.Constant;

/**
 * Created by yw on 16/7/20.
 */
public class EventSticky {

    public String event;
    public int type;
    public String id;
    public int good;
    public int bad;
    public int comment;
    public int act = Constant.CommentType.ALL;//触发的行为

    public EventSticky(String event) {
        this.event = event;
    }

    public EventSticky(String event, int type, String id, int good, int bad, int comment) {
        this.event = event;
        this.type = type;
        this.id = id;
        this.good = good;
        this.bad = bad;
        this.comment = comment;
    }

    public EventSticky(String event, int type, String id, int comment, int act) {
        this.event = event;
        this.type = type;
        this.id = id;
        this.comment = comment;
        this.act = act;
    }

    public String getEvent() {
        return event;
    }

    public EventSticky setEvent(String event) {
        this.event = event;
        return this;
    }

    public int getType() {
        return type;
    }

    public EventSticky setType(int type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public EventSticky setId(String id) {
        this.id = id;
        return this;
    }

    public int getGood() {
        return good;
    }

    public EventSticky setGood(int good) {
        this.good = good;
        return this;
    }

    public int getBad() {
        return bad;
    }

    public EventSticky setBad(int bad) {
        this.bad = bad;
        return this;
    }

    public int getComment() {
        return comment;
    }

    public EventSticky setComment(int comment) {
        this.comment = comment;
        return this;
    }

    public int getAct() {
        return act;
    }

    public EventSticky setAct(int act) {
        this.act = act;
        return this;
    }

    @Override
    public String toString() {
        return "EventSticky{" +
                "event='" + event + '\'' +
                ", type=" + type +
                ", id=" + id +
                '}';
    }
}
