package com.example.shihjie_sun.voicecontroller_beta.Features;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.Database.BackupContentProvider;
import com.example.shihjie_sun.voicecontroller_beta.Database.BackupDAO;
import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemActivity;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.Database.MyDBHelper;
import com.example.shihjie_sun.voicecontroller_beta.Database.SessionManager;
import com.example.shihjie_sun.voicecontroller_beta.MySQL.App.AppConfig;
import com.example.shihjie_sun.voicecontroller_beta.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by ShihJie_Sun on 2015/12/23.
 */
public class BackActivity extends Activity  {
    private String TAG_LOG = "test";
    private ImageButton button;

    private ItemDAO itemDAO;

    public String[] title;
    public String[] content;
    public Long[] datetime;
    public Long[] color;
    private Long[] key_id;
    private String id;
    public String email;
    public String name;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mhour;
    private int mminute;
    private int msecond;

    private String uri = "vnd.android.cursor.item/vnd.com.example.shihjie_sun.voicecontroller_beta.Database.contentprovider.users";

    String date2;
    String iresult;
    String p;

    private MyDBHelper db;
    private SessionManager session;

    private String srcPath;

    private Item item;

    String[] sql_title, sql_content, sql_color, sql_datetime;

    protected static final int REFRESH_DATA = 0x00000001;

    public String mydate;
    public String mycolor;
    public String mytitle;
    public String mycontent;

    public String[] mdate ;
    public String[] mcolor;
    public String[] mtitle;
    public String[] mcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消元件的應用程式標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_backup);
        button = (ImageButton) findViewById(R.id.back_up_picture);

        AsynTask task = new AsynTask();
        task.execute(itemDAO);
