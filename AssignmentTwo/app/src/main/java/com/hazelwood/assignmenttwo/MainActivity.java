package com.hazelwood.assignmenttwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends Activity {
    String _title, _date, _byLine, _description, _imageURL, _webURL;
    ArrayList<NYTimes> newsArray;
    Spinner spinnerOne, spinnerTwo;
    ListView listView;
    Dialog dialog;
    Button search, searchAgain;
    Network task;
    ProgressDialog progressDialog;
    String jsonString, url;
    EditText section;
    NYTimesAdapter adapter;
    String[] sectionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new Network();

        newsArray = new ArrayList<NYTimes>();

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("Search");
        dialog.setCanceledOnTouchOutside(false);

        spinnerOne = (Spinner)dialog.findViewById(R.id.sectionSpinner);
        spinnerTwo = (Spinner)dialog.findViewById(R.id.timePeriodSpinner);

        section = (EditText)dialog.findViewById(R.id.sectionText);

        sectionArray = new String[] {
                "All Sections",
                "Arts",
                "Automobiles",
                "Blogs",
                "Books",
                "Business Day",
                "Education",
                "Fashion & Style",
                "Great Homes & Destinations",
                "Health",
                "Home & Garden",
                "Magazine",
                "Movies",
                "Multimedia",
                "Opinion",
                "Public Editor",
                "Real Estate",
                "Science",
                "Sports",
                "Style",
                "Sunday Review",
                "Technology",
                "The Upshot",
                "Theater",
                "Travel",
                "U.S.",
                "World",
                "Your Money"};
        String[] spinnerTwoList = new String[] {"1", "7", "30"};
        ArrayAdapter<String> adapterSpinnerOne = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,sectionArray);
        ArrayAdapter<String> adapterSpinnerTwo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerTwoList);
        spinnerOne.setAdapter(adapterSpinnerOne);
        spinnerTwo.setAdapter(adapterSpinnerTwo);
        spinnerOne.setOnItemSelectedListener(itemClick);

        search = (Button)dialog.findViewById(R.id.searchButton);
        searchAgain = (Button)findViewById(R.id.searchAgainButton);
        searchAgain.setOnClickListener(click);
        search.setOnClickListener(click);
        dialog.show();
    }
    AdapterView.OnItemSelectedListener itemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            section.setText(spinnerOne.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private class Network extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle((ProgressDialog.STYLE_HORIZONTAL));
            progressDialog.setMessage("Loading " + spinnerOne.getSelectedItem().toString() + "...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressNumberFormat("Copyright (c) 2014 The New York Times Company. All Rights Reserved.");
            progressDialog.setProgressPercentFormat(null);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                //URL connection check
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                jsonString = IOUtils.toString(is);
                is.close();
                connection.disconnect();
            }catch (MalformedURLException e){
                e.printStackTrace();

            }catch (IOException e) {
                e.printStackTrace();
            }

            try{
                //JSON
                JSONObject outerObject = new JSONObject(jsonString);
                JSONArray items = outerObject.getJSONArray("results");

                for (int i = 0; i < items.length(); i++){
                    JSONObject news = items.getJSONObject(i);
                    if (news.has("title")){
                        _title = news.getString("title");
                    }
                    if (news.has("byline")){
                        _byLine = news.getString("byline");
                    }
                    if (news.has("published_date")){
                        _date = news.getString("published_date");
                    }
                    if (news.has("abstract")){
                        _description = news.getString("abstract");
                    }
                    if (news.has("url")){
                        _webURL = news.getString("url");
                    }
                    if (news.has("media")){
//                        JSONArray test = news.getJSONArray("media");
//                        Log.d("THis", "" + test);
//                        for (int j = 0; j < test.length(); j++ ){
//                            JSONObject metadata = test.getJSONObject(0);
//                            if (metadata.has("format")){
//                                String format = metadata.getString("format");
//                                if (format.equals("Standard Thumbnail")){
//                                    _imageURL = metadata.getString("url");
//
//                                }else if (format.equals("thumbLarge")){
//                                    _imageURL = metadata.getString("url");
//
//                                }else if (format.equals("mediumThreeByTwo210")){
//                                    _imageURL = metadata.getString("url");
//
//                                }
//                            }
//                        }
                    }
                    newsArray.add(new NYTimes(_title,_byLine,_description,_imageURL));
                }



            }catch (JSONException e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            adapter = new NYTimesAdapter(MainActivity.this, newsArray);
            listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
            progressDialog.dismiss();

        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.searchButton:
                    try{
                        url = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/"+ URLEncoder.encode(section.getText().toString(), "UTF-8") + "/"+ URLEncoder.encode(spinnerTwo.getSelectedItem().toString(), "UTF-8") +".json?api-key=" + URLEncoder.encode("96b25181c8c0c478f8a1a5e5c71226a8:1:68792990", "UTF-8");

                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    if (isConnected()){
                            if(Arrays.asList(sectionArray).contains(section.getText().toString())){
                                task.execute(url);
                                dialog.dismiss();

                            }else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("ATTENTION!");
                                builder.setMessage("Section not found.");
                                builder.setCancelable(true);
                                builder.show();

                            }
                    }
                    else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("ATTENTION!");
                        builder.setMessage("Problem with network connection.");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isConnected()) {
                                    try {
                                        task.execute("http://api.nytimes.com/svc/mostpopular/v2/mostviewed/" + URLEncoder.encode(spinnerOne.getSelectedItem().toString(), "UTF-8") + "/" + URLEncoder.encode(spinnerTwo.getSelectedItem().toString(), "UTF-8") + ".json?api-key=96b25181c8c0c478f8a1a5e5c71226a8:1:68792990");

                                    }catch (UnsupportedEncodingException e){
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    builder.show();
                                }
                            }
                        });

                        builder.show();
                    }
                    break;
                case R.id.searchAgainButton:
                    task = new Network();
                    dialog.show();
                    newsArray.clear();
                    adapter.notifyDataSetChanged();
                    break;
            }



        }
    };

    private boolean isConnected(){
        ConnectivityManager mgr = (ConnectivityManager)getSystemService((Context.CONNECTIVITY_SERVICE));

        if (mgr != null){
            NetworkInfo info = mgr.getActiveNetworkInfo();

            if (info != null && info.isConnected()){
                return true;
            }
        }
        return false;
    }


}
