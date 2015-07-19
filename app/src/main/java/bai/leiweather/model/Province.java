package bai.leiweather.model;

/**
 * Created by Baiyaozhong on 2015/7/19.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public void setId(int id){
        this.id=id;
    }
    public int getId() {
        return id;
    }


    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public String getProvinceName(){
        return provinceName;
    }

    public void setProvinceCode(String provinceCode){
        this.provinceCode=provinceCode;
    }
    public String getProvinceCode(){
        return provinceCode;
    }
}
