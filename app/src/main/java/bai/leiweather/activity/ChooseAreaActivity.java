package bai.leiweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
        //����ѡ�г��У��Ҳ��Ǵ�WeatherActivity��ת�������˴�����Ĭ����ת��WeahtherActivity.
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
     *
     *
     *
     * ��ѯȫ�����е�ʡ�����ݿ��оʹ����ݿ�飬���ݿ�û�оʹӷ�������
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
            titleText.setText("CHINA");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"Province");
        }
    }
    /*
     *��ѯĳʡ�������У����ݿ��оʹ����ݿ�飬���ݿ�û�оʹӷ�������
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
     *��ѯĳ�е������أ�������ݿ��оʹ����ݿ�飬���ݿ�û�оʹӷ�������
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
     *�ӷ�������ѯʡ������Ϣ
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
                    //ͨ��runOnUIThread()�����ص����̴߳����߼�
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
     *����back�������ݵ�ǰ�����жϰ��˳�Ӧ���Ƿ����У�ʡ�б������˳�
     */
    @Override
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}