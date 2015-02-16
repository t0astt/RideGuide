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

    public Shift(int id, int user_id, int seats, Date start, Date end) {
        this.id = id;
        this.user_id = user_id;
        this.seats = seats;
        this.start = start;
        this.end = end;
    }

    public Shift() {
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
