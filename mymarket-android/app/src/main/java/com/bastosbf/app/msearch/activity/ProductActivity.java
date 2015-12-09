package com.bastosbf.app.msearch.activity;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bastosbf.app.msearch.R;
import com.bastosbf.app.msearch.model.Market;
import com.bastosbf.app.msearch.model.Product;
import com.bastosbf.app.msearch.model.Search;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private TextView textView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        ArrayList<Search> results = (ArrayList<Search>) intent.getSerializableExtra("results");
        List<Map<String, Object>> list = new ArrayList<>();
        for(Search result : results) {
            Product product = result.getProduct();
            Market market = result.getMarket();
            Date lastUpdate = result.getLastUpdate();
            textView.setText(product.getName() + " - " + product.getBrand());
            Map<String, Object> map = new HashMap<>();
            map.put("info", market.getName() + " - R$ " + result.getPrice());
            map.put("date", "Última atualização: " + sdf.format(lastUpdate));
            list.add(map);
        }
        BaseAdapter adapter = new SimpleAdapter(this,list,
                android.R.layout.simple_list_item_2,
                new String[]{"info", "date"},
                new int[]{android.R.id.text1,android.R.id.text2});
        listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.white)));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        listView.setDividerHeight(height / 35);
        listView.setBackgroundColor(getResources().getColor(R.color.white));
        listView.setAdapter(adapter);
    }
}