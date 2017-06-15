package com.flowercentral.flowercentralcustomer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Ashish Upadhyay on 3/23/2016.
 */
public class LocalDAO {

    private static final String TAG = LocalDAO.class.getSimpleName();
    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mFlowerCenterDB;

    public LocalDAO(Context _ctx){
        mContext = _ctx;
        // Get dbHelper instance
        mDBHelper = DBHelper.getInstance(mContext, AppConstant.LOCAL_DB_NAME);
        mFlowerCenterDB = mDBHelper.getDatabase();
    }

    public boolean addProducts(JSONArray _data, boolean isExistingToBeDeleted){
        boolean status = false;

        try{
            if(_data!=null) {
                //Delete existing Products data from table
                if(isExistingToBeDeleted == true){
                    deleteProducts();
                }

                for(int i=0; i<_data.length (); i++){
                    JSONObject object = _data.getJSONObject (i);
                    String productID = object.getString ("id");

                    //Add product if not exists
                    if(!isProductExists (productID)){
                        addProduct (object);
                    }
                }

            }else{
                Logger.log(TAG, "addProducts", "No data available to add into local database.", AppConstant.LOG_LEVEL_INFO );
                status = false;
            }

        }catch (SQLException sqlEx){
            Logger.log(TAG, "addProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        }catch (Exception ex){
            Logger.log(TAG, "addProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        }finally {
            if(mFlowerCenterDB != null && mFlowerCenterDB.isOpen()){
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }
        return status;
    }

    private boolean addProduct(JSONObject _product){
        boolean status = false;
        try{

            ContentValues contentValues = new ContentValues();
            contentValues.put ("productID", _product.getString ("id"));
            contentValues.put("flower", _product.getString ("flower"));
            contentValues.put("description", _product.getString ("description"));
            contentValues.put("category", _product.getString ("category"));
            contentValues.put("image", _product.getString ("image"));
            contentValues.put("quantity", _product.getString ("quantity"));
            contentValues.put("price", _product.getString ("price"));

            String relatedImages = "";
            JSONArray images = _product.optJSONArray ("images");
            relatedImages = implodeJSONArrayToString(images, ",");

            contentValues.put("relatedImages", relatedImages);

            String relatedTags = "";
            JSONArray tags = _product.optJSONArray ("tag");
            relatedTags = implodeJSONArrayToString(tags, ",");
            contentValues.put("tags", relatedTags);

            contentValues.put("liked", _product.getString ("liked"));

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            long rowID = mFlowerCenterDB.insert("Product", null, contentValues);

            if (rowID == -1) {
                status = false;
            } else {
                status = true;
            }

        }catch (SQLException sqlEx){
            Logger.log(TAG, "addProduct", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;

        }catch (Exception ex){
            Logger.log(TAG, "addProduct", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;

        }
        return status;
    }

    private String implodeJSONArrayToString(JSONArray _array, CharSequence _delimeter){
        String imploadedString = "";

        if(_array != null){
            try{
                if(_array.length () > 1){
                    for(int i=0; i< _array.length (); i++){
                        imploadedString = imploadedString + _array.getString (i) + _delimeter;
                    }
                    imploadedString = imploadedString.substring (0, imploadedString.length ()-1);
                }else if(_array.length () == 1){
                    imploadedString = _array.getString (0);
                }
            }catch(JSONException jsonEx){
                imploadedString = "";
                jsonEx.printStackTrace ();
            }catch (Exception ex){
                imploadedString = "";
                ex.printStackTrace ();
            }

        }

        return imploadedString;
    }

    private JSONArray explodeStringToJSONArray (String _images, String _delimeter) {

        JSONArray explodedValue = null;

        if(!TextUtils.isEmpty (_images)){
            String [] images = _images.split (_delimeter);

            if(images.length > 0){
                explodedValue = new JSONArray ();
                for(int i = 0; i < images.length; i++){
                    explodedValue.put (images[i]);
                }
            }
        }

        return explodedValue;
    }


    private boolean deleteProducts() {
        boolean status = false;
        try{
            String sqlQuery = "DELETE FROM Product";
            if(mFlowerCenterDB == null){
                mFlowerCenterDB = mDBHelper.getDatabase();
            }
            mFlowerCenterDB.execSQL(sqlQuery);
            status = true;
        }catch (SQLException sqlEx){
            Logger.log(TAG, "deleteProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        }catch (Exception ex){
            Logger.log(TAG, "deleteProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
        }finally {
            if(mFlowerCenterDB != null && mFlowerCenterDB.isOpen()){
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }
        return status;
    }

    public JSONArray getProducts(){
        String data = null;
        Cursor cursor = null;
        JSONArray jsonArray = null;

        try{
            String sqlQuery = "SELECT id, productID, flower, description, category, image, quantity, price, relatedImages, tags, liked, creationDate FROM Product";

            if (mFlowerCenterDB == null) {
                mFlowerCenterDB = mDBHelper.getDatabase();
            }

            cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount () > 0) {
                cursor.moveToFirst();
                jsonArray = new JSONArray ();
                while (cursor.isAfterLast() == false) {
                    JSONObject jsonObject = new JSONObject ();

                    jsonObject.put ("localProductID", cursor.getString (cursor.getColumnIndex ("id")));
                    jsonObject.put ("id", cursor.getString (cursor.getColumnIndex ("productID")));
                    jsonObject.put ("flower", cursor.getString (cursor.getColumnIndex ("flower")));
                    jsonObject.put ("description", cursor.getString (cursor.getColumnIndex ("description")));
                    jsonObject.put ("category", cursor.getString (cursor.getColumnIndex ("category")));
                    jsonObject.put ("image", cursor.getString (cursor.getColumnIndex ("image")));
                    jsonObject.put ("price", cursor.getString (cursor.getColumnIndex ("price")));
                    jsonObject.put ("quantity", cursor.getString (cursor.getColumnIndex ("quantity")));
                    jsonObject.put ("liked", cursor.getString (cursor.getColumnIndex ("liked")));

                    String images = cursor.getString (cursor.getColumnIndex ("relatedImages"));
                    JSONArray arrImags = explodeStringToJSONArray(images, ",");
                    jsonObject.put ("images", arrImags);

                    String tags = cursor.getString (cursor.getColumnIndex ("tags"));
                    JSONArray arrTags = explodeStringToJSONArray(tags, ",");
                    jsonObject.put ("tag", arrTags);

                    jsonArray.put (jsonObject);
                    cursor.moveToNext();
                }
            } else {
                jsonArray = null;
            }


        }catch(SQLException sqlEx){
            Logger.log(TAG, "getProducts", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            jsonArray = null;

        }catch(Exception ex){
            Logger.log(TAG, "getProducts", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
            jsonArray = null;
        }finally {
            if(cursor!=null){
                cursor.close();
                cursor = null;
            }
            if(mFlowerCenterDB != null && mFlowerCenterDB.isOpen()){
                //mFlowerCenterDB.close();
                //mFlowerCenterDB = null;
            }
        }

        return jsonArray;
    }

    private boolean isProductExists(String _productID){


        Cursor cursor = null;
        boolean status = false;

        String sqlQuery = "SELECT id, productID, flower, description, category, image, quantity, price, relatedImages, tags, liked, creationDate FROM Product where productID = '"+_productID+"'";

        if (mFlowerCenterDB == null) {
            mFlowerCenterDB = mDBHelper.getDatabase();
        }

        cursor = mFlowerCenterDB.rawQuery(sqlQuery, null);
        if(cursor != null && cursor.getCount () > 0){
            status = true;
        }

        return status;

    }

}