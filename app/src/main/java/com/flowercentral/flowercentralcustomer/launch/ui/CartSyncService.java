package com.flowercentral.flowercentralcustomer.launch.ui;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CartSyncService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CartSyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LocalDAO localDAO = new LocalDAO(CartSyncService.this);
        ArrayList<ShoppingCart> shoppingCartArrayList = localDAO.getCartItemsToBeSynced();
        if (null != shoppingCartArrayList && !shoppingCartArrayList.isEmpty()) {
            try {
                JSONArray requestObjectArray = new JSONArray();
                for (ShoppingCart shoppingCart : shoppingCartArrayList) {
                    JSONObject requestObject = new JSONObject();
                    requestObject.put("product_id", shoppingCart.getProductID());
                    requestObject.put("product_qty", shoppingCart.getProductQuantity());
                    requestObjectArray.put(requestObject);
                }

                performCartSyncWebApi(requestObjectArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void performCartSyncWebApi(JSONArray requestObjectArray) {

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(CartSyncService.this) {
            @Override
            public void onSuccess(int _statusCode, Map<String, String> _headers, JSONObject _response) {

                if (_response != null) {
                    try {
                        if (_response.getString("status").equalsIgnoreCase("1")) {
                            LocalDAO localDAO = new LocalDAO(CartSyncService.this);
                            localDAO.updateCartSyncStatus(1);
                        } else {
                            //TODO
                        }

                    } catch (JSONException jsEx) {
                        jsEx.printStackTrace();
                    }
                } else {
                    //TODO
                }
            }

            @Override
            public void onError(ErrorData error) {
                if (error != null) {

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            break;
                        case INTERNAL_SERVER_ERROR:
                            break;
                        case CONNECTION_TIMEOUT:
                            break;
                        case APPLICATION_ERROR:
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            break;
                        case AUTHENTICATION_ERROR:
                            break;
                        case UNAUTHORIZED_ERROR:
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getCartSyncUrl();
        if (requestObjectArray != null) {
            baseModel.executePostJsonArrayRequest(url, requestObjectArray, IntentService.class.getSimpleName());
        } else {
            //TODO
        }
    }
}
