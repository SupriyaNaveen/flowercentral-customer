package com.flowercentral.flowercentralcustomer.profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Supriya on 21-06-2017.
 */

public class ProfileData implements Parcelable {

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("country")
    private String country;

    @SerializedName("pin")
    private String pin;

    @SerializedName("gender")
    private UserPreference.GENDER gender;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    protected ProfileData(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        pin = in.readString();
        gender = UserPreference.GENDER.valueOf(in.readString());
        email = in.readString();
        phone = in.readString();
    }

    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
        @Override
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(pin);
        dest.writeString(gender.name());
        dest.writeString(email);
        dest.writeString(phone);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPin() {
        return pin;
    }

    public UserPreference.GENDER getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
