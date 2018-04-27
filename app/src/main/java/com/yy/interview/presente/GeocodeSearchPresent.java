package com.yy.interview.presente;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.yy.interview.model.entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yao on 18-4-7.
 */

public class GeocodeSearchPresent implements GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener {

    public interface Callback {
        int RESULT_SUCCESS = -1;
        int RESULT_ERROR = 0;

//        void onGeocodeSearched(GeocodeResult geocodeResult, int i);

        void onGeocodeSearchError();

        void getAddressSuccess(LocationEntity locationEntity);

    }

    private GeocodeSearch geocodeSearch;
    private Context context;
    private Callback callback;
    private Handler handler = new Handler();
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private LocationEntity locationEntity = new LocationEntity();

    public GeocodeSearchPresent(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(this);
        locationClient = new AMapLocationClient(context);
        locationClient.setLocationListener(this);
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        locationClientOption.setOnceLocation(true);
        locationClientOption.setNeedAddress(true);
        locationClient.setLocationOption(locationClientOption);
    }

    private String name;

    public void search(String name) {
        this.name = name;
        locationClient.startLocation();
    }

    private void startSearch(String name) {
        GeocodeQuery geocodeQuery = new GeocodeQuery(name, locationEntity.getCity_start());
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        List<GeocodeAddress> list = geocodeResult.getGeocodeAddressList();
        locationEntity.setEndAddresses((ArrayList<GeocodeAddress>) list);
        handler.post(()->callback.getAddressSuccess(locationEntity));
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                locationClient.stopLocation();
                locationEntity.setCity_start(aMapLocation.getCity());
                locationEntity.setAddress_start(aMapLocation.getAddress());
                locationEntity.setPoint_start(new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                new Thread(() -> startSearch(name)).start();
            } else {
                handler.post(() -> callback.onGeocodeSearchError());
            }
        }
    }


}
