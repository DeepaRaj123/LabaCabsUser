package com.laba.user.ui.adapter;

import static com.laba.user.base.BaseActivity.getNewNumberFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.laba.user.R;
import com.laba.user.common.Constants;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.EstimateFare;
import com.laba.user.data.network.model.Service;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.fragment.RateCardFragment;
import com.laba.user.ui.fragment.service.ServiceFragment;
import com.laba.user.ui.fragment.service.ServiceFragment1;

import java.util.List;

public class ServiceAdapter1 extends RecyclerView.Adapter<ServiceAdapter1.MyViewHolder> {

    private final List<Service> list;
    private final TextView capacity;
    private final Context context;
    private int lastCheckedPos = -1;
    private final Animation zoomIn;
    private final ServiceFragment1.ServiceListener mListener;
    private EstimateFare estimateFare;
    private boolean canNotifyDataSetChanged = false;

    private final ServiceAdapterOnClickListener onClickListener;


    public interface ServiceAdapterOnClickListener {
        void adapterClick(Service service);
    }

    public ServiceAdapter1(Context context, List<Service> list,
                          ServiceFragment1.ServiceListener listener,
                          TextView capacity, EstimateFare fare, ServiceAdapterOnClickListener onClickListener, int lastCheckedPos) {
        this.context = context;
        this.list = list;
        this.capacity = capacity;
        this.mListener = listener;
        this.estimateFare = fare;
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
        this.onClickListener = onClickListener;
        this.lastCheckedPos = lastCheckedPos;
        Log.e("SERVICES", "Select: " + lastCheckedPos);
    }

    public void setEstimateFare(EstimateFare estimateFare) {
        this.estimateFare = estimateFare;
        canNotifyDataSetChanged = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service, parent, false));
    }

    @SuppressLint({"StringFormatMatches", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Service service = list.get(position);
        if (service != null)
            holder.serviceName.setText(service.getName());
        if (estimateFare != null) {
            holder.estimated_fixed.setText(SharedHelper.getKey(context, "currency") + " " +
                    getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare()))));
            if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                }
            } else {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }
            }
        }
        Glide.with(context)
                .load(service.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_car).dontAnimate().error(R.drawable.ic_car))
                .into(holder.image);

        Log.e("SERVICES", "Selected: " + lastCheckedPos + " | Current: " + position + " | Available: " + service.isAvailable());
        if ((position == lastCheckedPos /*&& service.isAvailable()*/) && canNotifyDataSetChanged) {
            Log.e("SERVICES", "SELECTED");
            canNotifyDataSetChanged = false;
            capacity.setText(String.valueOf(service.getCapacity()));

            holder.mFrame_service.setBackground(service.isAvailable() ?
                    context.getResources().getDrawable(R.drawable.circle_background_primary)
                    : context.getResources().getDrawable(R.drawable.circle_background_error)
            );
            holder.serviceName.setTextColor(service.isAvailable() ?
                    context.getResources().getColor(R.color.colorPrimary)
                    : context.getResources().getColor(R.color.errorColor)
            );
            holder.price.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1);
            holder.estimated_fixed.setVisibility(View.VISIBLE);
            if (estimateFare != null) {
                if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                } else {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }
                holder.estimated_fixed.setText(SharedHelper.getKey(context, "currency") + " " +
                        getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare()))));

            }
            holder.itemView.startAnimation(zoomIn);
        } else {
            holder.mFrame_service.setBackground(context.getResources().getDrawable(R.drawable.service_bkg));
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
            holder.itemView.setAlpha((float) 0.7);
            holder.estimated_fixed.setVisibility(View.INVISIBLE);
            holder.price.setVisibility(View.INVISIBLE);
        }
        holder.notAvailableText.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(view -> {
            if (view.getId() == R.id.item_view) {
                if (lastCheckedPos == position) {
                    RateCardFragment.SERVICE = service;
                    ((MainActivity) context).changeFragment(new RateCardFragment());
                }
                lastCheckedPos = position;
                notifyDataSetChanged();
            }
            mListener.whenClicked(position);
            onClickListener.adapterClick(service);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getServiceTypeName(int position) {
        return list.get(position).getName();
    }

    public Service getSelectedService() {
        if (lastCheckedPos >= 0 && list.size() > 0) return list.get(lastCheckedPos);
        else return null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout itemView;
        private final TextView serviceName;
        private final TextView price;
        private final TextView estimated_fixed;
        private final ImageView image;
        private final FrameLayout mFrame_service;
        private final TextView notAvailableText;

        MyViewHolder(View view) {
            super(view);
            mFrame_service = view.findViewById(R.id.frame_service);
            estimated_fixed = view.findViewById(R.id.estimated_fixed);
            serviceName = view.findViewById(R.id.service_name);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            itemView = view.findViewById(R.id.item_view);
            notAvailableText = view.findViewById(R.id.not_available);
        }
    }
}
