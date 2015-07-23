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
    //��ʾ�ط�����
    private TextView cityNameText;
    //��ʾ����ʱ��
    private TextView publishText;
    //��ʾ��ǰ����
    private TextView currentDateText;
    //��ʾ�������
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView currentTempText;
    //ˢ�°�ť
    private Button refreshButton;
    //ѡ����а�ť
    private Button selectCityButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //��ʼ���ؼ�
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
            //���ؼ����ž�ȥ��ѯ���ص�����
            publishText.setText("ͬ����");
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
                        //�ӷ�������ȡ����������
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    //����ӷ������з��ص�������Ϣ
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
                        publishText.setText("ͬ��ʧ��");
                    }
                });
            }
        });
    }
    /*
     *��SharedPreferens�ļ��ж�ȡ�洢��������Ϣ������ʾ��
     */
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        publishText.setText("����ʱ��:" + prefs.getString("publish_time", ""));
        currentDateText.setText("����ʱ��:"+prefs.getString("current_date", ""));
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
