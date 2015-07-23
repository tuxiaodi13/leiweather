package bai.leiweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import bai.leiweather.R;
import bai.leiweather.service.AutoUpdateWeather;
import bai.leiweather.util.HttpCallbackListener;
import bai.leiweather.util.HttpUtil;
import bai.leiweather.util.Utility;

/**
 * Created by Baiyaozhong on 2015/7/21.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    //显示地方名字
    private TextView cityNameText;
    //显示发布时间
    private TextView publishText;
    //显示当前日期
    private TextView currentDateText;
    //显示天气情况
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView currentTempText;
    //刷新按钮
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
        currentDateText=(TextView)findViewById(R.id.current_date);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        currentTempText=(TextView)findViewById(R.id.current_temp);
        refreshButton=(Button)findViewById(R.id.refresh);
        selectCityButton=(Button)findViewById(R.id.select_city);
        refreshButton.setOnClickListener(this);
        selectCityButton.setOnClickListener(this);

        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询该县的天气
            publishText.setText("同步中");
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
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从服务器中取出天气代号
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    //处理从服务器中返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
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
     *从SharedPreferens文件中读取存储的天气信息，并显示。
     */
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        publishText.setText("发布时间:" + prefs.getString("publish_time", ""));
        currentDateText.setText("现在时间:"+prefs.getString("current_date", ""));
        temp1Text.setText(prefs.getString("temp1",""));
        currentTempText.setText(prefs.getString("current_temp",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        Intent intent=new Intent(this, AutoUpdateWeather.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh:
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
                String currentWeatherCode=prefs.getString("weather_code","");
                queryWeatherInfo(currentWeatherCode);
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
}
