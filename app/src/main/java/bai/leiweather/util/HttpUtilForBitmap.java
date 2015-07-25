package bai.leiweather.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Baiyaozhong on 2015/7/24.
 */
public class HttpUtilForBitmap {
    public static void setHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try{
                    URL url=new URL(address);
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    Bitmap bitmap=BitmapFactory.decodeStream(in);
                    if (listener!=null){
                        listener.onFinish(bitmap);
                    }
                } catch (Exception e){
                        e.printStackTrace();
                } finally {
                    if(listener!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
