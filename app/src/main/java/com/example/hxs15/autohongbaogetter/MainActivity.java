package com.example.hxs15.autohongbaogetter;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static boolean isOn=false;
    private SharedPreferences sharedPreferences;

    private TextView mtv;
    private TextView ctv;

    public static myHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=getSharedPreferences("data",0);

        getterSwitch(sharedPreferences.getBoolean("isOn",false));

        mtv=findViewById(R.id.hongbaomoney);
        ctv=findViewById(R.id.hongbaonumber);

        mtv.setText(sharedPreferences.getString("money","0"));
        ctv.setText(String.valueOf(sharedPreferences.getInt("count",0)));

        handler=new myHandler();
    }


    public void autoGetterSwitch(View v){
        isOn=!isOn;
        getterSwitch(isOn);
    }
    public void getterSwitch(boolean b){
        Button btn=findViewById(R.id.switch_btn);
        if(b) btn.setText("关闭自动抢红包");
        else btn.setText("打开红包助手");
        SharedPreferences.Editor e=sharedPreferences.edit();
        e.putBoolean("isOn",b);
        e.apply();
    }
    public void showDutyClare(View view){
        new AlertDialog.Builder(this)
                .setTitle("免责声明")
                .setMessage(R.string.dutyClare)
                .setPositiveButton("确定",null)
                .show();
    }
    public void updateInfo(String money,int count){
        Toast.makeText(this,"--->rogger",Toast.LENGTH_SHORT).show();
        mtv.setText(money);
        ctv.setText(String.valueOf(count));
    }


   class myHandler extends Handler{

        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();
            String money=bundle.getString("money");
            int count=bundle.getInt("count");
            updateInfo(money,count);
        }

    };

}
