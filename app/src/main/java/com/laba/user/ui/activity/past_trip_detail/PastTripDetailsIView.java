package com.laba.user.ui.activity.past_trip_detail;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);
    void onError(Throwable e);
}
