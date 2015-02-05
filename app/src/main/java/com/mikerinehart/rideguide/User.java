package com.mikerinehart.rideguide;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mike on 2/4/2015.
 */
public class User implements Parcelable {
    protected int id;
    protected String fb_uid;
    protected String email;
    protected String first_name;
    protected String last_name;

    User(Parcel in) {
        this.id = in.readInt();
        this.fb_uid = in.readString();
        this.email = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fb_uid);
        dest.writeString(email);
        dest.writeString(first_name);
        dest.writeString(last_name);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private String getFbUid()
    {
        return fb_uid;
    }

    private String getEmail()
    {
        return email;
    }

    private String getFirstName()
    {
        return first_name;
    }

    private String getLastName()
    {
        return last_name;
    }

    public String getFullName()
    {
        return first_name + " " + last_name;
    }
}
