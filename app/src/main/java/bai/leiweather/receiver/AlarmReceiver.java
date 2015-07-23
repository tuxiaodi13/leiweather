package bai.leiweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bai.leiweather.service.AutoUpdateWeather;

/**
 * Created by Baiyaozhong on 2015/7/23.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent intent){
        Intent i=new Intent(context, AutoUpdateWeather.class);
        context.startService(i);
    }
}
