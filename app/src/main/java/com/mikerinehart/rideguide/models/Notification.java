package com.mikerinehart.rideguide.models;

/**
 * Created by Mike on 4/6/2015.
 */
public class Notification {

    protected String message;

    public Notification(String m) {
        this.message = m;
    }

    public String getMessage() {
        return this.message;
    }
}
