package bai.leiweather.model;

/**
 * Created by Baiyaozhong on 2015/7/19.
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }

    public void setCountyName(String countyName){
        this.countyName=countyName;
    }
    public String getCountyName(){
        return countyName;
    }

    public void setCountyCode(String countyCode){
        this.countyCode=countyCode;
    }
    public String getCountyCode(){
        return countyCode;
    }

    public void setCityId(int cityId){
        this.cityId=cityId;
    }
    public int getCityId(){
        return cityId;
    }
}
