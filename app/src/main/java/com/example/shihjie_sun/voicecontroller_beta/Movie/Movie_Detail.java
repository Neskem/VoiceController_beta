package com.example.shihjie_sun.voicecontroller_beta.Movie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.MySQL.App.AppConfig;
import com.example.shihjie_sun.voicecontroller_beta.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShihJie_Sun on 2016/1/7.
 */
public class Movie_Detail extends Activity {

    TextView movie_string;

    // title
    String title;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String movie_detials = "http://10.78.21.36/Movie/Get_Movie_Details.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_CONTENT = "content";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        movie_string = (TextView) findViewById(R.id.Movie_String);

        // Loader image - will be shown before loading image

        int loader = R.drawable.loader;

        // Imageview to show
        ImageView image = (ImageView) findViewById(R.id.image);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String pic = bundle.getString("picture");
        Log.d("pic", "value:" + pic);

        // Image url
        String image_url = AppConfig.MOVIE_PICTURE + pic + ".jpg";

        // ImageLoader class instance
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());

        // whenever you want to load an image from url
        // call DisplayImage function
        // url - image url to load
        // loader - loader image, will be displayed before getting image
        // image - ImageView

        imgLoader.DisplayImage(image_url, loader, image);
///////////////////////////////////////////////////////////////////////////////////

        title = "123";

        GetProductDetails task = new GetProductDetails();
        task.execute();




    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Movie_Detail.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("title", title));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                movie_detials, "POST", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray movieObj = json
                                    .getJSONArray(TAG_MOVIE); // JSON Array

                            // get first product object from JSON Array
                            JSONObject movie = movieObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            movie_string = (TextView) findViewById(R.id.Movie_String);

                            // display product data in EditText
                            movie_string.setText(movie.getString(TAG_CONTENT));


                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

}

