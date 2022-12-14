package com.laba.user.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.laba.user.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.map_custom_infowindow, null);
        TextView address = view.findViewById(R.id.address);
//        TextView eta = view.findViewById(R.id.eta);
        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        Log.d("MMddd", marker.getId());

        address.setText(infoWindowData.getAddress());
//        if(infoWindowData.getArrival_time() == null){
//            eta.setVisibility(View.GONE);
//        }else {
//            eta.setText(infoWindowData.getArrival_time());
//        }

        return view;
    }
}
