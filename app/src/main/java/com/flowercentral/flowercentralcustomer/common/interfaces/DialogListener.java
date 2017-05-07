package com.flowercentral.flowercentralcustomer.common.interfaces;

/**
 * Created by Ashish Upadhyay on 5/2/17.
 */

public interface DialogListener {
    public void onPositiveButtonClicked (int _reqFor);
    public void onNegativeButtonClicked (int _reqFor);
    public void onNeutralButtonClicked (int _reqFor);
}
