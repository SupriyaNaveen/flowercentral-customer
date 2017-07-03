package com.flowercentral.flowercentralcustomer.order.adapter;

import android.content.Context;
import android.support.transition.TransitionManager;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Order;
import com.flowercentral.flowercentralcustomer.common.model.Product;
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
    private final LayoutInflater mLayoutInflater;

    private Context mContext;
    private ArrayList<Order> mMyOrders;
    private OnItemClickListener mItemClickListener;
    private int mExpandedPosition = -1;


    public OrderAdapter(Context _context, ArrayList<Order> _orders, OnItemClickListener _itemClickListener){
        mContext = _context;
        mMyOrders = _orders;
        mItemClickListener = _itemClickListener;
        mLayoutInflater = LayoutInflater.from(mContext);
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

                //View viewEmptyList = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_order_item, parent, false);
                View viewEmptyList = mLayoutInflater.inflate(R.layout.layout_no_order_item, parent, false);
                viewEmptyList.setTag("VIEW_EMPTY_LIST");
                viewHolder = new OrderAdapter.EmptyListViewHolder(viewEmptyList);

                break;

            case VIEW_TYPE_NON_EMPTY_LIST:

                //view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
                view = mLayoutInflater.inflate(R.layout.layout_order_item, parent, false);
                view.setTag("VIEW_LIST");
                viewHolder = new OrderAdapter.ViewListHolder(view);

                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, final int position) {
        Order orderItem = null;
        if (mMyOrders != null && mMyOrders.size() > 0) {
            orderItem = mMyOrders.get(position);
        } else {
            Log.i(TAG, "Order list is empty");
        }

        if (holder instanceof OrderAdapter.ViewListHolder) {
            final boolean isExpanded = position==mExpandedPosition;

            final OrderAdapter.ViewListHolder viewListHolder = (OrderAdapter.ViewListHolder) holder;

            ArrayList<Product> products = orderItem.getProducts ();

            if(products != null && products.size ()>0){

                viewListHolder.itemContainer.removeAllViews ();

                for(Product product : products){
                    View itemLayout = mLayoutInflater.inflate (R.layout.layout_item, null);
                    itemLayout.setId (View.generateViewId ());
                    ImageView imgItem = (ImageView) itemLayout.findViewById (R.id.img_item);
                    TextView txtFlowerName = (TextView) itemLayout.findViewById (R.id.txt_item_title);
                    TextView txtDesc = (TextView) itemLayout.findViewById (R.id.txt_item_desc);
                    TextView txtPrice = (TextView) itemLayout.findViewById (R.id.txt_item_price);
                    TextView txtUserMsg = (TextView) itemLayout.findViewById (R.id.txt_user_message);

                    if (product.getImage () != null) {
                        Picasso.with(mContext).load(product.getImage ()).into(imgItem);
                    } else {
                        //Default image
                    }
                    txtFlowerName.setText (product.getFlowerName ());
                    txtDesc.setText (product.getDescription ());
                    txtPrice.setText (String.valueOf (product.getPrice ()));

                    if(!TextUtils.isEmpty (product.getUserMessage ())){
                        txtUserMsg.setText (product.getUserMessage ());
                    }


                    viewListHolder.itemContainer.addView (itemLayout);

                }

            }

            viewListHolder.itemExpandedWrapper.setVisibility (isExpanded==true?View.VISIBLE:View.GONE);
            viewListHolder.txtMore.setActivated (isExpanded);

            viewListHolder.txtMore.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    mExpandedPosition = isExpanded ? -1:position;
                    TransitionManager.beginDelayedTransition(viewListHolder.itemInnerWrapper);
                    notifyDataSetChanged();
                }
            });


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

        LinearLayout itemInnerWrapper;
        LinearLayout itemExpandedWrapper;
        LinearLayout itemContainer;
        TextView txtMore;

        public ViewListHolder(View itemView) {
            super(itemView);

            itemInnerWrapper = (LinearLayout) itemView.findViewById (R.id.ll_order_item_inner_wrapper);
            itemExpandedWrapper = (LinearLayout) itemView.findViewById (R.id.ll_expanded_item_content_wrapper);
            txtMore = (TextView) itemView.findViewById (R.id.txt_more);
            itemContainer = (LinearLayout) itemView.findViewById (R.id.item_container);

        }
    }

}
