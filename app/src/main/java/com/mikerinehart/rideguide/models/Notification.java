package com.mikerinehart.rideguide.models;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Mike on 4/6/2015.
 */
public class Notification implements Comparator<Notification> {

    protected String message;
//    protected Date timestamp;

    public Notification(String m) {
        this.message = m;
    }

    public String getMessage() {
        return this.message;
    }

//    public Date getTimestamp() {
//        return this.timestamp;
//    }

    @Override
    public int compare(Notification n1, Notification n2) {
        return 1;
    }
}
