package com.yy.interview.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.yy.interview.R;
import com.yy.interview.model.entity.LocationEntity;
import com.yy.interview.presente.BusSearchPresent;

import java.io.File;
import java.net.URL;

/**
 * Created by yao on 18-4-10.
 */

public class GetwayActivity extends AppCompatActivity {
    private ListView lv_address;
    private MapView mv_eim;
    private AMap aMap;

    private Button btn_aMap;
    private Button btn_bMap;

    private LocationEntity locationInfo;
    private BusSearchPresent busSearchPresent;
    private Listener listener = new Listener();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_getway);
        mv_eim = findViewById(R.id.mv_eim);
        mv_eim.onCreate(savedInstanceState);
        init();
        initView();
    }

    private void initView() {
        lv_address = findViewById(R.id.lv_address);
        String[] addresses = new String[locationInfo.getEndAddresses().size()];
        for (int i = 0; i < locationInfo.getEndAddresses().size(); i++) {
            addresses[i] = locationInfo.getEndAddresses().get(i).getFormatAddress();
        }
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.activity_list_item, android.R.id.text1, addresses);
        lv_address.setAdapter(adapter);
        btn_aMap = findViewById(R.id.btn_aMap);
        btn_aMap.setOnClickListener(listener);
        btn_bMap = findViewById(R.id.btn_bMap);
        btn_bMap.setOnClickListener(listener);
    }

    private void init() {
        locationInfo = getIntent().getParcelableExtra("locationInfo");
        busSearchPresent = new BusSearchPresent(GetwayActivity.this);
        busSearchPresent.search(locationInfo);
        if (aMap == null)
            aMap = mv_eim.getMap();
        if(locationInfo.getEndAddresses().size() > 0) {
            LatLng latLng = new LatLng(locationInfo.getEndAddresses().get(0).getLatLonPoint().getLatitude(), locationInfo.getEndAddresses().get(0).getLatLonPoint().getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,14,30,0));
            aMap.moveCamera(cameraUpdate);
            cameraUpdate = CameraUpdateFactory.zoomBy(18);
            aMap.moveCamera(cameraUpdate);
            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(locationInfo.getEndAddresses().get(0).getFormatAddress()).snippet("DefaultMarker").draggable(false));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv_eim.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv_eim.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv_eim.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv_eim.onDestroy();
    }

    private class Listener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            LatLonPoint latLonPoint = locationInfo.getEndAddresses().get(0).getLatLonPoint();
            double v1 = latLonPoint.getLatitude();
            double v2 = latLonPoint.getLongitude();
            String dname = locationInfo.getEndAddresses().get(0).getFormatAddress();
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = null;
            switch (v.getId()){
                case R.id.btn_aMap:
                    try {
                        packageInfo = packageManager.getPackageInfo("com.autonavi.minimap",0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                    }
                    if(packageInfo == null){
                        showToast("请先安装高德地图!");
                    }else{
                        String url = "amapuri://route/plan/?sid=BGVIS1&slat=&slon=&sname="+locationInfo.getAddress_start()+"&did=BGVIS2&dlat="+v1+"&dlon="+v2+"&dname="+dname+"&dev=0&t=1";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setPackage("com.autonavi.minimap");
                        startActivity(intent);
                    }
                    break;
                case R.id.btn_bMap:
                    try {
                        packageInfo = packageManager.getPackageInfo("com.baidu.BaiduMap",0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                    }
                    if(packageInfo == null){
                        showToast("请先安装百度地图!");
                    }else{
                        double[] gps = gcj02_To_bd09(v1,v2);
                        String url = "baidumap://map/direction?origin=&destination="+gps[0]+","+gps[1]+"&destination="+dname+"&mode=transit";
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }



    private static double[] gcj02_To_bd09(double lat,double lon){
        final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = lon,y=lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y,x) + 0.000003 * Math.cos(x * x_pi);
        double templat = z * Math.sin(theta) + 0.006;
        double templon = z * Math.cos(theta) + 0.0065;
        double[] gps = {templat,templon};
        return gps;
    }
}
