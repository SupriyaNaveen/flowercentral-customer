package com.flowercentral.flowercentralcustomer.common.interfaces;

/**
 * Created by Ashish Upadhyay on 5/21/17.
 */

public interface OnItemClickListener {
    void onItemClicked(String _type, int _position, Object _data);
    void onItemDeleted(int _position, Object _data);

}
