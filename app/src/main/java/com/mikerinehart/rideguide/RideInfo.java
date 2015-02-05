package com.mikerinehart.rideguide;

import java.util.Date;

/**
 * Created by Mike on 2/3/2015.
 */
public class RideInfo {
    protected int id;
    protected int user_id;
    protected int seats;
    protected Date start;
    protected Date end;
    protected User user;

    public int getRideId() {
        return id;
    }

    public void setRideId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
