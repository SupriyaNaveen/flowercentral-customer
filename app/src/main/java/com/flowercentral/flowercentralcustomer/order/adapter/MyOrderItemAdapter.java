package com.flowercentral.flowercentralcustomer.order.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.squareup.picasso.Picasso;



import java.util.List;

public class MyOrderItemAdapter extends RecyclerView.Adapter<MyOrderItemAdapter.ViewHolder> {

    private static final String TAG = MyOrderItemAdapter.class.getSimpleName ();

    private Context mContext;
    private final List<Product> mProducts;



    public MyOrderItemAdapter (Context _context, List<Product> _product) {
        mContext = _context;
        mProducts = _product;

    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.layout_item, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder (final ViewHolder _holder, int _position) {
        Product product = null;
        if(mProducts != null){
            product = mProducts.get (_position);
        }

        if (product.getImage () != null) {
            Picasso.with(mContext).load(product.getImage ()).into(_holder.imgItem);
        } else {
            //Default image
        }

        _holder.txtFlowerName.setText (product.getFlowerName ());
        _holder.txtDesc.setText (product.getDescription ());
        _holder.txtPrice.setText (String.valueOf (product.getPrice ()));

        if(!TextUtils.isEmpty (product.getUserMessage ())){
            _holder.txtUserMsg.setText (product.getUserMessage ());
        }

    }

    @Override
    public int getItemCount () {
        int size = 0;
        if(mProducts != null){
            size = mProducts.size ();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgItem;
        public TextView txtFlowerName;
        public TextView txtDesc;
        public TextView txtUserMsg;
        public TextView txtPrice;

        public ViewHolder (View view) {
            super (view);

            imgItem = (ImageView) view.findViewById (R.id.img_item);
            txtFlowerName = (TextView) view.findViewById (R.id.txt_item_title);
            txtDesc = (TextView) view.findViewById (R.id.txt_item_desc);
            txtUserMsg = (TextView) view.findViewById (R.id.txt_user_message);

        }

    }
}
