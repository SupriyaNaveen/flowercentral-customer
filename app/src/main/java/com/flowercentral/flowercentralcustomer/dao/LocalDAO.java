package com.flowercentral.flowercentralcustomer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.flowercentral.flowercentralcustomer.common.model.Order;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Ashish Upadhyay on 3/23/2016.
 */
public class LocalDAO {

    private static final String TAG = LocalDAO.class.getSimpleName();
    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mFlowerCenterDB;

    public LocalDAO(Context _ctx) {
        mContext = _ctx;
        // Get dbHelper instance
        mDBHelper = DBHelper.getInstance(mContext, AppConstant.LOCAL_DB_NAME);
        mFlowerCenterDB = mDBHelper.getDatabase();
    }

    public boolean addProducts(JSONArray _data, boolean isExistingToBeDeleted) {
        boolean status = false;

        try {
            if (_data != null) {
                //Delete existing Products data from table
                if (isExistingToBeDeleted == true) {
                    deleteProducts();
                }

                for (int i = 0; i < _data.length(); i++) {
                    JSONObject object = _data.getJSONObject(i);
                    String productID = object.getString("id");

                    //Add product if not exists
                    if (!isProductExists(productID)) {
                        addProduct(object);
                    }
                }

            } else {
                Logger.log(TAG, "addProducts", "No data available to add into local database.", AppConstant.LOG_LEVEL_INFO);
                status = false;
            }

        } catch (SQLException sqlEx) {
            Logger.log(TAG, "addProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } catch (Exception ex) {
            Logger.log(TAG, "addProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } finally {
            if (mFlowerCenterDB != null && mFlowerCenterDB.isOpen()) {
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }
        return status;
    }

    private boolean addProduct(JSONObject _product) {
        boolean status = false;
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put("productID", _product.getString("id"));
            // Not null flower title field
            contentValues.put("flower", _product.getString("title"));
            contentValues.put("description", _product.getString("description"));
            contentValues.put("category", _product.getString("category"));
            contentValues.put("image", _product.getString("image"));
//            contentValues.put("quantity", _product.getString ("quantity"));
            contentValues.put("price", _product.getString("price"));

            String relatedImages = "";
            JSONArray images = _product.optJSONArray("images");
            relatedImages = implodeJSONArrayToString(images, ",");

            contentValues.put("relatedImages", relatedImages);

            String relatedTags = "";
            JSONArray tags = _product.optJSONArray("tag");
            relatedTags = implodeJSONArrayToString(tags, ",");
            contentValues.put("tags", relatedTags);

            contentValues.put("liked", _product.getString("liked"));

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            long rowID = mFlowerCenterDB.insert("Product", null, contentValues);

            if (rowID == -1) {
                status = false;
            } else {
                status = true;
            }

        } catch (SQLException sqlEx) {
            Logger.log(TAG, "addProduct", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;

        } catch (Exception ex) {
            Logger.log(TAG, "addProduct", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;

        }
        return status;
    }

    private String implodeJSONArrayToString(JSONArray _array, CharSequence _delimeter) {
        String imploadedString = "";

        if (_array != null) {
            try {
                if (_array.length() > 1) {
                    for (int i = 0; i < _array.length(); i++) {
                        imploadedString = imploadedString + _array.getString(i) + _delimeter;
                    }
                    imploadedString = imploadedString.substring(0, imploadedString.length() - 1);
                } else if (_array.length() == 1) {
                    imploadedString = _array.getString(0);
                }
            } catch (JSONException jsonEx) {
                imploadedString = "";
                jsonEx.printStackTrace();
            } catch (Exception ex) {
                imploadedString = "";
                ex.printStackTrace();
            }

        }

        return imploadedString;
    }

    private JSONArray explodeStringToJSONArray(String _images, String _delimeter) {

        JSONArray explodedValue = null;

        if (!TextUtils.isEmpty(_images)) {
            String[] images = _images.split(_delimeter);

            if (images.length > 0) {
                explodedValue = new JSONArray();
                for (int i = 0; i < images.length; i++) {
                    explodedValue.put(images[i]);
                }
            }
        }

        return explodedValue;
    }


    private boolean deleteProducts() {
        boolean status = false;
        try {
            String sqlQuery = "DELETE FROM Product";
            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }
            mFlowerCenterDB.execSQL(sqlQuery);
            status = true;
        } catch (SQLException sqlEx) {
            Logger.log(TAG, "deleteProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } catch (Exception ex) {
            Logger.log(TAG, "deleteProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } finally {
            if (mFlowerCenterDB != null && mFlowerCenterDB.isOpen()) {
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }
        return status;
    }

    public JSONArray getProducts() {
        String data = null;
        Cursor cursor = null;
        JSONArray jsonArray = null;

        try {
            String sqlQuery = "SELECT id, productID, flower, description, category, image, quantity, price, relatedImages, tags, liked, creationDate FROM Product";

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                jsonArray = new JSONArray();
                while (cursor.isAfterLast() == false) {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("localProductID", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("productID")));
                    jsonObject.put ("title", cursor.getString (cursor.getColumnIndex ("flower")));
                    jsonObject.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    jsonObject.put("category", cursor.getString(cursor.getColumnIndex("category")));
                    jsonObject.put("image", cursor.getString(cursor.getColumnIndex("image")));
                    jsonObject.put("price", cursor.getString(cursor.getColumnIndex("price")));
//                    jsonObject.put ("quantity", cursor.getString (cursor.getColumnIndex ("quantity")));
                    jsonObject.put("liked", cursor.getString(cursor.getColumnIndex("liked")));

                    String images = cursor.getString(cursor.getColumnIndex("relatedImages"));
                    JSONArray arrImags = explodeStringToJSONArray(images, ",");
                    jsonObject.put("images", arrImags);

                    String tags = cursor.getString(cursor.getColumnIndex("tags"));
                    JSONArray arrTags = explodeStringToJSONArray(tags, ",");
                    jsonObject.put("tag", arrTags);

                    jsonArray.put(jsonObject);
                    cursor.moveToNext();
                }
            } else {
                jsonArray = null;
            }


        } catch (SQLException sqlEx) {
            Logger.log(TAG, "getProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            jsonArray = null;

        } catch (Exception ex) {
            Logger.log(TAG, "getProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            jsonArray = null;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (mFlowerCenterDB != null && mFlowerCenterDB.isOpen()) {
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }

        return jsonArray;
    }

    private boolean isProductExists(String _productID) {


        Cursor cursor = null;
        boolean status = false;

        String sqlQuery = "SELECT id, productID, flower, description, category, image, quantity, price, relatedImages, tags, liked, creationDate FROM Product where productID = '" + _productID + "'";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            status = true;
        }

        return status;

    }

    // ================= For Shopping Cart =================

    public boolean addItemsToCart(ArrayList<ShoppingCart> _cartItems) {
        boolean isAdded = false;

        if (_cartItems != null && _cartItems.size() > 0) {
            for (ShoppingCart cartItem : _cartItems) {

                try {
                    boolean isItemExists = isItemExistsinCart(cartItem.getProductID());
                    if (isItemExists == true) {
                        //if product exists then update the quanitity
                        isAdded = updateItemQuantity(cartItem);

                    } else {
                        //Else Add product
                        isAdded = addItemToCart(cartItem);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    isAdded = false;
                }

            }
        }

        return isAdded;
    }

    public boolean addItemToCart(ShoppingCart _cartItem) {
        boolean isAdded = false;
        if (_cartItem != null) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("productID", _cartItem.getProductID());
                contentValues.put("productName", _cartItem.getProductName());
                contentValues.put("productCategory", _cartItem.getProductCategory());
                contentValues.put("productQty", _cartItem.getProductQuantity());
                contentValues.put("productPrice", _cartItem.getProductPrice());
                contentValues.put("productImage", _cartItem.getProductImage());
                contentValues.put("cartQuantity", _cartItem.getShoppingCartQuantity());
                contentValues.put("userMessage", _cartItem.getUserMessage());
                contentValues.put("status", _cartItem.getStatus());

                if (mFlowerCenterDB == null) {
                    mFlowerCenterDB = mDBHelper.getDatabase();
                }

                long rowID = mFlowerCenterDB.insert("ShoppingCart", null, contentValues);

                if (rowID == -1) {
                    isAdded = false;
                } else {
                    isAdded = true;
                }

            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                isAdded = false;
            } catch (Exception ex) {
                ex.printStackTrace();
                isAdded = false;
            }
        }
        return isAdded;
    }

    public boolean isItemExistsinCart(int _productID) {
        boolean isExists = false;
        Cursor cursor = null;

        String sqlQuery = "SELECT * FROM ShoppingCart where productID = " + _productID + "";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            isExists = true;
        }
        return isExists;
    }

    public ShoppingCart getCartItem(int _productID) {
        ShoppingCart cartItem = null;
        Cursor cursor = null;

        String sqlQuery = "SELECT productID, productName, productCategory, productQty, productPrice, productImage, cartQuantity, " +
                "userMessage, status  FROM ShoppingCart where productID = " + _productID + "";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        try {
            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cartItem = new ShoppingCart();
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    cartItem.setProductID(cursor.getInt(cursor.getColumnIndex("productID")));
                    cartItem.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                    cartItem.setProductCategory(cursor.getString(cursor.getColumnIndex("productCategory")));
                    cartItem.setProductQuantity(cursor.getInt(cursor.getColumnIndex("productQty")));
                    cartItem.setProductPrice(cursor.getDouble(cursor.getColumnIndex("productPrice")));
                    cartItem.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
                    cartItem.setShoppingCartQuantity(cursor.getInt(cursor.getColumnIndex("cartQuantity")));
                    cartItem.setUserMessage(cursor.getString(cursor.getColumnIndex("userMessage")));
                    cartItem.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                    cursor.moveToNext();
                }
            } else {
                Log.i(TAG, "No item in the cart");
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            cartItem = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            cartItem = null;
        }

        return cartItem;
    }

    public boolean updateItemQuantity(ShoppingCart _cartItem) {
        boolean isUpdated = false;
        int qunatityTobeUpdated = 0;

        if (_cartItem != null) {
            ShoppingCart cartItem = getCartItem(_cartItem.getProductID());
            if (cartItem != null) {
                qunatityTobeUpdated = _cartItem.getShoppingCartQuantity() + cartItem.getShoppingCartQuantity();
            }
            String sqlQuery = "UPDATE ShoppingCart SET cartQuantity = " + qunatityTobeUpdated + " WHERE productID = " + _cartItem.getProductID();
            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            Cursor cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            isUpdated = true;
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "item quantity has been updatd.");
            }

        }
        return isUpdated;
    }

