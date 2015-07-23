package bai.leiweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Baiyaozhong on 2015/7/19.
 */
public class LeiWeatherOpenHelper extends SQLiteOpenHelper {
    /*
     *Province建表语句
     */
    public static final String CREATE_PROVINCE="Create table Province("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";
    /*
     *City建表语句
     */
    public static final String CREATE_CITY="Create table City("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";
    /*
     *County建表语句
     */
    public static final String CREATE_COUNTY="Create table County("
            + "id integer primary key autoincrement,"
            + "county_name text,"
            + "county_code text,"
            + "city_id integer)";
    public LeiWeatherOpenHelper(Context context,String name, SQLiteDatabase.CursorFactory
                                factory,int version) {
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
    }
}
