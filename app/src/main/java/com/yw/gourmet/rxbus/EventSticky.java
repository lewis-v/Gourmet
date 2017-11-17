package com.yw.gourmet.rxbus;

/**
 * Created by yw on 16/7/20.
 */
public class EventSticky {
    public String event;

    public EventSticky(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EventSticky{" +
                "event='" + event + '\'' +
                '}';
    }
}
