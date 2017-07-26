package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class FlowerDetails implements Parcelable {

    @SerializedName("name")
    private String flowerName;

    @SerializedName("quantity")
    private int flowerQuantity;

    private FlowerDetails(Parcel in) {
        flowerName = in.readString();
        flowerQuantity = in.readInt();
    }

    public static final Creator<FlowerDetails> CREATOR = new Creator<FlowerDetails>() {
        @Override
        public FlowerDetails createFromParcel(Parcel in) {
            return new FlowerDetails(in);
        }

        @Override
        public FlowerDetails[] newArray(int size) {
            return new FlowerDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flowerName);
        dest.writeInt(flowerQuantity);
    }

    public String getFlowerName() {
        return flowerName;
    }

    public int getFlowerQuantity() {
        return flowerQuantity;
    }
}
