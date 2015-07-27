package bai.leiweather.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bai.leiweather.R;
import bai.leiweather.database.LeiWeatherDB;
import bai.leiweather.model.City;
import bai.leiweather.model.County;
import bai.leiweather.model.Province;
import bai.leiweather.util.HttpCallbackListener;
import bai.leiweather.util.HttpUtil;
import bai.leiweather.util.Utility;

/**
 * Created by Baiyaozhong on 2015/7/20.
 */
public class ChooseAreaActivity extends Activity {
    public final static int LEVEL_PROVINCE=0;
    public final static int LEVEL_CITY=1;
    public final static int LEVEL_COUNTY=2;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<String>();
    private int currentLevel;
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private LeiWeatherDB leiWeatherDB;
    private boolean isFromWeatherActivity;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weatherActivity",false);
        SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(this);
        //必须选中城市，且不是从WeatherActivity跳转过来，此处才能默认跳转到WeahtherActivity.
        if(prfs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        titleText=(TextView)findViewById(R.id.title_text);
        listView=(ListView)findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        leiWeatherDB=LeiWeatherDB.getInstance(this);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    /*
     *  查询全国所有的省，数据库有就从数据库查，数据库没有就从服务器查
     */
    private void queryProvinces(){
        provinceList=leiWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p:provinceList){
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"Province");
        }
    }
    /*
     *查询某省的所有市，数据库有就从数据库查，数据库没有就从服务器查
     */
    private void queryCities(){
        cityList=leiWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City c:cityList){
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            titleText.setText(selectedProvince.getProvinceName());
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"City");
        }
    }
    /*
     *查询某市的所有县，如果数据库有就从数据库查，数据库没有就从服务器查
     */
    private void queryCounties(){
        countyList=leiWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County c:countyList){
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            titleText.setText(selectedCity.getCityName());
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"County");
        }
    }
    /*
     *从服务器查询省市县信息
     */
    private void queryFromServer(String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if(type.equals("Province")){
                    result= Utility.handleProvincesResponse(leiWeatherDB,response);
                }else if(type.equals("City")){
                    result=Utility.handleCityResponse(leiWeatherDB,response,selectedProvince.getId());
                }else if(type.equals("County")){
                    result=Utility.handleCountyResponse(leiWeatherDB,response,selectedCity.getId());

                }
                if(result){
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(type.equals("Province")){
                                queryProvinces();
                            }else if(type.equals("City")){
                                queryCities();
                            }else {
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFinish(Bitmap bitmap){};


            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseAreaActivity.this,"Download Failed!" +
                                "",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /*
     *捕获back键，根据当前级别，判断按退出应该是返回市，省列表还是退出
     */
    @Override
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            if(isFromWeatherActivity){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
