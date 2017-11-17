package com.yw.gourmet.rxbus;

/**
 * Created by yw on 16/7/19.
 */
public class Event {
    public int event;

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Event{" +
                "event=" + event +
                '}';
    }

    public Event(int event) {
        this.event = event;
    }
}
