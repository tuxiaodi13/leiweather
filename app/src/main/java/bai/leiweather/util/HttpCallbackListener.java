package bai.leiweather.util;

/**
 * Created by Baiyaozhong on 2015/7/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
