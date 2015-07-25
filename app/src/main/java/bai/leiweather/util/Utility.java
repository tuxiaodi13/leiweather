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
     *解析和处理服务器返回的省份数据
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
     *解析和处理从服务器返回的城市数据
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
     *解析和处理从服务器返回的县级数据
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
     *解析服务器返回的JSON数据，并将解析出来的数据保存到本地
     */
    public static void handleWeatherResponse(Context context,String response){

        try{
            //removeUnvalid用来移除掉JSON数据中的无用字段。
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
     *将解析完的天气数据保存到SharedPreferences文件中。
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
        //获取明天的日期。
        Calendar calendar=Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, 1);
        editor.putString("tomorrow_date", sdf.format(calendar.getTime()));
        editor.putString("third_temp",thirdTemp);
        editor.putString("third_weatherdesp",thirdWeatherDesp);
        //获取后天的日期。
        calendar.roll(Calendar.DAY_OF_MONTH,1);
        editor.putString("third_date",sdf.format(calendar.getTime()));

        editor.commit();
    }
    /*
     *若返回的JSON数据前面有一些不需要的字段，则用此函数移除无用字符
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