    public void updateCartSyncStatus(int syncStatus) {
        String sqlQuery = "UPDATE ShoppingCart SET isSynced = " + syncStatus;
        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        Cursor cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.i(TAG, "item quantity has been updatd.");
        }
    }

    public boolean updateItemQuantity(int _productID, int _qty) {
        boolean isUpdated = false;
        int qunatityTobeUpdated = 0;

        if (_productID > 0) {
            ShoppingCart cartItem = getCartItem(_productID);
            if (cartItem != null) {
                qunatityTobeUpdated = cartItem.getShoppingCartQuantity() + _qty;
            }

            String sqlQuery = "UPDATE ShoppingCart SET cartQuantity = " + qunatityTobeUpdated + " WHERE productID = " + _productID;

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            Cursor cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            isUpdated = true;
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "item quantity has been updatd.");
            }

        }
        return isUpdated;
    }

    public ArrayList<ShoppingCart> getCartItems() {
        ArrayList<ShoppingCart> cartItems = null;
        ShoppingCart cartItem = null;
        Cursor cursor = null;

        String sqlQuery = "SELECT productID, productName, productCategory, productQty, productPrice, productImage, cartQuantity, " +
                "userMessage, status  FROM ShoppingCart";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        try {
            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cartItems = new ArrayList<ShoppingCart>();

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    cartItem = new ShoppingCart();

                    cartItem.setProductID(cursor.getInt(cursor.getColumnIndex("productID")));
                    cartItem.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                    cartItem.setProductCategory(cursor.getString(cursor.getColumnIndex("productCategory")));
                    cartItem.setProductQuantity(cursor.getInt(cursor.getColumnIndex("productQty")));
                    cartItem.setProductPrice(cursor.getDouble(cursor.getColumnIndex("productPrice")));
                    cartItem.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
                    cartItem.setShoppingCartQuantity(cursor.getInt(cursor.getColumnIndex("cartQuantity")));
                    cartItem.setUserMessage(cursor.getString(cursor.getColumnIndex("userMessage")));
                    cartItem.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                    cartItems.add(cartItem);

                    cursor.moveToNext();
                }
            } else {
                Log.i(TAG, "No item in the cart");
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            cartItem = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            cartItem = null;
        }
        return cartItems;
    }

    public ArrayList<ShoppingCart> getCartItemsToBeSynced() {
        ArrayList<ShoppingCart> cartItems = null;
        ShoppingCart cartItem = null;
        Cursor cursor = null;

        String sqlQuery = "SELECT productID, productName, productCategory, productQty, productPrice, productImage, cartQuantity, " +
                "userMessage, status  FROM ShoppingCart where isSynced = " + 0 + "";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        try {
            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cartItems = new ArrayList<ShoppingCart>();

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    cartItem = new ShoppingCart();

                    cartItem.setProductID(cursor.getInt(cursor.getColumnIndex("productID")));
                    cartItem.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                    cartItem.setProductCategory(cursor.getString(cursor.getColumnIndex("productCategory")));
                    cartItem.setProductQuantity(cursor.getInt(cursor.getColumnIndex("productQty")));
                    cartItem.setProductPrice(cursor.getDouble(cursor.getColumnIndex("productPrice")));
                    cartItem.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
                    cartItem.setShoppingCartQuantity(cursor.getInt(cursor.getColumnIndex("cartQuantity")));
                    cartItem.setUserMessage(cursor.getString(cursor.getColumnIndex("userMessage")));
                    cartItem.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                    cartItems.add(cartItem);

                    cursor.moveToNext();
                }
            } else {
                Log.i(TAG, "No item in the cart");
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            cartItem = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            cartItem = null;
        }
        return cartItems;
    }

    public boolean deleteItem(int _productID) {
        boolean isDeleted = false;
        String sqlQuery = "";
        try {
            if (_productID > 0) {
                sqlQuery = "DELETE FROM ShoppingCart WHERE productID = " + _productID;
            } else {
                sqlQuery = "DELETE FROM ShoppingCart";
            }

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }
            mFlowerCenterDB.execSQL(sqlQuery);
            isDeleted = true;
        } catch (SQLException sqlEx) {
            Logger.log(TAG, "deleteItem", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            isDeleted = false;
        } catch (Exception ex) {
            Logger.log(TAG, "deleteItem", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            isDeleted = false;
        } finally {
            if (mFlowerCenterDB != null && mFlowerCenterDB.isOpen()) {
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }

        return isDeleted;
    }

    //======================= Order related queries =======================

    public ArrayList<Order> getOrders(Context _context, @Nullable Integer _id) {
        ArrayList<Order> orders = null;
        Order order = null;
        Cursor cursor = null;

        String sqlQuery = "SELECT order_data FROM UserOrder";

        if (_id != null && _id > 0) {
            sqlQuery = sqlQuery + " where order_id='" + _id + "'";
        }

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        try {
            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);

            if (cursor != null && cursor.getCount() > 0) {
                orders = new ArrayList<Order>();

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    order = new Order();

                    String strOrder = cursor.getString(cursor.getColumnIndex("order_data"));
                    JSONObject object = new JSONObject(strOrder);

                    order.setOrderID(object.getString("id"));
                    order.setOrderDate(object.getString("order_date"));
                    order.setDeliveryAddress(object.getString("address"));
                    order.setDeliveredAt(object.getString("delivered_at"));
                    order.setStatus(object.getString("status"));
                    order.setPaymentStatus(object.getString("payment_status"));
                    order.setProducts(object.getJSONArray("product_details"));

                    orders.add(order);

                    cursor.moveToNext();
                }
            } else {
                Log.i(TAG, "No order");
            }
        } catch (JSONException jsEx) {
            jsEx.printStackTrace();
            orders = null;
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            orders = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            orders = null;
        }

        return orders;
    }

    public boolean addOrder(Context _context, JSONArray _orders) {
        boolean isAdded = false;
        if (_orders == null) {
            return isAdded;
        }
        for (int i = 0; i < _orders.length(); i++) {
            try {
                JSONObject order = _orders.getJSONObject(i);

                String orderID = order.getString("id");
                boolean isOrderExixts = isOrderExists(orderID);

                if (isOrderExixts == false) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("order_id", order.getString("id"));
                    contentValues.put("order_data", order.toString());
                    contentValues.put("isSynced", 0);

                    if (mFlowerCenterDB == null) {
                        mFlowerCenterDB = mDBHelper.getDatabase();
                    }

                    long rowID = mFlowerCenterDB.insert("UserOrder", null, contentValues);

                    if (rowID == -1) {
                        isAdded = false;
                    } else {
                        isAdded = true;
                    }
                }


            } catch (JSONException jsEx) {
                jsEx.printStackTrace();
                isAdded = false;

            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                isAdded = false;
            } catch (Exception ex) {
                ex.printStackTrace();
                isAdded = false;
            }


        }

        return isAdded;
    }

    public boolean deleteOrders(Context _context, @Nullable Integer _id) {
        boolean status = false;
        try {
            String sqlQuery = "DELETE FROM Order";

            if (_id != null && _id > 0) {
                sqlQuery = sqlQuery + " where order_id=" + _id;
            }

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            mFlowerCenterDB.execSQL(sqlQuery);
            status = true;
        } catch (SQLException sqlEx) {
            Logger.log(TAG, "deleteOrder", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } catch (Exception ex) {
            Logger.log(TAG, "deleteOrder", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        } finally {

        }
        return status;

    }

    public boolean updateOrderSyncStatus(Context _context, int _orderId, int _status) {
        boolean isUpdated = false;
        int qunatityTobeUpdated = 0;

        if (_orderId > 0) {

            String sqlQuery = "UPDATE Order SET isSynced = " + _status + " WHERE order_id = " + _orderId;

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            Cursor cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            isUpdated = true;
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "order status has been updated.");
            }

        }
        return isUpdated;
    }

    public boolean isOrderExists(String _orderID) {
        boolean isExists = false;
        Cursor cursor = null;

        String sqlQuery = "SELECT * FROM UserOrder where order_id = " + _orderID + "";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            isExists = true;
        }
        return isExists;
    }

}