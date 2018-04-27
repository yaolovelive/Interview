package com.yy.interview.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yao on 18-4-10.
 */

public class LocationEntity implements Parcelable{
    /**
     * 当前定位城市
     */
    private String city_start;
    /**
     * 当前定位地址
     */
    private String address_start;
    /**
     * 当前定位坐标
     */
    private LatLonPoint point_start;
    /**
     * 目标位置集合
     */
    private ArrayList<GeocodeAddress> endAddresses;


    public LocationEntity() {
    }


    protected LocationEntity(Parcel in) {
        city_start = in.readString();
        address_start = in.readString();
        point_start = in.readParcelable(LatLonPoint.class.getClassLoader());
        endAddresses = in.readArrayList(GeocodeAddress.class.getClassLoader());
    }

    public static final Creator<LocationEntity> CREATOR = new Creator<LocationEntity>() {
        @Override
        public LocationEntity createFromParcel(Parcel in) {
            return new LocationEntity(in);
        }

        @Override
        public LocationEntity[] newArray(int size) {
            return new LocationEntity[size];
        }
    };

    public String getCity_start() {
        return city_start;
    }

    public void setCity_start(String city_start) {
        this.city_start = city_start;
    }

    public String getAddress_start() {
        return address_start;
    }

    public void setAddress_start(String address_start) {
        this.address_start = address_start;
    }

    public LatLonPoint getPoint_start() {
        return point_start;
    }

    public void setPoint_start(LatLonPoint point_start) {
        this.point_start = point_start;
    }

    public ArrayList<GeocodeAddress> getEndAddresses() {
        return endAddresses;
    }

    public void setEndAddresses(ArrayList<GeocodeAddress> endAddresses) {
        this.endAddresses = endAddresses;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city_start);
        dest.writeString(this.address_start);
        dest.writeParcelable(this.point_start,Parcelable.CONTENTS_FILE_DESCRIPTOR);
        dest.writeList(endAddresses);
    }
}
