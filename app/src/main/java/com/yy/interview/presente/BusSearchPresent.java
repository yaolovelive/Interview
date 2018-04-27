package com.yy.interview.presente;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.yy.interview.model.entity.LocationEntity;

import java.util.List;

/**
 * Created by yao on 18-4-15.
 */

public class BusSearchPresent {

    private RouteSearch routeSearch;

    private Listener listener;
    private Context context;
    public BusSearchPresent(Context context){
        this.context = context;
        listener = new Listener();
    }

    public void search(LocationEntity locationEntity){
        routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(listener);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(locationEntity.getPoint_start(),locationEntity.getEndAddresses().get(0).getLatLonPoint());
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo,RouteSearch.BUS_DEFAULT,locationEntity.getCity_start(),1);
        routeSearch.calculateBusRouteAsyn(query);
    }

    private class Listener implements RouteSearch.OnRouteSearchListener{

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
            /*Log.d("yy",""+i);
            if(i == 1000){
                List<BusPath> paths = busRouteResult.getPaths();
                for (int j = 0; j < paths.size(); j++) {
                    Log.d("yy","\n\n方案:"+(j+1));
                    List<BusStep> steps = paths.get(j).getSteps();
                    for (int k = 0; k < steps.size(); k++) {
                        List<RouteBusLineItem> lines = steps.get(k).getBusLines();
                        for (int l = 0; l < lines.size(); l++) {

                            Log.d("yy",lines.get(l).getDepartureBusStation().toString()+",线路:"+lines.get(l).getBusLineName());
                        }
                    }
                    Log.d("yy","步行:"+paths.get(j).getWalkDistance()+"米");
                }
            }*/
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    }
}
