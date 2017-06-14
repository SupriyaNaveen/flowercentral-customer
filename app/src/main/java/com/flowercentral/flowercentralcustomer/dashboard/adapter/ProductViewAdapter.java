package com.flowercentral.flowercentralcustomer.dashboard.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish Upadhyay on 5/21/17.
 */

public class ProductViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = ProductViewAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;

    private Context mContext;
    private int mSelectedView;
    private List<Product> mProductList;
    private OnItemClickListener mItemClickListener;

    public ProductViewAdapter(Context _context, int _selView, List<Product> _productList, OnItemClickListener _itemClicklister) {
        mContext = _context;
        mSelectedView = _selView;
        mProductList = _productList;
        mItemClickListener = _itemClicklister;

    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        int viewType;
        if (mProductList != null && mProductList.size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
        } else {
            viewType = VIEW_TYPE_EMPTY_LIST;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mProductList != null && mProductList.size() > 0) {
            size = mProductList.size();
        }
        return size;
    }

    public Product getItemAtPosition(int _position) {
        Product product = null;
        if (mProductList != null && _position > 0) {
            if (_position < mProductList.size()) {
                product = mProductList.get(_position);
            }
        }
        return product;
    }

    public void clear() {
        if (mProductList != null && mProductList.size() > 0) {
            mProductList.clear();
            this.notifyDataSetChanged();
        }
    }

    public void addAll(List<Product> _list) {
        if (mProductList == null) {
            mProductList = new ArrayList<Product>();
        }
        mProductList.clear();
        mProductList.addAll(_list);
        this.notifyDataSetChanged();
    }

    public void addAt(Product item, int _position) {
        if (mProductList == null) {
            mProductList = new ArrayList<Product>();
        }
        if (item != null && _position >= 0) {
            mProductList.add(_position, item);
            this.notifyDataSetChanged();
        }
    }

    public void removeAt(int _position) {
        if (mProductList != null && (_position >= 0 && _position < mProductList.size())) {
            mProductList.remove(_position);
            this.notifyItemRemoved(_position);
            this.notifyItemRangeChanged(_position, mProductList.size());

            if (mItemClickListener != null) {
                mItemClickListener.onItemDeleted(_position, mProductList.get(_position));
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
                viewHolder = new EmptyListViewHolder(viewEmptyList);

                break;

            case VIEW_TYPE_NON_EMPTY_LIST:

                if (mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal()) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
                    view.setTag("VIEW_GRID_LIST");
                    viewHolder = new ViewGridHolder(view);
                } else if (mSelectedView == AppConstant.VIEW_TYPE.LIST.ordinal()) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
                    view.setTag("VIEW_LIST");
                    viewHolder = new ViewListHolder(view);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
                    view.setTag("VIEW_GRID_LIST");
                    viewHolder = new ViewGridHolder(view);
                }

                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Product product = null;
        if (mProductList != null && mProductList.size() > 0) {
            product = mProductList.get(position);
        } else {
            Log.i(TAG, "Product lis is empty");
        }

        if (holder instanceof ViewGridHolder) {
            ViewGridHolder gridViewHolder = (ViewGridHolder) holder;
            gridViewHolder.txtTitle.setText(product.getTitle());
            gridViewHolder.txtDesc.setText(product.getDescription());
            gridViewHolder.txtPrice.setText(String.format("$%s", product.getPrice()));

            if (product.isLiked() == 0) {
                gridViewHolder.imgLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_dislike));
            } else {
                gridViewHolder.imgLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_like));
            }

            if (!TextUtils.isEmpty(product.getImage())) {
                Picasso.with(mContext).load(product.getImage()).into(gridViewHolder.imgProduct);
            } else {
                //Todo add default image
            }

            gridViewHolder.cv_wrapperLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("item", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            gridViewHolder.txtBuyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("buy_now", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            gridViewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("like", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            gridViewHolder.imgCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("cart", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });


        } else if (holder instanceof ViewListHolder) {
            ViewListHolder listViewHolder = (ViewListHolder) holder;
            listViewHolder.txtTitle.setText(product.getTitle());
            listViewHolder.txtDesc.setText(product.getDescription());
            listViewHolder.txtPrice.setText(String.format("$%S", product.getPrice()));

            if (product.isLiked() == 0) {
                listViewHolder.imgLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_dislike));
            } else {
                listViewHolder.imgLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_like));
            }

            if (!TextUtils.isEmpty(product.getImage())) {
                Picasso.with(mContext).load(product.getImage()).into(listViewHolder.imgProduct);
            } else {

            }

            listViewHolder.cv_wrapperLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("item", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            listViewHolder.txtBuyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("buy_now", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            listViewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("like", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

            listViewHolder.imgCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked("cart", position, mProductList.get(position));
                    } else {
                        Log.e(TAG, "Item Click Listener is not implemented");
                    }
                }
            });

        } else if (holder instanceof EmptyListViewHolder) {
            //Empty view holder

        } else {
            // God knows
            Log.i(TAG, "Unknown View Holder");
        }

    }

    private class ViewGridHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        ImageView imgCart;
        ImageView imgLike;

        TextView txtTitle;
        TextView txtDesc;
        TextView txtPrice;
        TextView txtBuyNow;

        CardView cv_wrapperLayout;

        public ViewGridHolder(View itemView) {
            super(itemView);

            cv_wrapperLayout = (CardView) itemView.findViewById(R.id.product_outer_wrapper);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_item_title);
            txtDesc = (TextView) itemView.findViewById(R.id.txt_item_desc);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_item_price);
            txtBuyNow = (TextView) itemView.findViewById(R.id.txt_item_buy_now);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_item_image);
            imgCart = (ImageView) itemView.findViewById(R.id.img_item_cart);
            imgLike = (ImageView) itemView.findViewById(R.id.img_item_like);

        }
    }

    private class ViewListHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        ImageView imgCart;
        ImageView imgLike;

        TextView txtTitle;
        TextView txtDesc;
        TextView txtPrice;
        TextView txtBuyNow;

        CardView cv_wrapperLayout;

        public ViewListHolder(View itemView) {
            super(itemView);
            cv_wrapperLayout = (CardView) itemView.findViewById(R.id.product_outer_wrapper);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_item_title);
            txtDesc = (TextView) itemView.findViewById(R.id.txt_item_desc);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_item_price);
            txtBuyNow = (TextView) itemView.findViewById(R.id.txt_item_buy_now);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_item_image);
            imgCart = (ImageView) itemView.findViewById(R.id.img_item_cart);
            imgLike = (ImageView) itemView.findViewById(R.id.img_item_like);
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


}
