package bai.leiweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bai.leiweather.database.LeiWeatherDB;
import bai.leiweather.model.City;
import bai.leiweather.model.County;
import bai.leiweather.model.Province;

/**
 * Created by Baiyaozhong on 2015/7/20.
 */
public class Utility {

    /*
     *�����ʹ�����������ص�ʡ������
     */
    public synchronized static boolean handleProvincesResponse(LeiWeatherDB leiWeatherDB,String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces=response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){
                for(String p:allProvinces) {
                    Province province=new Province();
                    String[] array = p.split("\\|");
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    leiWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /*
     *�����ʹ���ӷ��������صĳ�������
     */
    public synchronized static boolean handleCityResponse(LeiWeatherDB leiWeatherDB,
                                                          String response,int provinceId){
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c:allCities){
                    City city=new City();
                    String[] array=c.split("\\|");
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    leiWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /*
     *�����ʹ���ӷ��������ص��ؼ�����
     */
    public synchronized static boolean handleCountyResponse(LeiWeatherDB leiWeatherDB,
                                                            String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if(allCounties!=null&&allCounties.length>0){
                for (String c:allCounties){
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    leiWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    /*
     *�������������ص�JSON���ݣ������������������ݱ��浽����
     */
    public static void handleWeatherResponse(Context context,String response){

        try{
            //removeUnvalid�����Ƴ���JSON�����е������ֶΡ�
            JSONObject jsonObject=new JSONObject( removeUnvalid(response,"weather_callback("));
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String currentTemp=weatherInfo.getString("temp");
            String weatherDesp=weatherInfo.getString("weather1");
            String publishTime=weatherInfo.getString("date");
            String humidity=weatherInfo.getString("sd");
            String windSpeed=weatherInfo.getString("fx1")+weatherInfo.getString("fl1");
            String tomorrowTemp=weatherInfo.getString("temp2");
            String tomorrowWeatherDesp=weatherInfo.getString("weather2");
            String thirdTemp=weatherInfo.getString("temp3");
            String thirdWeatherDesp=weatherInfo.getString("weather3");
            saveWeatherInfo(context,cityName,weatherCode,temp1,currentTemp,weatherDesp,publishTime,
                             humidity,windSpeed,tomorrowTemp,tomorrowWeatherDesp,thirdTemp,
                    thirdWeatherDesp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
     *����������������ݱ��浽SharedPreferences�ļ��С�
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                       String temp1,String currentTemp,String weatherDesp,
                                       String publishTime,String humidity,String windSpeed,
                                       String tomorrowTemp,String tomorrowWeatherDesp,
                                       String thirdTemp,String thirdWeatherDesp){
        SimpleDateFormat sdf=new SimpleDateFormat("dd E", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("temp1",temp1);
        editor.putString("current_temp",currentTemp);
        editor.putString("publish_time",publishTime);
        editor.putString("humidity",humidity);
        editor.putString("wind_speed",windSpeed);
        editor.putString("tomorrow_temp", tomorrowTemp);
        editor.putString("tomorrow_weatherdesp",tomorrowWeatherDesp);
        //��ȡ��������ڡ�
        Calendar calendar=Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, 1);
        editor.putString("tomorrow_date", sdf.format(calendar.getTime()));
        editor.putString("third_temp",thirdTemp);
        editor.putString("third_weatherdesp",thirdWeatherDesp);
        //��ȡ��������ڡ�
        calendar.roll(Calendar.DAY_OF_MONTH,1);
        editor.putString("third_date",sdf.format(calendar.getTime()));

        editor.commit();
    }
    /*
     *�����ص�JSON����ǰ����һЩ����Ҫ���ֶΣ����ô˺����Ƴ������ַ�
     */
    private static String removeUnvalid(String response,String start){
        if(response==null){
            return response;
        }
        if (response!=null&&response.startsWith(start)){
            return response.substring(start.length(),response.length()-1);
        }else{
            return response;
        }
    }
}

