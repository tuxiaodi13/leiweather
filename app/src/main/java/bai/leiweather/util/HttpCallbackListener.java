package bai.leiweather.util;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by Baiyaozhong on 2015/7/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onFinish(Bitmap bitmap);
    void onError(Exception e);
}
