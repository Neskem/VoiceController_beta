package com.example.shihjie_sun.voicecontroller_beta;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemActivity;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.Features.ColorActivity;
import com.example.shihjie_sun.voicecontroller_beta.Features.Colors;
import com.example.shihjie_sun.voicecontroller_beta.Features.FileUtil;
import com.example.shihjie_sun.voicecontroller_beta.Features.MapsActivity;
import com.example.shihjie_sun.voicecontroller_beta.Features.PictureActivity;
import com.example.shihjie_sun.voicecontroller_beta.Features.PlayActivity;
import com.example.shihjie_sun.voicecontroller_beta.Features.RecordActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by ShihJie_Sun on 2016/1/8.
 */
public class ItemActivityFragment extends Fragment {
    private RelativeLayout relativeLayout;

    private EditText title_text, content_text;

    private static final int START_CAMERA = 0;
    private static final int START_RECORD = 1;
    private static final int START_LOCATION = 2;
    private static final int START_ALARM = 3;
    private static final int START_COLOR = 4;

    public Item item;
    private String fileName;
    private String recFileName;
    public File recordFile;
    private ImageView picture;
    private Button submit, cancel;
    private ImageButton take_picture, record_sound, play_sound, set_location, set_alarm, select_color;
    private TextView address_text;
    private String address;

    private int position = -1;
    public ItemAdapter itemAdapter;
    private ItemDAO itemDAO;
    public List<Item> items;
    Handler handler;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstance) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_item, container, false);
        processView();
