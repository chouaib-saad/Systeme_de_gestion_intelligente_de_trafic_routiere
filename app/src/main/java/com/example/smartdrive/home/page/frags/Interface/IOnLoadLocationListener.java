package com.example.smartdrive.home.page.frags.Interface;

import com.example.smartdrive.home.page.frags.MyLatLng;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface IOnLoadLocationListener {

    void onLoadLocationSuccess(List<MyLatLng> latLngs);
    void onLoadLocationFailed(String message);


}
