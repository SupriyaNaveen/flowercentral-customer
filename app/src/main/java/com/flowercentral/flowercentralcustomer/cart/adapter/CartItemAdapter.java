package com.flowercentral.flowercentralcustomer.cart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
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
    private ArrayList<Product> mCartItems;
    private OnItemClickListener mItemClickListener;

    public CartItemAdapter(Context _context, ArrayList<Product> _cartItems, OnItemClickListener _itemClickListener) {
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
        int size = 0;
        if (mCartItems != null && mCartItems.size() > 0) {
            size = mCartItems.size();
        }
        return size;
    }

    public Product getItemAtPosition(int _position) {
        Product product = null;
        if (mCartItems != null && _position > 0) {
            if (_position < mCartItems.size()) {
                product = mCartItems.get(_position);
            }
        }
        return product;
    }

    public void clear() {
        if (mCartItems != null && mCartItems.size() > 0) {
            mCartItems.clear();
            this.notifyDataSetChanged();
        }
    }

    public void addAll(List<Product> _list) {
        if (mCartItems == null) {
            mCartItems = new ArrayList<Product>();
        }
        mCartItems.clear();
        mCartItems.addAll(_list);
        this.notifyDataSetChanged();
    }

    public void addAt(Product item, int _position) {
        if (mCartItems == null) {
            mCartItems = new ArrayList<Product>();
        }
        if (item != null && _position >= 0) {
            mCartItems.add(_position, item);
            this.notifyDataSetChanged();
        }
    }

    public void updateQuantity(String _type, int _position, int _qty) {
        if (mCartItems != null && mCartItems.size() > _position) {
            int qty = mCartItems.get(_position).getQty();
            if (!TextUtils.isEmpty(_type) && _type.trim().equalsIgnoreCase("plus")) {
                mCartItems.get(_position).setQty(qty + _qty);
                this.notifyDataSetChanged();
            } else if (!TextUtils.isEmpty(_type) && _type.trim().equalsIgnoreCase("minus")) {
                if (qty > 0) {
                    mCartItems.get(_position).setQty(qty - _qty);
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

            if (mItemClickListener != null) {
                mItemClickListener.onItemDeleted(_position, mCartItems.get(_position));
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;

        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:

                View viewEmptyList = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_product_item, parent, false);
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

        Product product = null;
        if (mCartItems != null && mCartItems.size() > 0) {
            product = mCartItems.get(position);
        } else {
            Log.i(TAG, "Product lis is empty");
        }

        if (holder instanceof ViewListHolder) {

            ViewListHolder viewListHolder = (ViewListHolder) holder;
            if (product.getImage() != null) {
                Picasso.with(mContext).load(product.getImage()).into(viewListHolder.imgProduct);
            } else {
                //Default image
            }


            if(product.getQty ()>0){
                viewListHolder.qty.setText (String.valueOf (product.getQty()));
            }else {
                viewListHolder.qty.setText (String.valueOf (0));
            }
            viewListHolder.title.setText(product.getFlowerName ());
            viewListHolder.description.setText(product.getDescription());
            viewListHolder.price.setText(String.format("$%S", product.getPrice()));

            if (product.getQty() > 0) {
                viewListHolder.qty.setText(String.valueOf(product.getQty()));
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
            });


        } else if (holder instanceof EmptyListViewHolder) {
            EmptyListViewHolder emptyListViewHolder = (EmptyListViewHolder) holder;
            emptyListViewHolder.txtNoItemFound.setText(mContext.getString(R.string.msg_empty_cart));

        } else {

        }

    }

    private class EmptyListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNoItemFound;
        TextView txtNoItemFound;

        public EmptyListViewHolder(View itemView) {
            super(itemView);
            imgNoItemFound = (ImageView) itemView.findViewById(R.id.img_no_item_found);
            txtNoItemFound = (TextView) itemView.findViewById(R.id.txt_msg_no_item_found);
        }
    }

    private class ViewListHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView title;
        TextView description;
        TextView price;
        Button plus;
        TextView qty;
        Button minus;
        TextView remove;

        public ViewListHolder(View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            title = (TextView) itemView.findViewById(R.id.txt_item_title);
            description = (TextView) itemView.findViewById(R.id.txt_item_desc);
            price = (TextView) itemView.findViewById(R.id.txt_item_price);
            plus = (Button) itemView.findViewById(R.id.btn_plus);
            minus = (Button) itemView.findViewById(R.id.btn_minus);
            qty = (TextView) itemView.findViewById(R.id.txt_qty);
            remove = (TextView) itemView.findViewById(R.id.btn_remove);

        }
    }

}
