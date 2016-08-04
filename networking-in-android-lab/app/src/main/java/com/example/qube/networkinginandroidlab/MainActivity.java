package com.example.qube.networkinginandroidlab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button mCereal, mChocolate, mTea;
    ListView mListView;
    ArrayList<String> mProductsList;
    ArrayAdapter mAdapter;
    private TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCereal = (Button) findViewById(R.id.button_cereal);
        mChocolate = (Button) findViewById(R.id.button_chocolate);
        mTea = (Button) findViewById(R.id.button_tea);

        mListView = (ListView) findViewById(R.id.list_view);
        mProductsList = new ArrayList<>();

        mAdapter = new ArrayAdapter(
                MainActivity.this, android.R.layout.simple_list_item_1,
                android.R.id.text1, mProductsList);

        mListView.setAdapter(mAdapter);

        mCereal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductsList.clear();
                //TODO: fill this in after finished with connection
                new DownloadTask().execute(
                        "http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=2csz8q9tpd39qp6ccm9wyyp7\n"
                );
            }
        });

        mChocolate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductsList.clear();
                //TODO: fill this in after finished with connection
                new DownloadTask().execute(
                        "http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=2csz8q9tpd39qp6ccm9wyyp7"
                );
            }
        });

        mTea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductsList.clear();
                //TODO: fill this in after finished with connection
                new DownloadTask().execute(
                        "http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=2csz8q9tpd39qp6ccm9wyyp7"
                );
            }
        });

        //Working on connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //connection available
            Toast.makeText(MainActivity.this, "Network is ative", Toast.LENGTH_SHORT).show();
        } else {
            //connection unavailable
            Toast.makeText(MainActivity.this, "Internet not working", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadUrl(String myUrl) throws IOException, JSONException {
        InputStream is = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            is = connection.getInputStream();

            String contentAsString = readIt(is);
            parseJson(contentAsString);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String readIt(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String read;

        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        return sb.toString();
    }

    private void parseJson(String contentAsString) throws JSONException {
        JSONObject search = new JSONObject(contentAsString);
        JSONArray items = search.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            mProductsList.add(item.getString("name"));
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                downloadUrl(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }
}

