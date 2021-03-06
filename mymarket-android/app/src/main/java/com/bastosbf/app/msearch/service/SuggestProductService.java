package com.bastosbf.app.msearch.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bastosbf.app.msearch.R;
import com.bastosbf.app.msearch.activity.MainActivity;
import com.bastosbf.app.msearch.activity.ProductActivity;
import com.bastosbf.app.msearch.model.Market;
import com.bastosbf.app.msearch.model.Place;
import com.bastosbf.app.msearch.model.Product;
import com.bastosbf.app.msearch.model.Search;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SuggestProductService extends IntentService {

    public SuggestProductService(){
        super("SuggestProductService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ArrayList<Place> places = (ArrayList<Place>) intent.getSerializableExtra("places");
            ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
            Place place = (Place) intent.getSerializableExtra("place");
            Market market = (Market) intent.getSerializableExtra("market");
            String barcode = intent.getStringExtra("barcode");
            String name = intent.getStringExtra("name");
            String brand = intent.getStringExtra("brand");
            String price = intent.getStringExtra("price");
            URL url = new URL(intent.getStringExtra("root-url") + "/rest/collaboration/suggest-product?market="+market.getId()+"&barcode="+barcode+"&name="+name+"&brand="+brand+"&price="+price);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            is.close();

            Intent i = new Intent("SUGGEST_PRODUCT");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
