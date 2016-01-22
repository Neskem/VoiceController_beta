package com.example.shihjie_sun.voicecontroller_beta;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ShihJie_Sun on 2015/12/22.
 */
public class ItemSearch extends Activity {
    private EditText menu_search;
    private ImageButton menu_back;
    private Button menu_searchbutton;

    private ItemDAO itemDAO;

    private List<Item> items;


    String[] title;
    String[] content;
    int j;

    Item item;

    private ListView item_list;
    private ItemAdapter itemAdapter;

    private int selectedCount = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        processViews();
/*
        itemDAO = new ItemDAO(getApplicationContext());

        if (itemDAO.getCount() == 0) {
            itemDAO.sample();
        }

       // items = itemDAO.getAll();

        title = itemDAO.myNote_title();
*/
        AsynTask task = new AsynTask();
        try {
            title =  task.execute(itemDAO).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("item_title", "title_value:" + title[0]);
        //Log.d("item_content", "content_value:" + content);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    class AsynTask extends AsyncTask<ItemDAO, Void, String[]> {
        String[] titlee;

        @Override
        protected String[] doInBackground(ItemDAO... params) {
            itemDAO = new ItemDAO(getApplicationContext());
            if (itemDAO.getCount() == 0) {
                itemDAO.sample();
            }

            titlee = itemDAO.myNote_title();
            return titlee;
        }

        protected void onPostExecute(String[] titlee) {
            this.titlee = titlee;

        }
    }



    private void processViews() {
        menu_search = (EditText) findViewById(R.id.menu_search);
        menu_searchbutton = (Button) findViewById(R.id.menu_searchbutton);
        menu_back = (ImageButton) findViewById(R.id.menu_back);
        item_list = (ListView) findViewById(R.id.listView);

        // 隱藏狀態列ProgressBar
        setProgressBarIndeterminateVisibility(false);
    }

    public void onSearch(View view) {
        Log.d("Search","Runnnnnnnnn");
        String search = menu_search.getText().toString();
        boolean j ;
        int k = 0;
        List<Item> search_items = null;
        Log.d("search","textstttttttttttttttttt:" +search);

        for (int i = 0; i < itemDAO.getCount(); i++) {
            if (title[i].equals(search)) {
                j = (title[i].equals(search));
                //search_items = itemDAO.getOne(i+1);
                search_items = itemDAO.getOne_test(search);
                Log.d("bundle","bundle.Correcttttttttttttttttttt:" +j);
                Log.d("bundle","bundle.lissssssssssssssssssssssssssssssssst:" +search_items);

                k++;

            }

            itemAdapter = new ItemAdapter(this, R.layout.single_item, search_items);

        }

        if (k > 0 ) {

            item_list.setAdapter(itemAdapter);
            processControllers();
        } else {
            Toast.makeText(this,"This Title is not exist!",Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Item item = (Item) data.getExtras().getSerializable(
                    "com.example.shihjie_sun.voicecontroller_beta.Database.Item");

            // 是否修改提醒設定
            boolean updateAlarm = false;

            if (requestCode == 0) {
                // 新增記事資料到資料庫
                item = itemDAO.insert(item);

                items.add(item);
                itemAdapter.notifyDataSetChanged();
            }
            else if (requestCode == 1) {
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    // 讀取原來的提醒設定
                    Item ori = itemDAO.get(item.getId());
                    // 判斷是否需要設定提醒
                    updateAlarm = (item.getAlarmDatetime() != ori.getAlarmDatetime());

                    // 修改資料庫中的記事資料
                    itemDAO.update(item);

                    items.set(position, item);
                    itemAdapter.notifyDataSetChanged();
                }
            }

            // 設定提醒
            if (item.getAlarmDatetime() != 0 && updateAlarm) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                //intent.putExtra("title", item.getTitle());

                // 加入記事編號
                intent.putExtra("id", item.getId());

                PendingIntent pi = PendingIntent.getBroadcast(
                        this, (int)item.getId(),
                        intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager am = (AlarmManager)
                        getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, item.getAlarmDatetime(), pi);
            }
        }
    }

    private void processControllers() {

        // 建立選單項目點擊監聽物件
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 讀取選擇的記事物件
                Item item = itemAdapter.getItem(position);

                // 如果已經有勾選的項目
                if (selectedCount > 0) {
                    // 處理是否顯示已選擇項目
                    itemAdapter.set(position, item);
                }
                else {
                    Intent intent = new Intent(
                            "com.example.shihjie_sun.voicecontroller_beta.Database.EDIT_ITEM");

                    // 設定記事編號與記事物件
                    intent.putExtra("position", position);
                    intent.putExtra("com.example.shihjie_sun.voicecontroller_beta.Database.Item", item);

                    startActivityForResult(intent, 1);
                }
            }
        };

        // 註冊選單項目點擊監聽物件
        item_list.setOnItemClickListener(itemListener);

        // 建立記事項目長按監聽物件
        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // 讀取選擇的記事物件
                Item item = itemAdapter.getItem(position);
                // 處理是否顯示已選擇項目
                itemAdapter.set(position, item);
                return true;
            }
        };

        // 註冊記事項目長按監聽物件
        item_list.setOnItemLongClickListener(itemLongListener);

        // 建立長按監聽物件
        View.OnLongClickListener listener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog =
                        new AlertDialog.Builder(ItemSearch.this);
                dialog.setTitle(R.string.app_name)
                        .setMessage(R.string.about)
                        .show();
                return false;
            }

        };
    }


    public void clickRevert(View view) {
        Intent intent = new Intent();
        intent.setClass(ItemSearch.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ItemSearch Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.shihjie_sun.voicecontroller_beta/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ItemSearch Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.shihjie_sun.voicecontroller_beta/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
