package com.laba.user.ui.activity.help;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.Help;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIView extends MvpView {
    void onSuccess(Help help);
    void onError(Throwable e);
}
