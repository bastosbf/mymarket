package com.bastosbf.app.msearch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bastosbf.app.msearch.R;
import com.bastosbf.app.msearch.model.Market;
import com.bastosbf.app.msearch.model.Place;
import com.bastosbf.app.msearch.model.Product;
import com.bastosbf.app.msearch.model.Search;
import com.bastosbf.app.msearch.service.FindProductService;
import com.bastosbf.app.msearch.service.ListMarketsService;
import com.bastosbf.app.msearch.service.ListPlacesService;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ProductActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private ListView listView;
    private Button button;
    private ProgressDialog progress;
    private String rootURL;

    private BroadcastReceiver productsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress.dismiss();
            receiveList(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        try {
            InputStream is = getBaseContext().getAssets().open("mymarket.properties");
            Properties properties = new Properties();
            properties.load(is);
            rootURL = properties.getProperty("root.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        final Intent i = new Intent(ProductActivity.this, FindProductService.class);
        i.putExtra("barcode", intent.getStringExtra("barcode"));
        i.putExtra("place", intent.getSerializableExtra("place"));
        i.putExtra("market", intent.getSerializableExtra("market"));
        i.putExtra("markets", intent.getSerializableExtra("markets"));
        i.putExtra("root-url", rootURL);
        startService(i);
        progress = ProgressDialog.show(ProductActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.products_loading_activity_product), true, true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopService(i);
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver((productsReceiver), new IntentFilter("PRODUCTS"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    protected void receiveList(final Intent intent) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button);

        ArrayList<Search> results = (ArrayList<Search>) intent.getSerializableExtra("results");
        if(results.isEmpty()) {
            textView1.setText(getResources().getString(R.string.not_found_activity_product));
            String barcode = intent.getStringExtra("barcode");
            textView2.setText(barcode);
            textView2.setVisibility(View.INVISIBLE);
            button.setText(getResources().getString(R.string.add_activity_product));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String barcode = String.valueOf(textView2.getText());
                    ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                    Place place = (Place) intent.getSerializableExtra("place");
                    Market market = (Market) intent.getSerializableExtra("market");

                    Intent i = new Intent(ProductActivity.this, SuggestProductActivity.class);
                    i.putExtra("barcode", barcode);
                    i.putExtra("place", place);
                    i.putExtra("market", market);
                    i.putExtra("markets", markets);

                    startActivity(i);
                }
            });
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            Product product = null;
            for (Search result : results) {
                if(product == null) {
                    product = result.getProduct();
                    textView1.setText(product.getName() + " - " + product.getBrand());
                    textView2.setText(product.getBarcode());
                }
                Market market = result.getMarket();
                Date lastUpdate = result.getLastUpdate();
                Map<String, Object> map = new HashMap<>();
                map.put("info", market.getName() + " - " + getResources().getString(R.string.currency_activity_product) + " " + result.getPrice());
                map.put("date", getResources().getString(R.string.last_update_activity_product) + ": " + sdf.format(lastUpdate));
                list.add(map);
            }
            BaseAdapter adapter = new SimpleAdapter(this, list,
                    android.R.layout.simple_list_item_2,
                    new String[]{"info", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.white)));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            listView.setDividerHeight(height / 35);
            listView.setBackgroundColor(getResources().getColor(R.color.white));
            listView.setAdapter(adapter);
            button.setText(getResources().getString(R.string.update_activity_product));
            final String productName = product.getName();
            final String productBrand = product.getBrand();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String barcode = String.valueOf(textView2.getText());
                    ArrayList<Place> places = (ArrayList<Place>) intent.getSerializableExtra("places");
                    ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                    Place place = (Place) intent.getSerializableExtra("place");
                    Market market = (Market) intent.getSerializableExtra("market");

                    Intent i = new Intent(ProductActivity.this, SuggestProductActivity.class);
                    i.putExtra("barcode", barcode);
                    i.putExtra("productName", productName);
                    i.putExtra("productBrand", productBrand);
                    i.putExtra("place", place);
                    i.putExtra("market", market);
                    i.putExtra("markets", markets);

                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
