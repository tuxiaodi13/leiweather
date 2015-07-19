package bai.leiweather.model;

import java.lang.ref.SoftReference;

/**
 * Created by Baiyaozhong on 2015/7/19.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public void setId(int id){
        this.id=id;
    }
    public int getId() {
        return id;
    }

    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public String getCityName(){
        return cityName;
    }

    public void setCityCode(String cityCode){
        this.cityCode=cityCode;
    }
    public String getCityCode(){
        return cityCode;
    }

    public void setProvinceId(int provinceId){
        this.provinceId=provinceId;
    }
    public int getProvinceId(){
        return provinceId;
    }
}
