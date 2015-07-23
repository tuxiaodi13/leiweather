package bai.leiweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bai.leiweather.model.City;
import bai.leiweather.model.County;
import bai.leiweather.model.Province;

/**
 * Created by Baiyaozhong on 2015/7/19.
 */
public class LeiWeatherDB {
    /*
     *数据库名
     */
    public static final String DB_NAME="lei_weather";
    /*
     *数据库版本
     */
    public static final int VERSION=1;
    private static LeiWeatherDB leiWeatherDB;
    private SQLiteDatabase db;

    public LeiWeatherDB(Context context){
        LeiWeatherOpenHelper leiWeatherOpenHelper=new LeiWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=leiWeatherOpenHelper.getWritableDatabase();
    }
    /*
     *获取LeiWeatherDB的实例
     */
    public synchronized static LeiWeatherDB getInstance(Context context){
        if(leiWeatherDB==null){
            leiWeatherDB=new LeiWeatherDB(context);
        }
        return leiWeatherDB;
    }

    /*
     *将Province实例存储到数据库
  */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    /*
     *从数据库读取全国所有省份
     */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return list;
    }
    /*
     *将City实例存储到数据库
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }
    /*
     *从数据库中读取某省的所有City信息。
     */
    public List<City> loadCity(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",
                new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while(cursor.moveToNext());

        }
        return list;
    }
    /*
     *将County实例存储到数据库
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }
    /*
     *从数据库中取出某市的County信息
     */
    public List<County> loadCounty(int cityId){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",
                new String[]{String.valueOf(cityId)},null,null,null );
        if(cursor.moveToFirst()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
