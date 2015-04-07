package com.mikerinehart.rideguide.models;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Mike on 4/6/2015.
 */
public class Notification implements Comparator<Notification> {

    protected String message;
    protected Date date;
    protected String fbUid;
    protected String type;
    protected Boolean seen;

    public Notification(String m) {
        this.message = m;
    }

    public Notification(String message, Date date, String fbUid, String type) {
        this.message = message;
        this.date = date;
        this.fbUid = fbUid;
        this.type = type;
    }

    public Notification(String message, Date date, String fbUid, String type, boolean seen) {
        this.message = message;
        this.date = date;
        this.fbUid = fbUid;
        this.type = type;
        this.seen = seen;
    }

    @Override
    public int compare(Notification n1, Notification n2) {
        return n1.getDate().compareTo(n2.getDate());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFbUid() {
        return fbUid;
    }

    public void setFbUid(String fbUid) {
        this.fbUid = fbUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isSeen() {
        if (seen == null) {
            return null;
        } else {
            return seen;
        }
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
