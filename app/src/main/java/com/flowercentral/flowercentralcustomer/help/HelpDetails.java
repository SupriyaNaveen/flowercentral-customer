package com.flowercentral.flowercentralcustomer.help;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

class HelpDetails implements Parcelable{

    @SerializedName("question")
    private String question;

    @SerializedName("answer")
    private String answer;

    private HelpDetails(Parcel in) {
        question = in.readString();
        answer = in.readString();
    }

    public static final Creator<HelpDetails> CREATOR = new Creator<HelpDetails>() {
        @Override
        public HelpDetails createFromParcel(Parcel in) {
            return new HelpDetails(in);
        }

        @Override
        public HelpDetails[] newArray(int size) {
            return new HelpDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(answer);
    }

    String getQuestion() {
        return question;
    }

    String getAnswer() {
        return answer;
    }
}
