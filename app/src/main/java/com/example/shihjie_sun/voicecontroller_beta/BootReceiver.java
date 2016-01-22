package com.example.shihjie_sun.voicecontroller_beta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ShihJie_Sun on 2015/12/21.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO Auto-generated method stub
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            Intent serviceIntent = new Intent(context, SMS_Service.class);
            context.startService(serviceIntent);

        }
    }
}
