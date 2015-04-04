package com.mikerinehart.rideguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Mike on 2/4/2015.
 */

public class User implements Parcelable {
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
    protected int id;
    protected String fb_uid;
    protected String email;
    protected String first_name;
    protected String last_name;
    protected String phone;
    protected boolean email_confirmed;
    protected boolean phone_confirmed;
    protected List<Review> positive_reviews;
    protected List<Review> negative_reviews;

    public User(int id, String fb_uid, String email, String first_name, String last_name, String phone, boolean email_confirmed, boolean phone_confirmed, List<Review> positive_reviews, List<Review> negative_reviews) {
        this.id = id;
        this.fb_uid = fb_uid;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.email_confirmed = email_confirmed;
        this.phone_confirmed = phone_confirmed;
        this.positive_reviews = positive_reviews;
        this.negative_reviews = negative_reviews;
    }

    User(Parcel in) {
        this.id = in.readInt();
        this.fb_uid = in.readString();
        this.email = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.phone = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fb_uid);
        dest.writeString(email);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(phone);
    }

    public int describeContents() {
        return 0;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFbUid() {
        return fb_uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getFullName() {
        return first_name + " " + last_name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean getEmailConfirmationStatus() {
        return email_confirmed;
    }

    public boolean getPhoneConfirmationStatus() {
        return phone_confirmed;
    }

    public List<Review> getPositive_reviews() {
        return positive_reviews;
    }

    public void setPositive_reviews(List<Review> positive_reviews) {
        this.positive_reviews = positive_reviews;
    }

    public List<Review> getNegative_reviews() {
        return negative_reviews;
    }

    public void setNegative_reviews(List<Review> negative_reviews) {
        this.negative_reviews = negative_reviews;
    }
}
