package com.flowercentral.flowercentralcustomer.cart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish Upadhyay on 5/25/17.
 */

public class CartItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CartItemAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;

    private Context mContext;
    //private ArrayList<Product> mCartItems;
    private ArrayList<ShoppingCart> mCartItems;
    private OnItemClickListener mItemClickListener;

    public CartItemAdapter(Context _context, ArrayList<ShoppingCart> _cartItems, OnItemClickListener _itemClickListener) {
        mContext = _context;
        mCartItems = _cartItems;
        mItemClickListener = _itemClickListener;

    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        int viewType;
        if (mCartItems != null && mCartItems.size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
        } else {
            viewType = VIEW_TYPE_EMPTY_LIST;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        int size = 1;
        if (mCartItems != null && mCartItems.size() > 0) {
            size = mCartItems.size();
        }
        return size;
    }

    public ShoppingCart getItemAtPosition(int _position) {
        //Product product = null;
        ShoppingCart cartItem = null;
        if (mCartItems != null && _position > 0) {
            if (_position < mCartItems.size()) {
                cartItem = mCartItems.get(_position);
            }
        }
        return cartItem;
    }

    public void clear() {
        if (mCartItems != null && mCartItems.size() > 0) {
            mCartItems.clear();
            this.notifyDataSetChanged();
        }
    }

    public void addAll(List<ShoppingCart> _list) {
        if (mCartItems == null) {
            mCartItems = new ArrayList<ShoppingCart>();
        }
        mCartItems.clear();
        mCartItems.addAll(_list);
        this.notifyDataSetChanged();
    }

    public void addAt(ShoppingCart item, int _position) {
        if (mCartItems == null) {
            mCartItems = new ArrayList<ShoppingCart>();
        }
        if (item != null && _position >= 0) {
            mCartItems.add(_position, item);
            this.notifyDataSetChanged();
        }
    }

    public void updateQuantity(String _type, int _position, int _qty) {
        if (mCartItems != null && mCartItems.size() > _position) {
            int qty = mCartItems.get(_position).getShoppingCartQuantity();
            if (!TextUtils.isEmpty(_type) && _type.trim().equalsIgnoreCase("plus")) {
                mCartItems.get(_position).setShoppingCartQuantity(qty + _qty);
                this.notifyDataSetChanged();
            } else if (!TextUtils.isEmpty(_type) && _type.trim().equalsIgnoreCase("minus")) {
                if (qty > 1) {
                    mCartItems.get(_position).setShoppingCartQuantity(qty - _qty);
                    this.notifyDataSetChanged();
                }
            } else {
                // Do nothing
            }
        }
    }

    public void removeAt(int _position) {
        if (mCartItems != null && (_position >= 0 && _position < mCartItems.size())) {
            mCartItems.remove(_position);
            this.notifyItemRemoved(_position);
            this.notifyItemRangeChanged(_position, mCartItems.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;

        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:

                View viewEmptyList = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_cart_item, parent, false);
                viewEmptyList.setTag("VIEW_EMPTY_LIST");
                viewHolder = new CartItemAdapter.EmptyListViewHolder(viewEmptyList);

                break;

            case VIEW_TYPE_NON_EMPTY_LIST:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item, parent, false);
                view.setTag("VIEW_LIST");
                viewHolder = new CartItemAdapter.ViewListHolder(view);

                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ShoppingCart cartItem = null;
        if (mCartItems != null && mCartItems.size() > 0) {
            cartItem = mCartItems.get(position);
        } else {
            Log.i(TAG, "Product lis is empty");
        }

        if (holder instanceof ViewListHolder) {

            ViewListHolder viewListHolder = (ViewListHolder) holder;
            if (cartItem.getProductImage() != null) {
                Picasso.with(mContext).load(cartItem.getProductImage()).into(viewListHolder.imgProduct);
            } else {
                //Default image
            }


            if (cartItem.getShoppingCartQuantity() > 0) {
                viewListHolder.qty.setText(String.valueOf(cartItem.getShoppingCartQuantity()));
            } else {
                viewListHolder.qty.setText(String.valueOf(0));
            }
            viewListHolder.title.setText(cartItem.getProductName());
            viewListHolder.description.setText("");
            viewListHolder.price.setText(String.format("$%S", cartItem.getProductPrice()));

            if (cartItem.getShoppingCartQuantity() > 0) {
                viewListHolder.qty.setText(String.valueOf(cartItem.getShoppingCartQuantity()));
            } else {
                viewListHolder.qty.setText(String.valueOf(0));

            }

            viewListHolder.plus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("plus", position, mCartItems.get(position));
                    }
                }
            });

            viewListHolder.minus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("minus", position, mCartItems.get(position));
                    }
                }
            });

            viewListHolder.remove.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemDeleted(position, mCartItems.get(position));
                    }
                }
            });


        } else if (holder instanceof EmptyListViewHolder) {
            EmptyListViewHolder emptyListViewHolder = (EmptyListViewHolder) holder;

        } else {

        }

    }

    private class EmptyListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNoItemFound;

        public EmptyListViewHolder(View itemView) {
            super(itemView);
            imgNoItemFound = (ImageView) itemView.findViewById(R.id.img_no_item_found);

        }
    }

    private class ViewListHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView title;
        TextView description;
        TextView price;
        RippleView plus;
        TextView qty;
        RippleView minus;
        RippleView remove;

        public ViewListHolder(View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            title = (TextView) itemView.findViewById(R.id.txt_item_title);
            description = (TextView) itemView.findViewById(R.id.txt_item_desc);
            price = (TextView) itemView.findViewById(R.id.txt_item_price);
            plus = (RippleView) itemView.findViewById(R.id.btn_plus);
            minus = (RippleView) itemView.findViewById(R.id.btn_minus);
            qty = (TextView) itemView.findViewById(R.id.txt_qty);
            remove = (RippleView) itemView.findViewById(R.id.btn_remove);
        }
    }
}
