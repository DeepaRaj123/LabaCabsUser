package com.laba.user.ui.activity.upcoming_trip_detail;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);
    void onError(Throwable e);
}