/*
        Intent intent = getActivity().getIntent();
        // 讀取Action名稱
        String action = intent.getAction();

        Bundle bundle = new Bundle();
        item = (Item) bundle.getSerializable("com.example.shihjie_sun.voicecontroller_beta.Database.Item");
        Log.d("item", "value:" + item);

        title_text.setText(item.getTitle());
        content_text.setText(item.getContent());

*/
        itemDAO = new ItemDAO(getActivity().getApplicationContext());

        MyTask task = new MyTask();
        try {
            items =  task.execute(itemDAO).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("items","value:" + items);

        itemAdapter = new ItemAdapter(getActivity(), R.layout.single_item, items);

        getBundle();

        if (item != null) {
            title_text.setText(item.getTitle());
            content_text.setText(item.getContent());
        if (item.getLatitude()!= 0 & item.getLongitude() !=0) {
            address = getAddressByLocation(item.getLatitude(), item.getLongitude());
            address_text.setText(address);
        }

        } else {
            item = new Item();
            // 建立SharedPreferences物件
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            // 讀取設定的預設顏色
            int color = sharedPreferences.getInt("DEFAULT_COLOR", -1);
            item.setColor(getColors(color));
        }

        //handler = new Handler();
        //handler.post(r1);

        return relativeLayout;
    }

    public void getBundle() {

        position = getArguments().getInt("position");
        Log.d("position", "value:" + position);
        if(position !=-1) {
            item = itemAdapter.getItem(position);
        } else {
            item = null;
        }

    }

    class MyTask extends AsyncTask<ItemDAO, Void, List<Item>> {
        private List<Item> item;

        @Override
        protected List<Item> doInBackground(ItemDAO... params) {
            if (itemDAO.getCount() == 0) {
                itemDAO.sample();
            }

            item = itemDAO.getAll();

            return item;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        handler = new Handler();
        handler.post(r1);

        //SetPhotoThread thread = new SetPhotoThread();
        //thread.run();
/*
        // 如果有檔案名稱
        if (item.getFileName() != null && item.getFileName().length() > 0) {

            File file = configFileName("P", ".jpg");

            if (file.exists()) {

                picture.setVisibility(View.VISIBLE);
                FileUtil.fileToImageView(file.getAbsolutePath(), picture);
            }
        }
*/

    }

    private Runnable r1 = new Runnable() {
        @Override
        public void run() {
            if (item.getFileName() != null && item.getFileName().length() > 0) {

                File file = configFileName("P", ".jpg");

                if (file.exists()) {

                    picture.setVisibility(View.VISIBLE);
                    FileUtil.fileToImageView(file.getAbsolutePath(), picture);
                }
            }
        }
    };

    public class SetPhotoThread extends Thread {
        public void run() {
            if (item.getFileName() != null && item.getFileName().length() > 0) {

                File file = configFileName("P", ".jpg");    ///

                if (file.exists()) {        //

                    picture.setVisibility(View.VISIBLE);
                    FileUtil.fileToImageView(file.getAbsolutePath(), picture);      //
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // camera
                case START_CAMERA:
                    // set files name
                    item.setFileName(fileName);
                    break;
                case START_RECORD:
                    // record
                    item.setRecFileName(fileName);
                    break;
                case START_LOCATION:
                    // location
                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lng = data.getDoubleExtra("lng", 0.0);
                    item.setLatitude(lat);
                    item.setLongitude(lng);
                    break;
                case START_ALARM:
                    break;
                // color
                case START_COLOR:
                    int colorId = data.getIntExtra(
                            "colorId", Colors.LIGHTGREY.parseColor());
                    item.setColor(getColors(colorId));
                    break;
            }
        }
    }

    public static Colors getColors(int color) {
        Colors result = Colors.LIGHTGREY;

        if (color == Colors.BLUE.parseColor()) {
            result = Colors.BLUE;
        } else if (color == Colors.PURPLE.parseColor()) {
            result = Colors.PURPLE;
        } else if (color == Colors.GREEN.parseColor()) {
            result = Colors.GREEN;
        } else if (color == Colors.ORANGE.parseColor()) {
            result = Colors.ORANGE;
        } else if (color == Colors.RED.parseColor()) {
            result = Colors.RED;
        }

        return result;
    }

    private void processView() {
        title_text = (EditText) relativeLayout.findViewById(R.id.title_text);
        content_text = (EditText) relativeLayout.findViewById(R.id.content_text);
        picture = (ImageView) relativeLayout.findViewById(R.id.picture);

        submit = (Button) relativeLayout.findViewById(R.id.ok_teim);
        cancel = (Button) relativeLayout.findViewById(R.id.cancel_item);

        take_picture = (ImageButton) relativeLayout.findViewById(R.id.take_picture);
        record_sound = (ImageButton) relativeLayout.findViewById(R.id.record_sound);
        play_sound = (ImageButton) relativeLayout.findViewById(R.id.play_sound);
        set_location = (ImageButton) relativeLayout.findViewById(R.id.set_location);
        set_alarm = (ImageButton) relativeLayout.findViewById(R.id.set_alarm);
        select_color = (ImageButton) relativeLayout.findViewById(R.id.select_color);
        address_text = (TextView) relativeLayout.findViewById(R.id.address_text);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PictureActivity.class);

                // 設定圖片檔案名稱
                intent.putExtra("pictureName", configFileName("P", ".jpg").getAbsolutePath());

                // 如果裝置的版本是LOLLIPOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(getActivity(), picture, "picture");
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = title_text.getText().toString();
                String contentText = content_text.getText().toString();

                item.setTitle(titleText);
                item.setContent(contentText);

                if (item != null) {
                    item.setLastModify(new Date().getTime());
                }
                // 新增記事
                else {
                    item.setDatetime(new Date().getTime());
                }

                //Intent result = getActivity().getIntent();
                //result.putExtra("com.example.shihjie_sun.voicecontroller_beta.Database.Item", item);

                //getActivity().setResult(Activity.RESULT_OK, result);
                //startActivityForResult(result, getActivity().RESULT_FIRST_USER);

                Log.d("getid", "value:" +item.getId());
                Log.d("getcount", "value:" +itemDAO.getCount());

                if(item.getId() == 0) {
                    itemDAO.insert(item);
                } else {
                    itemDAO.update(item);
                }

                if(getActivity().findViewById(R.id.MainActivityUI) != null) {

                    MainActivityFragment firstFragment = new MainActivityFragment();
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.MainActivityUI, firstFragment).commit();
                    ((Main_Activity)getActivity()).updateFragment();
                    getActivity().getFragmentManager().beginTransaction().remove(ItemActivityFragment.this).commit();
                } else {

                    MainActivityFragment firstFragment = new MainActivityFragment();

                    //getActivity().getFragmentManager().beginTransaction().remove(firstFragment).commit();
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.headlines_fragment, firstFragment).commit();
                    getActivity().getFragmentManager().beginTransaction().remove(ItemActivityFragment.this).commit();
                }

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().remove(ItemActivityFragment.this).commit();
            }
        });

        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File pictureFile = configFileName("P", ".jpg");
                Uri uri = Uri.fromFile(pictureFile);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentCamera, START_CAMERA);
            }
        });

        record_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFile = configRecFileName("R", ".mp3");

                if (recordFile.exists()) {

                    AlertDialog.Builder d = new AlertDialog.Builder(getActivity());

                    d.setTitle(R.string.title_record)
                            .setCancelable(false);
                    d.setPositiveButton(R.string.record_play,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent playIntent = new Intent(
                                            getActivity(), PlayActivity.class);
                                    playIntent.putExtra("fileName",
                                            recordFile.getAbsolutePath());
                                    startActivity(playIntent);
                                }
                            });
                    d.setNeutralButton(R.string.record_new,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToRecord(recordFile);
                                }
                            });
                    d.setNegativeButton(android.R.string.cancel, null);

                    d.show();
                }

                else {
                    goToRecord(recordFile);
                }
            }
        });

        play_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordFile!= null) {
                    Intent playIntent = new Intent(
                            getActivity(), PlayActivity.class);

                    playIntent.putExtra("fileName",
                            recordFile.getAbsolutePath());
                    startActivity(playIntent);

                } else {
                    Toast.makeText(getActivity(), "尚未錄音!", Toast.LENGTH_LONG).show();
                }

            }
        });

        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent(getActivity(), MapsActivity.class);

                intentMap.putExtra("lat", item.getLatitude());
                intentMap.putExtra("lng", item.getLongitude());
                intentMap.putExtra("title", item.getTitle());
                intentMap.putExtra("datetime", item.getLocaleDatetime());

                startActivityForResult(intentMap, START_LOCATION);
            }
        });

        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSetAlarm();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getActivity(), ColorActivity.class), START_COLOR);
            }
        });
    }

    private void processSetAlarm() {
        Calendar calendar = Calendar.getInstance();

        if (item.getAlarmDatetime() != 0) {
            // 設定為已經儲存的提醒日期時間
            calendar.setTimeInMillis(item.getAlarmDatetime());
        }

        // 讀取年、月、日、時、分
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 儲存設定的提醒日期時間
        final Calendar alarm = Calendar.getInstance();

        // 設定提醒時間
        TimePickerDialog.OnTimeSetListener timeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        alarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alarm.set(Calendar.MINUTE, minute);

                        item.setAlarmDatetime(alarm.getTimeInMillis());
                    }
                };

        // 選擇時間對話框
        final TimePickerDialog tpd = new TimePickerDialog(
                this.getActivity(), timeSetListener, hour, minute, true);

        // 設定提醒日期
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view,
                                          int year,
                                          int monthOfYear,
                                          int dayOfMonth) {
                        alarm.set(Calendar.YEAR, year);
                        alarm.set(Calendar.MONTH, monthOfYear);
                        alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // 繼續選擇提醒時間
                        tpd.show();
                    }
                };

        // 建立與顯示選擇日期對話框
        final DatePickerDialog dpd = new DatePickerDialog(
                this.getActivity(), dateSetListener, year, month, day);
        dpd.show();
    }

    private void goToRecord(File recordFile) {
        // 錄音
        Intent recordIntent = new Intent(this.getActivity(), RecordActivity.class);
        recordIntent.putExtra("fileName", recordFile.getAbsolutePath());
        startActivityForResult(recordIntent, START_RECORD);
    }

    private File configFileName(String prefix, String extension) {
        // 如果記事資料已經有檔案名稱
        if (item.getFileName() != null && item.getFileName().length() > 0) {
            fileName = item.getFileName();
        }
        // 產生檔案名稱
        else {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),        //
                prefix + fileName + extension);
    }

    private File configRecFileName(String prefix, String extension) {
        // 如果記事資料已經有檔案名稱
        if (item.getRecFileName() != null && item.getRecFileName().length() > 0) {
            recFileName = item.getRecFileName();
        }
        // 產生檔案名稱
        else {
            recFileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + recFileName + extension);
    }

    public String getAddressByLocation(double lat, double lng) {
        String returnAddress = "";
        try {
            if (lat != 0 && lng !=0 ) {

                Geocoder gc = new Geocoder(this.getActivity(), Locale.TRADITIONAL_CHINESE);

                List<Address> lstAddress = gc.getFromLocation(lat, lng, 1);
                returnAddress = lstAddress.get(0).getAddressLine(0);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
    }



}
