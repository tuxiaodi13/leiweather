package bai.leiweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import bai.leiweather.R;
import bai.leiweather.model.SearchDrawable;
import bai.leiweather.util.HttpCallbackListener;
import bai.leiweather.util.HttpUtilForBitmap;


public class LoginActivity extends Activity implements View.OnClickListener {

    //初始化各控件
    private EditText userNameEdit;
    private EditText telNumEdit;
    private CheckBox rememberPass;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private long START_ANIMATION_DURATION = 600;
    private Animation mStartAnimation;
    private SearchDrawable searchDrawable;
    private ImageView imageView;
    boolean back = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        userNameEdit=(EditText)findViewById(R.id.user_name);
        telNumEdit=(EditText)findViewById(R.id.tel_num);
        telNumEdit.bringToFront();
        rememberPass=(CheckBox)findViewById(R.id.remember_password);
        imageView = (ImageView) findViewById(R.id.login);
        searchDrawable = new SearchDrawable();
        imageView.setImageDrawable(searchDrawable);
        startUpAnimation();
        imageView.setOnClickListener(this);
        //检查prefs中是否存有数据，如果有就将存储的数据填充。
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember=prefs.getBoolean("remember_password",false);
        if(isRemember){
            String username=prefs.getString("user_name","");
            String telnum=prefs.getString("tel_num","");
            userNameEdit.setText(username);
            telNumEdit.setText(telnum);
            rememberPass.setChecked(true);
        }

    }

    private void startUpAnimation() {
        mStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                if(back){
                    searchDrawable.setPhase(1-interpolatedTime);
                }else{
                    searchDrawable.setPhase(interpolatedTime);
                }
            }
        };
        mStartAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mStartAnimation.setDuration(START_ANIMATION_DURATION);
    }

    @Override
    public void onClick(View v) {
        imageView.startAnimation(mStartAnimation);
        back = !back;
        login();
    }
    /*
     *登陆
     */
    private void login(){
        String username=userNameEdit.getText().toString();
        String telnum=telNumEdit.getText().toString();
        System.out.print(username+telnum);
        if (username.equals("leilei")&&(telnum.equals("3269")||telnum.equals("9692"))){

            editor=prefs.edit();
            //如果输入密码正确，且复选框被选中，则将数据保存到SharedPreferences，否则清除SharedPreferences.
            if(rememberPass.isChecked()){
                editor.putString("user_name",username);
                editor.putString("tel_num", telnum);
                editor.putBoolean("remember_password", true);
            }else{
                editor.clear();
            }
            editor.commit();
            Intent intent=new Intent(this,ChooseAreaActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(LoginActivity.this,"不对！",Toast.LENGTH_SHORT).show();
        }
    }
}
