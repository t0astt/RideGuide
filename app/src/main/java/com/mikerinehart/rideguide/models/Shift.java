package com.mikerinehart.rideguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Mike on 2/15/2015.
 */

public class Shift implements Parcelable {
    public static final Parcelable.Creator<Shift> CREATOR = new Parcelable.Creator<Shift>() {
        public Shift createFromParcel(Parcel in) {
            return new Shift(in);
        }

        public Shift[] newArray(int size) {
            return new Shift[size];
        }
    };

    protected int id;
    protected int user_id;
    protected int seats;
    // Dates get passed in/out as long instead of Date!!!
    protected Date start;
    protected Date end;
    public User user;
    public Reservation[] reservations;

    public Shift() {
    }

    public Shift(int id, int user_id, int seats, Date start, Date end, User user, Reservation[] reservations) {
        this.id = id;
        this.user_id = user_id;
        this.seats = seats;
        this.start = start;
        this.end = end;
        this.user = user;
        this.reservations = reservations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
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

    public Reservation[] getReservations() {
        return reservations;
    }

    public void setReservations(Reservation[] reservations) {
        this.reservations = reservations;
    }

    public int getReservationCount() {
        return reservations.length;
    }

    Shift(Parcel in) {
        this.id = in.readInt();
        this.user_id = in.readInt();
        this.seats = in.readInt();
        this.start = new Date(in.readLong());
        this.end = new Date(in.readLong());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(user_id);
        dest.writeInt(seats);
        dest.writeLong(start.getTime());
        dest.writeLong(end.getTime());
    }

    public int describeContents() {
        return 0;
    }

}
