package bai.leiweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.spec.ECField;
import java.util.logging.Handler;

import bai.leiweather.R;
import bai.leiweather.service.AutoUpdateWeather;
import bai.leiweather.util.HttpCallbackListener;
import bai.leiweather.util.HttpUtil;
import bai.leiweather.util.Utility;

/**
 * Created by Baiyaozhong on 2015/7/21.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    //显示地名
    private TextView cityNameText;
    //发布日期
    private TextView publishText;
    //天气信息
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView currentTempText;
    private TextView windSpeedText;
    private TextView humidityText;
    private TextView tomorrowDateText;
    private TextView tomorrowTempText;
    private TextView tomorrowWeatherDespText;
    private TextView thirdDateText;
    private TextView thirdTempText;
    private TextView thirdWeatherDespText;
    private TextView noteText;
    private ImageView weatherImageView;
    private LinearLayout noteLayout;
    //播放音乐
    private MediaPlayer mediaPlayer;

    //播放按钮
    private Button refreshButton;
    //选择城市按钮
    private Button selectCityButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化控件
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        currentTempText=(TextView)findViewById(R.id.current_temp);
        windSpeedText=(TextView)findViewById(R.id.wind_speed);
        humidityText=(TextView)findViewById( R.id.humidity);
        tomorrowDateText=(TextView)findViewById(R.id.tomorrow_date);
        tomorrowTempText=(TextView)findViewById(R.id.tomorrow_temp);
        tomorrowWeatherDespText=(TextView)findViewById(R.id.tomorrow_weatherdesp);
        thirdDateText=(TextView)findViewById(R.id.third_date);
        thirdTempText=(TextView)findViewById(R.id.third_temp);
        thirdWeatherDespText=(TextView)findViewById(R.id.third_weatherdesp);
        noteText=(TextView)findViewById(R.id.note_text);
        weatherImageView=(ImageView)findViewById(R.id.weather_image);
        noteLayout=(LinearLayout)findViewById(R.id.note_layout);
        refreshButton=(Button)findViewById(R.id.refresh);
        selectCityButton=(Button)findViewById(R.id.select_city);
        refreshButton.setOnClickListener(this);
        selectCityButton.setOnClickListener(this);
        //MediaPlayer初始化
        mediaPlayer=MediaPlayer.create(this,R.raw.sound_laugh);
        try{
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        noteText.setText("听说今天很适合学习啊");
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询该县天气。
            publishText.setText("同步中");
            noteLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
    }
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String address="http://weather.123.duba.net/static/weather_info/"+weatherCode+".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(String address,final String type){
        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器中得到天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    //处理从服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onFinish(Bitmap bitmap) {
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    /*
     *从SharedPreferences文件读取存储的天气信息，并显示。
     */
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String publishTime=prefs.getString("publish_time", "").substring(8,10);
        cityNameText.setText(prefs.getString("city_name",""));
        publishText.setText("今天" +publishTime+ ":00发布");
        temp1Text.setText(prefs.getString("temp1",""));
        currentTempText.setText(prefs.getString("current_temp","")+"°");
        windSpeedText.setText(prefs.getString("wind_speed",""));
        humidityText.setText("湿度"+"   "+prefs.getString("humidity",""));
        tomorrowTempText.setText(prefs.getString("tomorrow_temp",""));
        tomorrowWeatherDespText.setText(prefs.getString("tomorrow_weatherdesp",""));
        tomorrowDateText.setText(prefs.getString("tomorrow_date",""));
        thirdDateText.setText(prefs.getString("third_date",""));
        thirdTempText.setText(prefs.getString("third_temp",""));
        thirdWeatherDespText.setText(prefs.getString("third_weatherdesp",""));
        String weatherDesp=prefs.getString("weather_desp","");
        switch(weatherDesp){
            case "雷阵雨":
            case "雷电":
                weatherImageView.setBackgroundResource(R.drawable.thundershower);
                break;
            case "晴":
                weatherImageView.setBackgroundResource(R.drawable.sunny);
                break;
            case "阴":
            case "多云":
                weatherImageView.setBackgroundResource(R.drawable.cloud);
                break;
            case "多云转晴":
                weatherImageView.setBackgroundResource(R.drawable.cloud_sunny);
                break;
            case "晴转多云":
                weatherImageView.setBackgroundResource(R.drawable.sunny_cloud);
            case "大雨":
            case "大到暴雨":
                weatherImageView.setBackgroundResource(R.drawable.bigrain);
            case "中雨":
            case "小雨":
                weatherImageView.setBackgroundResource(R.drawable.rain);
            case "阵雨":
                weatherImageView.setBackgroundResource(R.drawable.shower);
                break;
            case "浮尘":
                break;
            case "大雪":
                weatherImageView.setBackgroundResource(R.drawable.bigsnow);
                break;
            case "中雪":
                weatherImageView.setBackgroundResource(R.drawable.midsnow);
                break;
            case "小雪":
                weatherImageView.setBackgroundResource(R.drawable.smallsnow);
                break;
            case "冰雹":
                weatherImageView.setBackgroundResource(R.drawable.hailstone);
                break;

            default:
                break;

        }
        weatherDespText.setText(prefs.getString("weather_desp",""));
        noteLayout.setVisibility(View.VISIBLE);
        //启动自动更新天气以及通知的服务。
        Intent intent=new Intent(this, AutoUpdateWeather.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh:
                mediaPlayer.start();
                break;
            case R.id.select_city:
                Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weatherActivity",true);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
    @Override//在活动销毁时将MediaPlayer的资源释放
    public void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
