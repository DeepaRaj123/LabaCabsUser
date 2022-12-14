package com.laba.user.ui.fragment.upcoming_trip;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.CancelRequestInterface;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Provider;
import com.laba.user.ui.activity.upcoming_trip_detail.UpcomingTripDetailActivity;
import com.laba.user.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingTripFragment extends BaseFragment implements UpcomingTripIView, CancelRequestInterface {

    @BindView(R.id.upcoming_trip_rv)
    RecyclerView upcomingTripRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_error)
    TextView error;
    Unbinder unbinder;
    private UpcomingTripPresenter<UpcomingTripFragment> presenter = new UpcomingTripPresenter<>();
    private CancelRequestInterface callback;


    List<Datum> list = new ArrayList<>();
    TripAdapter adapter;

    public UpcomingTripFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_upcoming_trip;
    }
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        error.setText(getString(R.string.no_upcoming_found));
        callback = this;
        adapter = new TripAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        upcomingTripRv.setLayoutManager(mLayoutManager);
        upcomingTripRv.setItemAnimator(new DefaultItemAnimator());
        upcomingTripRv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        presenter.upcomingTrip();
    }

    private void alertCancel(Integer requestId) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.cancel_request))
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    showLoading();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("request_id", requestId);
                    presenter.cancelRequest(map);
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onSuccess(List<Datum> datumList) {
        progressBar.setVisibility(View.GONE);

        list.clear();
        list.addAll(datumList);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        presenter.upcomingTrip();
    }

    @Override
    public void onError(Throwable e) {
        progressBar.setVisibility(View.GONE);
        handleError(e);
    }

    @Override
    public void cancelRequestMethod() {
        presenter.upcomingTrip();
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

        private List<Datum> list;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private CardView itemView;
            private TextView bookingId;
            private TextView scheduleAt;
            private ImageView staticMap;
            private Button cancel;
            CircleImageView avatar;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                bookingId = view.findViewById(R.id.booking_id);
                scheduleAt = view.findViewById(R.id.schedule_at);
                staticMap = view.findViewById(R.id.static_map);
                cancel = view.findViewById(R.id.cancel);
                avatar = view.findViewById(R.id.avatar);
                itemView.setOnClickListener(this);
                cancel.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                BaseActivity.DATUM = list.get(position);
                if (view.getId() == R.id.item_view) {

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity(), staticMap, ViewCompat.getTransitionName(staticMap));
                    Intent intent = new Intent(getActivity(), UpcomingTripDetailActivity.class);
                    startActivity(intent, options.toBundle());

                } else if (view.getId() == R.id.cancel) {
                    CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
                    cancelRideDialogFragment.show(activity().getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                    //alertCancel(BaseActivity.DATUM.getId());
                }

            }
        }

        private TripAdapter(List<Datum> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_upcoming_trip, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Datum datum = list.get(position);
//            holder.scheduleAt.setText(datum.getScheduleAt());

            try{
                SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date finishedAt = smf.parse(datum.getScheduleAt());
                SimpleDateFormat smf1 = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
                String formattedFinishedAt = smf1.format(finishedAt);
                holder.scheduleAt.setText(formattedFinishedAt);
            }catch (Exception e){
                holder.scheduleAt.setText(datum.getScheduleAt());
            }


            holder.bookingId.setText(datum.getBookingId());
            Glide.with(activity()).load(datum.getStaticMap()).apply(RequestOptions.
                    placeholderOf(R.drawable.ic_launcher_background).dontAnimate().
                    error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.staticMap);

            Provider provider = datum.getProvider();
            if (provider != null)
                Glide.with(activity()).load(provider.getAvatar()).
                        apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).
                                dontAnimate().error(R.drawable.ic_user_placeholder).
                                diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.avatar);


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }
}
