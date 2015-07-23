package bai.leiweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import bai.leiweather.receiver.AlarmReceiver;
import bai.leiweather.util.HttpCallbackListener;
import bai.leiweather.util.HttpUtil;
import bai.leiweather.util.Utility;

/**
 * Created by Baiyaozhong on 2015/7/23.
 */
public class AutoUpdateWeather extends Service {
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        });
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+8*60*60*1000;
        Intent i=new Intent(this,AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    /*
     *后台自动更新天气,将最新的天气信息自动更新存储到SharedPreferences中
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weather_code", "");
        String address="http://weather.123.duba.net/static/weather_info/"+weatherCode+".html";
        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateWeather.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
