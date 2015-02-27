package com.mikerinehart.rideguide.models;

import java.util.Date;

/**
 * Created by Mike on 2/16/2015.
 */

// TODO: Implement parcelable on this
public class Reservation {

    protected int id;
    protected int user_id;
    protected int shift_id;
    protected int passengers;
    protected String origin;
    protected String destination;
    protected Date pickup_time;
    public User user;
    public Shift shift;

    public Reservation(int id, int user_id, int shift_id, int passengers, String origin, String destination, Date pickup_time, User user, Shift shift) {
        this.id = id;
        this.user_id = user_id;
        this.shift_id = shift_id;
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.pickup_time = pickup_time;
        this.user = user;
        this.shift = shift;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(int user_id) {
//        this.user_id = user_id;
//    }

    public int getShift_id() {
        return shift_id;
    }

    public void setShift_id(int shift_id) {
        this.shift_id = shift_id;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(Date pickup_time) {
        this.pickup_time = pickup_time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

}
