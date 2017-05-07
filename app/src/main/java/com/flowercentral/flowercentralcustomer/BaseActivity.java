package com.flowercentral.flowercentralcustomer;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Ashish Upadhyay on 3/26/17.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context mContext;


    @Override
    public void onCreate (Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate (savedInstanceState, persistentState);
        mContext = this;
    }


}
