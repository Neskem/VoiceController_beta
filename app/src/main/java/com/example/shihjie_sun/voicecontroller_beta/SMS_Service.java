package com.example.shihjie_sun.voicecontroller_beta;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.shihjie_sun.voicecontroller_beta.Database.BackupContentProvider;
import com.example.shihjie_sun.voicecontroller_beta.Database.BackupDAO;
import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.Database.MyDBHelper;
import com.example.shihjie_sun.voicecontroller_beta.Features.Colors;
import com.example.shihjie_sun.voicecontroller_beta.Movie.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by ShihJie_Sun on 2015/12/21.
 */
public class SMS_Service extends Service {
    public static final String SMS_RECEIVE_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private Item item;
    private ItemDAO db;
    public String titleText,contentText;

    private static final String movie_detials = "http://10.78.21.36/Movie/Get_Movie_Details.php";

    JSONParser jsonParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_REQUEST = "request";
    private static final String TAG_TIME = "time";

    private String email, request;
    private int time;

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SMSreceiver mSmsReceiver = new SMSreceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(SMS_RECEIVE_ACTION);
        this.registerReceiver(mSmsReceiver, filter);

        db = new ItemDAO(getApplicationContext());

        //GetProductDetails task = new GetProductDetails();
        //task.execute();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SMSreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            item = new Item();
            item.setColor(Colors.BLUE);
            item.setDatetime(new Date().getTime());

            if (TextUtils.equals(intent.getAction(), SMS_RECEIVE_ACTION)) {
                //handle sms receive
                Bundle msg = intent.getExtras();
                if (msg != null) {
                    Object[] myObjects = (Object[]) msg.get("pdus");
                    //protocol description units 標準協定
                    //PDU可以是文字或多媒體，用Object陣列全包下來

                    SmsMessage[] messages = new SmsMessage[myObjects.length];

                    for (int i = 0; i < myObjects.length; i++) {

                        messages[i] = SmsMessage.createFromPdu((byte[]) myObjects[i]);
                        //需將PDU格式轉成byte陣列
                        //將PDU格式的資料轉為smsMessage的格式
                        //由於簡訊長度的限制可能不止一封
                    }

                    for(SmsMessage tempMessage : messages) {
                        titleText = tempMessage.getOriginatingAddress();
                        contentText = tempMessage.getMessageBody();

                        Log.d("title: ", "  " + titleText);
                        Log.d("content: ", " " + contentText);

                    }

                        item.setTitle(titleText);
                        item.setContent(contentText);

                        db.insert(item);

                }


            }
        }
    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> param = new ArrayList<NameValuePair>();
                        param.add(new BasicNameValuePair("request", "request"));
                        param.add(new BasicNameValuePair("email", "test_001"));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                movie_detials, "POST", param);

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

                            ////////////////////////////////////

                            request = movie.getString(TAG_REQUEST);
                            time = movie.getInt(TAG_TIME);

                            InsertDB(request, time);

                            ///////////////////////////////////
                        } else {
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

        }


    public void InsertDB(String request, int time) {

        ContentResolver contentresolver = getContentResolver();
        contentresolver.delete(BackupContentProvider.CONTENT_URI, null, null);

        ContentValues values = new ContentValues();

        values.put(ItemDAO.DATETIME_COLUMN, time);
        values.put(ItemDAO.CONTENT_COLUMN, request);

        Log.d("Back_Activity", "error_value:" + values);

        getContentResolver().insert(BackupContentProvider.CONTENT_URI, values);

    }

}
