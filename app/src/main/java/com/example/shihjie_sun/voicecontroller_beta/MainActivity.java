package com.example.shihjie_sun.voicecontroller_beta;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.Features.AboutActivity;
import com.example.shihjie_sun.voicecontroller_beta.Features.PrefActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusShare;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {

    private ListView item_list;
    private TextView show_app_name;


    private ItemAdapter itemAdapter;
    private List<Item> items;

    private MenuItem add_item, search_item, revert_item, share_item, delete_item;

    private int selectedCount = 0;

    private ItemDAO itemDAO;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // BackgroundTask task = new BackgroundTask();
        //task.execute(itemDAO);

        processViews();
        processControllers();

        AsynTask task = new AsynTask();
        try {
            items =  task.execute(itemDAO).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        itemAdapter = new ItemAdapter(this, R.layout.single_item, items);
        item_list.setAdapter(itemAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    class AsynTask extends AsyncTask<ItemDAO, Void, List<Item>> {
        private List<Item> item;

        @Override
        protected List<Item> doInBackground(ItemDAO... params) {
            itemDAO = new ItemDAO(getApplicationContext());
            if (itemDAO.getCount() == 0) {
                itemDAO.sample();
            }

            item = itemDAO.getAll();
            return item;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Item item = (Item) data.getExtras().getSerializable(
                    "com.example.shihjie_sun.voicecontroller_beta.Database.Item");

            boolean updateAlarm = false;

            if (requestCode == 0) {

                item = itemDAO.insert(item);

                items.add(item);
                itemAdapter.notifyDataSetChanged();
            } else if (requestCode == 1) {
                int position = data.getIntExtra("position", -1);

                if (position != -1) {

                    Item ori = itemDAO.get(item.getId());

                    updateAlarm = (item.getAlarmDatetime() != ori.getAlarmDatetime());

                    itemDAO.update(item);

                    items.set(position, item);
                    itemAdapter.notifyDataSetChanged();
                }
            }

            // 設定提醒
            if (item.getAlarmDatetime() != 0 && updateAlarm) {
                Intent intent = new Intent(this, AlarmReceiver.class);

                intent.putExtra("id", item.getId());

                PendingIntent pi = PendingIntent.getBroadcast(
                        this, (int) item.getId(),
                        intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager am = (AlarmManager)
                        getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, item.getAlarmDatetime(), pi);
            }
        }
    }


    private void processViews() {
        item_list = (ListView) findViewById(R.id.item_list);
        show_app_name = (TextView) findViewById(R.id.show_app_name);

    }

    private void processControllers() {



        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Item item = itemAdapter.getItem(position);

                if (selectedCount > 0) {

                    processMenu(item);

                    itemAdapter.set(position, item);
                } else {
                    Intent intent = new Intent(
                            "com.example.shihjie_sun.voicecontroller_beta.Database.EDIT_ITEM");


                    intent.putExtra("position", position);
                    intent.putExtra("com.example.shihjie_sun.voicecontroller_beta.Database.Item", item);

                    startActivityForVersion(intent, 1);
                }
            }
        };


        item_list.setOnItemClickListener(itemListener);


        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Item item = itemAdapter.getItem(position);

                processMenu(item);

                itemAdapter.set(position, item);
                return true;
            }
        };


        item_list.setOnItemLongClickListener(itemLongListener);


        View.OnLongClickListener listener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog =
                        new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(R.string.app_name)
                        .setMessage(R.string.about)
                        .show();
                return false;
            }

        };

        show_app_name.setOnLongClickListener(listener);
    }


    private void processMenu(Item item) {

        if (item != null) {

            item.setSelected(!item.isSelected());


            if (item.isSelected()) {
                selectedCount++;
            } else {
                selectedCount--;
            }
        }


        add_item.setVisible(selectedCount == 0);
        search_item.setVisible(selectedCount == 0);
        revert_item.setVisible(selectedCount > 0);
        share_item.setVisible(selectedCount > 0);
        delete_item.setVisible(selectedCount > 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);


        add_item = menu.findItem(R.id.add_item);
        search_item = menu.findItem(R.id.search_item);
        revert_item = menu.findItem(R.id.revert_item);
        share_item = menu.findItem(R.id.share_item);
        delete_item = menu.findItem(R.id.delete_item);


        processMenu(null);

        return true;
    }


    public void clickMenuItem(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.search_item:
                Intent intent1 = new Intent(this, ItemSearch.class);
                startActivity(intent1);
                //setDefaultKeyMode(DEFAULT_KEYS_SEARCH_GLOBAL);
                //onSearchRequested();
                break;

            case R.id.add_item:

                Intent intent2 = new Intent("com.example.shihjie_sun.voicecontroller_beta.Database.ADD_ITEM");

                startActivityForResult(intent2, 0);
                break;

            case R.id.revert_item:
                for (int i = 0; i < itemAdapter.getCount(); i++) {
                    Item ri = itemAdapter.getItem(i);

                    if (ri.isSelected()) {
                        ri.setSelected(false);
                        itemAdapter.set(i, ri);
                    }
                }

                selectedCount = 0;
                processMenu(null);

                break;

            case R.id.delete_item:
                if (selectedCount == 0) {
                    break;
                }

                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_item);
                d.setTitle(R.string.delete)
                        .setMessage(String.format(message, selectedCount));
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int index = itemAdapter.getCount() - 1;

                                while (index > -1) {
                                    Item item = itemAdapter.get(index);

                                    if (item.isSelected()) {
                                        itemAdapter.remove(item);
                                        // 刪除資料庫中的記事資料
                                        itemDAO.delete(item.getId());
                                    }

                                    index--;
                                }

                                itemAdapter.notifyDataSetChanged();
                                selectedCount = 0;
                                processMenu(null);
                            }
                        });
                d.setNegativeButton(android.R.string.no, null);
                d.show();

                break;
            case R.id.googleplus_item:
                // Launch the Google+ share dialog with attribution to your app.

                int index = itemAdapter.getCount() - 1;
                String google = "";

                while (index > -1) {
                    Item g = itemAdapter.get(index);
                    String title = g.getTitle();
                    String content = g.getContent();


                    if (g.isSelected()) {
                        google += title + ":  " + content + "\n";
                    }

                    index--;
                }

                itemAdapter.notifyDataSetChanged();
                selectedCount = 0;
                processMenu(null);

                Intent shareIntent = new PlusShare.Builder(this)
                        .setType("text/plain")
                        .setText("Welcome to the Google+ platform."+"\n"+ google +"\n")
                        .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                        .getIntent();
                startActivity (shareIntent);

                break;
            case R.id.facebook_item:
                break;
        }

    }

    public void aboutApp(View view) {

        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }


    public void clickPreferences(MenuItem item) {

        startActivity(new Intent(this, PrefActivity.class));
    }

    @Override
    public boolean onSearchRequested() {
        // TODO Auto-generated method stub

        Log.d("Test", "onSearchRequested()");

        startSearch("請輸入關鍵字", false, null, true);

        return true;
    }

    private void startActivityForVersion(Intent intent, int requestCode) {
        // Version is LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            startActivityForResult(intent, requestCode,
                    ActivityOptions.makeSceneTransitionAnimation(
                            MainActivity.this).toBundle());
        } else {
            startActivityForResult(intent, requestCode);
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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
