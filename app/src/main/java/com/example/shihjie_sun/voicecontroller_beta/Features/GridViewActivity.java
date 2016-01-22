package com.example.shihjie_sun.voicecontroller_beta.Features;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.R;

/**
 * Created by ShihJie_Sun on 2015/12/24.
 */
public class GridViewActivity extends Activity {

    private GridView gridView;
    private ImageView imageView;
    private List<String> thumbs;  //存放縮圖的id
    private List<String> imagePaths;  //存放圖片的路徑
    private ImageAdapter imageAdapter;  //用來顯示縮圖
    public static List<String> imageSource = new ArrayList<>();
    private ItemDAO itemDAO;
    public String[] mrecfilename;
    public String[] filename;
    public String rename, renamee;
    public String[] getname, intentname;

    private HashMap photo_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        processViews();

        itemDAO = new ItemDAO(getApplicationContext());

        if (itemDAO.getCount() == 0) {
            itemDAO.sample();
        }

        mrecfilename = itemDAO.myNote_recfilename();

        ContentResolver cr = getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };

        //查詢SD卡的圖片
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        thumbs = new ArrayList<>();
        imagePaths = new ArrayList<>();

        int k= 0;

        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID));// ID
            thumbs.add(id + "");

            String a = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

            String[] separated = a.split("/");

            String picfile = separated[5];
            String[] picname = picfile.split("\\.");
            String pic_name = picname[0];
            String filename = pic_name.replace("P",""); //filename is photo name.

            //HashMap photo_name = new HashMap();

            //putdata(photo_name, i, filename);
            String filepath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//抓路徑

            imagePaths.add(filepath);
            imageSource.add(filepath);

            Log.d("Pic", "Name:" + filename);

/*
            for(int j = 0; j < mrecfilename.length; j++) {
                String filenamee = mrecfilename[j];
                Log.d("database", "picname" +mrecfilename[j]);
                if (filenamee.equals(filename)) {
                    k++;
                    rename = rename +","+ i;
                    renamee = renamee + "," + filenamee;

                }


            }
            Log.d("k", "value:" +k);
            Log.d("rename", "value:    "+rename);
*/
        }

        cursor.close();

        imageAdapter = new ImageAdapter(GridViewActivity.this, thumbs);
        gridView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();


        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
/*
                String imageName = (String) imageView.getTag();
                getname = rename.split(","); //id
                intentname = renamee.split(","); //name
*/
                imageView.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);



/*
                for(int i = 0; i < getname.length; i++) {
                    //String forfilename = getname[i];
                    //String intent_name = intentname[i];  //photo name, use to intent to that item
                    if (getname[i].equals(imageName)) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), com.example.shihjie_sun.voicecontroller_beta.Main_Activity.class);
                        //Bundle bundle = new Bundle();
                        //bundle.putString("filename", intentname[i]);
                        //intent.putExtras(bundle);
                        startActivity(intent);

                        Toast.makeText(com.example.shihjie_sun.voicecontroller_beta.Features.GridViewActivity.this, "The Photo is in your items", Toast.LENGTH_LONG).show();

                    }


                }
*/
                Toast.makeText(com.example.shihjie_sun.voicecontroller_beta.Features.GridViewActivity.this, "Done!!", Toast.LENGTH_LONG).show();//The results output


            }

        });
        imageView.setVisibility(View.GONE);





    }

    private void processViews() {
        gridView = (GridView) findViewById(R.id.gridView1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        // 隱藏狀態列ProgressBar
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_test, menu);
        return true;
    }

    public void setImageView(int position){
        Bitmap bm = BitmapFactory.decodeFile(imagePaths.get(position));
        imageView.setImageBitmap(bm);
        imageView.setTag(Integer.toString(position));
        imageView.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
    }

    public void putdata (HashMap map, int key, String value) {

        map.put(key, value);
    }




}

