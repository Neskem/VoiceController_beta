package com.example.shihjie_sun.voicecontroller_beta.Features;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.shihjie_sun.voicecontroller_beta.Database.Item;
import com.example.shihjie_sun.voicecontroller_beta.Database.ItemDAO;
import com.example.shihjie_sun.voicecontroller_beta.Database.MyDBHelper;
import com.example.shihjie_sun.voicecontroller_beta.Database.SessionManager;
import com.example.shihjie_sun.voicecontroller_beta.R;

import java.io.File;
import java.util.List;

/**
 * Created by ShihJie_Sun on 2015/12/4.
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    private Button button;
    private MyDBHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do not need the title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        button = (Button) findViewById(R.id.ok);
        button.setOnClickListener(this);

       // db = new MyDBHelper(getApplicationContext(),"mydata.db",null,4);
        //session = new SessionManager(getApplicationContext());
        MyTask task = new MyTask();
        task.execute();

    }

    class MyTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            db = new MyDBHelper(getApplicationContext(),"mydata.db",null,4);
            session = new SessionManager(getApplicationContext());

            return null;
        }
    }



    public void onClick(View view) {

        Intent intent = new Intent();
        intent.setClass(AboutActivity.this, com.example.shihjie_sun.voicecontroller_beta.Main_Activity.class);
        startActivity(intent);
        finish();
    }

    public void onBackup(View view) {

        Intent intent = new Intent();
        intent.setClass(AboutActivity.this, com.example.shihjie_sun.voicecontroller_beta.Features.BackActivity.class);
        startActivity(intent);
        finish();

    }

    public void PicBackUp(View v) {

        Intent intent = new Intent();
        intent.setClass(this, com.example.shihjie_sun.voicecontroller_beta.Features.GridViewActivity.class);
        startActivityForResult(intent, 0);
        finish();

    }

    public void LogOut(View v) {

        session.setLogin(false);
        db.deleteUsers();

        Intent intent = new Intent(this, com.example.shihjie_sun.voicecontroller_beta.MySQL.Activity.LoginActivity.class);
        startActivityForResult(intent, 0);

    }

    public void Movie(View v) {

        Intent intent = new Intent();
        intent.setClass(this, com.example.shihjie_sun.voicecontroller_beta.Movie.MovieActivity.class);
        startActivityForResult(intent, 0);
        finish();

    }

    public void Note(View v) {

        Intent intent = new Intent();
        intent.setClass(this, com.example.shihjie_sun.voicecontroller_beta.Note.NoteActivity.class);
        startActivityForResult(intent, 0);
        finish();

    }



}