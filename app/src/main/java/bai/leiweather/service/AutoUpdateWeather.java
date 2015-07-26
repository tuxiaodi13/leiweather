package bai.leiweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import bai.leiweather.R;
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
                sendNotification();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+8*60*60*1000;
        Intent i=new Intent(this,AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    /*
     *��̨�Զ���������,�����µ�������Ϣ�Զ����´洢��SharedPreferences��
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weather_code", "");
        String address="http://weather.123.duba.net/static/weather_info/"+weatherCode+".html";
        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateWeather.this, response);
            }

            @Override
            public void onFinish(Bitmap bitmap) {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
    /*
     *����֪ͨ
     */
    private void sendNotification(){
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification=new Notification(R.mipmap.tudi_notify,"С�٣������ָ�������",
                System.currentTimeMillis());
        //���õ�����Զ�ȡ��������������������Բ����á�
        notification.flags =Notification.FLAG_AUTO_CANCEL;
        //��������
        notification.defaults=Notification.DEFAULT_SOUND;

        //�����������
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String cityName=prefs.getString("city_name", "");
        String currentTemp=prefs.getString("current_temp", "")+"��";
        String weatherDesp=prefs.getString("weather_desp","");
        String temp=prefs.getString("temp1","");
        //�Զ�����档
        RemoteViews contentView=new RemoteViews(getPackageName(),R.layout.notify_layout);
        contentView.setTextViewText(R.id.notify_cityname,cityName);
        contentView.setTextViewText(R.id.nofity_currenttemp,currentTemp);
        contentView.setTextViewText(R.id.notify_temp,temp);
        contentView.setTextViewText(R.id.notify_weatherdesp,weatherDesp);
        notification.contentView=contentView;
        manager.notify(1,notification);
    }
}
