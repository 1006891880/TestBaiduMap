package com.baidu.testbaidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.testbaidumap.activity.ActAddressLocationNew1;
import com.baidu.testbaidumap.activity.ActBaseMap;
import com.baidu.testbaidumap.activity.LayersDemo;
import com.baidu.testbaidumap.activity.LocationAndPoiSearchActivity;
import com.baidu.testbaidumap.activity.MapControlDemo;
import com.baidu.testbaidumap.activity.MapFragmentDemo;
import com.baidu.testbaidumap.activity.UISettingDemo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                Intent intent = new Intent(this, ActBaseMap.class);
                startActivity(intent);
                break;
            case R.id.button2:
                Intent intent1 = new Intent(this, MapFragmentDemo.class);
                startActivity(intent1);
                break;
            case R.id.button3:
                Intent intent2 = new Intent(this, LayersDemo.class);
                startActivity(intent2);
                break;
            case R.id.button4:
                Intent intent3 = new Intent(this, MapControlDemo.class);
                startActivity(intent3);
                break;
            case R.id.button5:
                Intent intent4 = new Intent(this, UISettingDemo.class);
                startActivity(intent4);
                break;
            case R.id.button6:
                Intent intent5 = new Intent(this, LocationAndPoiSearchActivity.class);
                startActivity(intent5);
                break;
            case R.id.button7:
                Intent intent6 = new Intent(this, ActAddressLocationNew1.class);
                startActivity(intent6);
                break;
        }
    }
}
