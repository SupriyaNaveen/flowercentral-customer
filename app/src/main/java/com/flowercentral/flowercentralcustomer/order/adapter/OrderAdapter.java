package com.flowercentral.flowercentralcustomer.order.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.adapter.CartItemAdapter;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Order;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish Upadhyay on 6/26/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = OrderAdapter.class.getSimpleName ();

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;

    private Context mContext;
    private ArrayList<Order> mMyOrders;
    private OnItemClickListener mItemClickListener;

    public OrderAdapter(Context _context, ArrayList<Order> _orders, OnItemClickListener _itemClickListener){
        mContext = _context;
        mMyOrders = _orders;
        mItemClickListener = _itemClickListener;
    }

    @Override
    public int getItemCount () {
        int size = 1;
        if(mMyOrders != null && mMyOrders.size ()>0){
            size = mMyOrders.size ();
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        int viewType;
        if (mMyOrders != null && mMyOrders.size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
        } else {
            viewType = VIEW_TYPE_EMPTY_LIST;
        }
        return viewType;
    }

    public Order getItemAtPosition(int _position) {

        Order order = null;
        if (mMyOrders != null && _position > 0) {
            if (_position < mMyOrders.size()) {
                order = mMyOrders.get(_position);
            }
        }
        return order;
    }

    public void clear() {
        if (mMyOrders != null && mMyOrders.size() > 0) {
            mMyOrders.clear();
            this.notifyDataSetChanged();
        }
    }

    public void addAll(List<Order> _list) {
        if (mMyOrders == null) {
            mMyOrders = new ArrayList<Order>();
        }
        mMyOrders.clear();
        mMyOrders.addAll(_list);
        this.notifyDataSetChanged();
    }

    public void addAt(Order item, int _position) {
        if (mMyOrders == null) {
            mMyOrders = new ArrayList<Order>();
        }
        if (item != null && _position >= 0) {
            mMyOrders.add(_position, item);
            this.notifyDataSetChanged();
        }
    }

    public void removeAt(int _position) {
        if (mMyOrders != null && (_position >= 0 && _position < mMyOrders.size())) {
            mMyOrders.remove(_position);
            this.notifyItemRemoved(_position);
            this.notifyItemRangeChanged(_position, mMyOrders.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;

        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:

                View viewEmptyList = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_order_item, parent, false);
                viewEmptyList.setTag("VIEW_EMPTY_LIST");
                viewHolder = new OrderAdapter.EmptyListViewHolder(viewEmptyList);

                break;

            case VIEW_TYPE_NON_EMPTY_LIST:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
                view.setTag("VIEW_LIST");
                viewHolder = new OrderAdapter.ViewListHolder(view);

                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        Order orderItem = null;
        if (mMyOrders != null && mMyOrders.size() > 0) {
            orderItem = mMyOrders.get(position);
        } else {
            Log.i(TAG, "Order list is empty");
        }

        if (holder instanceof OrderAdapter.ViewListHolder) {

            OrderAdapter.ViewListHolder viewListHolder = (OrderAdapter.ViewListHolder) holder;

            /*if (orderItem.getProductImage () != null) {
                Picasso.with(mContext).load(orderItem.getProductImage ()).into(viewListHolder.imgProduct);
            } else {
                //Default image
            }


            if(cartItem.getShoppingCartQuantity ()>0){
                viewListHolder.qty.setText (String.valueOf (cartItem.getShoppingCartQuantity ()));
            }else {
                viewListHolder.qty.setText (String.valueOf (0));
            }
            viewListHolder.title.setText(cartItem.getProductName ());
            viewListHolder.description.setText("");
            viewListHolder.price.setText(String.format("$%S", cartItem.getProductPrice ()));

            if (cartItem.getShoppingCartQuantity () > 0) {
                viewListHolder.qty.setText(String.valueOf(cartItem.getShoppingCartQuantity ()));
            } else {
                viewListHolder.qty.setText(String.valueOf(0));

            }

            viewListHolder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("plus", position, mCartItems.get(position));
                    }
                }
            });

            viewListHolder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("minus", position, mCartItems.get(position));
                    }
                }
            });

            viewListHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemDeleted(position, mCartItems.get(position));
                    }
                }
            });*/


        } else if (holder instanceof OrderAdapter.EmptyListViewHolder) {
            OrderAdapter.EmptyListViewHolder emptyListViewHolder = (OrderAdapter.EmptyListViewHolder) holder;

        } else {

        }
    }


    //================= View Holder =================
    private class EmptyListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNoItemFound;

        public EmptyListViewHolder(View itemView) {
            super(itemView);
            imgNoItemFound = (ImageView) itemView.findViewById(R.id.img_no_order_item_found);

        }
    }

    private class ViewListHolder extends RecyclerView.ViewHolder {

        /*ImageView imgProduct;
        TextView title;
        TextView description;
        TextView price;
        Button plus;
        TextView qty;
        Button minus;
        TextView remove;*/

        public ViewListHolder(View itemView) {
            super(itemView);
            /*imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            title = (TextView) itemView.findViewById(R.id.txt_item_title);
            description = (TextView) itemView.findViewById(R.id.txt_item_desc);
            price = (TextView) itemView.findViewById(R.id.txt_item_price);
            plus = (Button) itemView.findViewById(R.id.btn_plus);
            minus = (Button) itemView.findViewById(R.id.btn_minus);
            qty = (TextView) itemView.findViewById(R.id.txt_qty);
            remove = (TextView) itemView.findViewById(R.id.btn_remove);*/

        }
    }

}
