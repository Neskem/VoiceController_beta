package com.example.shihjie_sun.voicecontroller_beta.Features;


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
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.MySQL.App.AppConfig;
import com.example.shihjie_sun.voicecontroller_beta.R;

/**
 * Created by ShihJie_Sun on 2015/12/24.
 */
public class ImageAdapter extends BaseAdapter {

    private ViewGroup layout;
    private Context context;
    private List item;
    private String srcPath;
    int index;
    private List<String> src = new ArrayList<>();
    public int Image_position;


    public ImageAdapter(Context context, List item) {

        super();
        this.context = context;
        this.item = item;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview = inflater.inflate(R.layout.item_photo, parent, false);
        layout = (ViewGroup) rowview.findViewById(R.id.rl_item_photo);
        ImageView imageView = (ImageView) rowview.findViewById(R.id.imageView1);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float dd = dm.density;
        float px = 25 * dd;
        float screenWidth = dm.widthPixels;
        int newWidth = (int) (screenWidth - px) / 4; // 一行顯示四個縮圖

        layout.setLayoutParams(new GridView.LayoutParams(newWidth, newWidth));
        imageView.setId(position);
        // Bitmap bm = BitmapFactory.decodeFile((String)coll.get(position));
        // Bitmap newBit = Bitmap.createScaledBitmap(bm, newWidth, newWidth,
        // true);

        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(context
                        .getApplicationContext().getContentResolver(), Long
                        .parseLong((String) item.get(position)),
                MediaStore.Images.Thumbnails.MICRO_KIND, null);

        imageView.setImageBitmap(bm);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        //點擊照片
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "index:" + position + " is load!", Toast.LENGTH_SHORT)
                        .show();
                Image_position = position;
                ((com.example.shihjie_sun.voicecontroller_beta.Features.GridViewActivity)context).setImageView(position);



            }

        });
        index = position;
        return rowview;
    }



    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return item.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return item.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private void uploadFile(String uploadUrl)
    {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";

        src = GridViewActivity.imageSource;
        src.get(index);
        try
        {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();//The HTTP connection
            // Stream size settings of each transmission, can effectively prevent the mobile phone because of insufficient memory to collapse
            // This method is used to request the text in advance do not know the content length is enabled when no internal buffer of HTTP flow.
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // Allow the input and output streams
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // Using the POST method
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");//Keep connection
            httpURLConnection.setRequestProperty("Charset", "UTF-8");//Code
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);//POST passes in the code

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());//The output stream
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);//File input stream, writing to memory
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // Read file
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();//HTTP input, to obtain the returned results
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            dos.close();
            is.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }



}