/*
        itemDAO = new ItemDAO(getApplicationContext());

        if (itemDAO.getCount() == 0) {
            itemDAO.sample();
        }

        title = itemDAO.myNote_title();
        content = itemDAO.myNote_content();
        datetime = itemDAO.myNote_datetime();
        color = itemDAO.myNote_color();
        key_id = itemDAO.myNote_id();

        Log.d("Back_Activity", "title_value:" + title[0]);
        Log.d("Back_Activity", "content_value:" + content[0]);
        Log.d("Back_Activity", "datetime_value:" + datetime[0]);
        Log.d("Back_Activity", "color_value:" + color[0]);

        Calendar c = Calendar.getInstance();// 建立抓日期物件c
        mYear = c.get(Calendar.YEAR);// 年
        mMonth = c.get(Calendar.MONTH)+1;// 月
        mDay = c.get(Calendar.DAY_OF_MONTH);// 日
        mhour = c.get(Calendar.HOUR);
        mminute = c.get(Calendar.MINUTE);
        msecond = c.get(Calendar.SECOND);

        db = new MyDBHelper(getApplicationContext(),"mydata.db",null,4);
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        email = user.get("email");
*/
    }

    class AsynTask extends AsyncTask<ItemDAO, Void, Void> {
        private List<Item> item;

        @Override
        protected Void doInBackground(ItemDAO... params) {
            itemDAO = new ItemDAO(getApplicationContext());
            if (itemDAO.getCount() == 0) {
                itemDAO.sample();
            }

            title = itemDAO.myNote_title();
            content = itemDAO.myNote_content();
            datetime = itemDAO.myNote_datetime();
            color = itemDAO.myNote_color();
            key_id = itemDAO.myNote_id();

            Log.d("Back_Activity", "title_value:" + title[0]);
            Log.d("Back_Activity", "content_value:" + content[0]);
            Log.d("Back_Activity", "datetime_value:" + datetime[0]);
            Log.d("Back_Activity", "color_value:" + color[0]);

            Calendar c = Calendar.getInstance();// 建立抓日期物件c
            mYear = c.get(Calendar.YEAR);// 年
            mMonth = c.get(Calendar.MONTH)+1;// 月
            mDay = c.get(Calendar.DAY_OF_MONTH);// 日
            mhour = c.get(Calendar.HOUR);
            mminute = c.get(Calendar.MINUTE);
            msecond = c.get(Calendar.SECOND);

            db = new MyDBHelper(getApplicationContext(),"mydata.db",null,4);
            session = new SessionManager(getApplicationContext());

            HashMap<String, String> user = db.getUserDetails();
            name = user.get("name");
            email = user.get("email");
            return null;
        }
    }


    public void StartBackUp(View v) {

        ContentResolver contentresolver = getContentResolver();
        contentresolver.delete(BackupContentProvider.CONTENT_URI,null,null);

        for(int i=0;i<title.length;i++) {
            ContentValues values = new ContentValues();
            //values.put(BackupDAO.KEY_ID, key_id[i]);
            String titlee = title[i];
            String contentt = content[i];
            Long datetimeee = datetime[i];
            Long colorrr = color[i];

            values.put(BackupDAO.DATETIME_COLUMN, datetimeee);
            values.put(BackupDAO.COLOR_COLUMN, colorrr);
            values.put(BackupDAO.TITLE_COLUMN, titlee);
            values.put(BackupDAO.CONTENT_COLUMN, contentt);
            Log.d("Back_Activity", "error_value:" + values);

            getContentResolver().insert(BackupContentProvider.CONTENT_URI, values);

            //HashMap<String, String> user = db.getUserDetails();
            //String name = user.get("name");
            // String email = user.get("email");
            String datetimee = datetimeee.toString();
            String colorr = colorrr.toString();

            Thread t = new Thread(new sendPostRunnable(
                    email,
                    datetimee,
                    colorr,
                    titlee,
                    contentt));
            t.start();

        }
        finish();
    }

    // get data from backup contentprovider
    private void loadUserInfo(int id) {
    String[] projection = {
            BackupDAO.KEY_ID,
            BackupDAO.DATETIME_COLUMN,
            BackupDAO.COLOR_COLUMN,
            BackupDAO.TITLE_COLUMN,
            BackupDAO.CONTENT_COLUMN
    };

    Uri uri = Uri.parse(BackupContentProvider.CONTENT_URI + "/" + id);  //need key_id to query
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);

            cursor.moveToFirst();
            mydate = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.DATETIME_COLUMN));
            mycolor = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.COLOR_COLUMN));
            mytitle = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.TITLE_COLUMN));
            mycontent = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.CONTENT_COLUMN));


    }

    private void loadUserAll() {
        String[] projection = {
                BackupDAO.KEY_ID,
                BackupDAO.DATETIME_COLUMN,
                BackupDAO.COLOR_COLUMN,
                BackupDAO.TITLE_COLUMN,
                BackupDAO.CONTENT_COLUMN
        };


        Cursor cursor = getContentResolver().query(BackupContentProvider.CONTENT_URI, projection, null, null,
                null);
        int i = 0;
        Log.d("testtttttttttttttttt", "" + projection);
        cursor.moveToFirst();
        do {
            mydate = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.DATETIME_COLUMN));
            mycolor = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.COLOR_COLUMN));
            mytitle = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.TITLE_COLUMN));
            mycontent = cursor.getString(cursor.getColumnIndexOrThrow(BackupDAO.CONTENT_COLUMN));
            i++;
            Log.d(TAG_LOG,""+mytitle);
            item = new Item();
            item.setDatetime(Long.parseLong(mydate));
            item.setColor(ItemActivity.getColors(Integer.parseInt(mycolor)));
            item.setTitle(mytitle);
            item.setContent(mycontent);
            item.setLastModify(new Date().getTime());

            itemDAO.insert(item);
        }while (cursor.moveToNext());

    }


    // old transform function
    private void uploadString(String uploadUrl, String title, String content, Long datetime, Long color) {

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        PrintWriter writer = null;
        String param = p;
        String charset = "UTF-8";

        String datetimee = datetime.toString();
        String colorr = color.toString();

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        Log.d("up1", "Start upload");
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 設置每次傳輸流量，防止內存過大崩潰
            // 此方法用在無法預先知道傳輸大小
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允許傳輸
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("	", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            Log.d("up2", "connection set");

            // value
            OutputStream output = httpURLConnection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, charset),
                    true); // true = autoFlush, important!

            // Send normal param.
            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"param\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(param).append(end).flush();

            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"email\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(email).append(end).flush();

            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"title\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(title).append(end).flush();

            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"content\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(content).append(end).flush();

            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"datetimee\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(datetimee).append(end).flush();

            writer.append("--" + boundary).append(end);
            writer.append("Content-Disposition: form-data; name=\"colorr\"")
                    .append(end);
            writer.append("Content-Type: text/plain; charset=" + charset)
                    .append(end);
            writer.append(end);
            writer.append(colorr).append(end).flush();
/*
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            iresult = br.readLine();
            iresult = iresult.replaceAll("\\s+","");     //去空白
            Log.d("up3", "get result");
            // Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            Log.d("up4", "********************************\nresult:" + iresult);

            is.close();
*/
        } catch (Exception e) {
            e.printStackTrace();
            // setTitle(e.getMessage());
        }

    }

    public void StartRecovery(View v) {
        itemDAO.deleteAll();

        loadUserAll();

        Toast.makeText(BackActivity.this, "Finish Recovery!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, com.example.shihjie_sun.voicecontroller_beta.Main_Activity.class);
        startActivityForResult(intent, 0);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null)
                        Toast.makeText(BackActivity.this, "Back up is over!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private String sendPostDataToInternet(String s1 ,String s2 , String s3 , String s4 , String s5){
        HttpPost httpRequest = new HttpPost(AppConfig.URL_UPLOAD_STRING);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", s1));
        params.add(new BasicNameValuePair("datetime", s2));
        params.add(new BasicNameValuePair("color", s3));
        params.add(new BasicNameValuePair("title", s4));
        params.add(new BasicNameValuePair("content", s5));
        try	{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class sendPostRunnable implements Runnable
    {
        String s1 = null;
        String s2 = null;
        String s3 = null;
        String s4 = null;
        String s5 = null;

        public sendPostRunnable(String s1 ,String s2 , String s3 , String s4 , String s5){
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.s4 = s4;
            this.s5 = s5;
        }
        @Override
        public void run() {
            String result = sendPostDataToInternet(s1,s2,s3,s4,s5);
            mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        }
    }
}
