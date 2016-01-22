package com.example.shihjie_sun.voicecontroller_beta;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Display;
import android.view.View;
import android.view.Window;

/**
 * Created by ShihJie_Sun on 2016/1/8.
 */
public class Main_Activity extends Activity {
    private MainActivityFragment mainActivityFragment;

    private View activity_main;
    private FragmentManager fragmentManager;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main_activity);

/*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog() ////打印logcat，當然也可以定位到dropbox，通過文件保存相應的log
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build());
*/
        if (findViewById(R.id.MainActivityUI) != null) {

            if (saveInstanceState != null) {
                return;
            }

            MainActivityFragment firstFragment = new MainActivityFragment();
            getFragmentManager().beginTransaction().replace(R.id.MainActivityUI, firstFragment).commit();

        } else {
            MainActivityFragment firstFragment = new MainActivityFragment();

            getFragmentManager().beginTransaction().replace(R.id.headlines_fragment, firstFragment).commit();

        }

/*
        Display display = getWindowManager().getDefaultDisplay();
        if  (display.getWidth() < display.getHeight()) {
            MainActivityFragment fragment1 =  new MainActivityFragment();
            getFragmentManager().beginTransaction().replace(R.id.headlines_fragment, fragment1).commit();
        }  else  {
            MainActivityFragment fragment1 = new MainActivityFragment();
            //ItemActivityFragment fragment2 =  new ItemActivityFragment();
            getFragmentManager().beginTransaction().replace(R.id.article_fragment, fragment1).commit();
           // getFragmentManager().beginTransaction().replace(R.id.SingleActivityUI_bottom, fragment2).commit();
        }
*/
        //initView();
/*
        Bundle bundle0311 =this.getIntent().getExtras();
        String filename = bundle0311.getString("filename");

        if( filename != null) {

        }
*/
    }

    void initView() {
       Fragment initFragment = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.MainActivityUI, initFragment, "Initial");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void updateFragment() {
        MainActivityFragment firstFragment = new MainActivityFragment();

        getFragmentManager().beginTransaction().replace(R.id.headlines_fragment, firstFragment).commit();
    }
}
