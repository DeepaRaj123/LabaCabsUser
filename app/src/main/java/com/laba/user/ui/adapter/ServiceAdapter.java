package com.laba.user.ui.adapter;

import static com.laba.user.base.BaseActivity.RIDE_REQUEST;
import static com.laba.user.base.BaseActivity.getNewNumberFormat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.laba.user.R;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.EstimateFare;
import com.laba.user.data.network.model.Service;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.fragment.RateCardFragment;
import com.laba.user.ui.fragment.service.ServiceFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private final List<Service> list;
    private final TextView capacity;
    private final List<String> AvailableTime;
    private Context context;
    private final List<Integer> list1;
    private final List<String> list2;
    List<String> list3 = new ArrayList<String>();
    private int lastCheckedPos = -1;
    private final Animation zoomIn;
    private final ServiceFragment.ServiceListener mListener;
    private EstimateFare estimateFare;
    private boolean canNotifyDataSetChanged = false;
    SharedPreferences prefs;
    Set<String> set = new HashSet<String>();
    String dropOffTime;
    String arrivalTime1,arrivalTime;
    private RecyclerView.RecyclerListener listener;

    private final ServiceAdapterOnClickListener onClickListener;
    private Object Locale;
    ProgressDialog progressDialog;


    public interface ServiceAdapterOnClickListener {
        void adapterClick(Service service);
    }

    public ServiceAdapter(Context context, List<Service> list, List<Integer> list1, List<String> list2,
                          ServiceFragment.ServiceListener listener,
                          TextView capacity, EstimateFare fare, String dropOffTime, List<String> AvailableTime, ServiceAdapterOnClickListener onClickListener, int lastCheckedPos) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.list = list;
        this.list1 = list1;
        this.list2 = list2;
        this.capacity = capacity;
        this.mListener = listener;
        this.estimateFare = fare;
        this.dropOffTime = dropOffTime;
        this.AvailableTime = AvailableTime;
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
        this.onClickListener = onClickListener;
        this.lastCheckedPos = lastCheckedPos;
        progressDialog = new ProgressDialog(this.context);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"StringFormatMatches", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {


        showLoading();
        holder.estimated_fixed.setVisibility(View.INVISIBLE);

        if(list2.size()==list.size()){

            for (int i = 0; i < list.size(); i++) {

                for (int j = 0; j < list.size(); j++) {
                    if (list1.get(j) == i) {
                        list3.add(i, list2.get(j));
                        if(list3.size()==list.size()){
                            break;
                        }

                    }
                }
            }
        }

        Service service = list.get(position);
        if (service != null)
            holder.serviceName.setText(service.getName());


        if (estimateFare != null) {

            long finaltime = calculateTime(estimateFare.getTime());
            int finalhour = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                finalhour = Math.toIntExact(finaltime / (60 * 60));
            }
            int finalminute = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                finalminute = Math.toIntExact((finaltime %((60*60))/60));
            }


                if (finalhour > 12) {
                    if (finalminute < 9) {
                        arrivalTime = finalhour - 12 + ":0" + finalminute + " AM";
                    } else {
                        arrivalTime = finalhour - 12 + ":" + finalminute + " AM";
                    }
                } else {
                    if (finalminute < 9) {
                        arrivalTime = finalhour + ":0" + finalminute + " PM";
                    } else {
                        arrivalTime = finalhour + ":" + finalminute + " PM";
                    }
                }

        if(service.isAvailable()&&(position == lastCheckedPos&&AvailableTime.size()!=0) && canNotifyDataSetChanged) {
            holder.price.setText(AvailableTime.get(position));
            holder.notAvailableText.setText("Available - ");
            holder.notAvailableText.setTextColor(
                    context.getResources().getColor(R.color.colorPrimary)
            );
            holder.price.setTextColor(
                    context.getResources().getColor(R.color.colorPrimary)
            );
        }else{
            holder.price.setText(arrivalTime);
            holder.notAvailableText.setText("Drop Off - ");
            holder.notAvailableText.setTextColor(
                    context.getResources().getColor(R.color.black));
            holder.price.setTextColor(
                    context.getResources().getColor(R.color.black)
            );
        }

            if (list2.size()==list.size()){
                hideloading();
                holder.estimated_fixed.setVisibility(View.VISIBLE);
                String string = String.valueOf(list3.get(position));
                int count = 0;

                //Counts each character except space
                for(int i = 0; i < string.length(); i++) {
                    if(string.charAt(i) != ' ')
                        count++;
                }
                if(count>6){
                    holder.estimated_fixed.setTextSize(17);}
                holder.estimated_fixed.setText(SharedHelper.getKey(context, "currency") + "" + getNewNumberFormat(Double.parseDouble(String.valueOf(list3.get(position)))));

            }
        }
        Glide.with(context)
                .load(service.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_car).dontAnimate().error(R.drawable.ic_car))
                .into(holder.image);

       // Log.e("SERVICES", "Selected: " + lastCheckedPos + " | Current: " + position + " | Available: " + service.isAvailable());

        if ((position == lastCheckedPos) && canNotifyDataSetChanged) {

            canNotifyDataSetChanged = false;
            capacity.setText(String.valueOf(service.getCapacity()));
              holder.itemView.setBackgroundColor(Color.rgb(220,220,220));

            holder.price.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1);
            holder.estimated_fixed.setVisibility(View.VISIBLE);


        } else {
              holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
              holder.serviceName.setTextColor(context.getResources().getColor(R.color.black));
              holder.notAvailableText.setTextColor(context.getResources().getColor(R.color.black));
              holder.price.setTextColor(context.getResources().getColor(R.color.black));
          }

        holder.itemView.setOnClickListener(view -> {
            Log.e("count", String.valueOf(position));
            holder.itemView.setBackgroundColor(Color.rgb(0,0,0));

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


    private long calculateTime(String time) {
        int hour = 0;
        int min=0;
        int finalhour=0;
        int finalmin=0;
        long finaltime=0;
        int hourm=0;
        int minm=0;
        int timem=0;
        long finalm=0;
        long hour24hrs,minutes;

        String[] separated = time.split(" ");

        if (time.toLowerCase().contains("hours") || time.toLowerCase().contains("hour")) {
            hour = Integer.parseInt(separated[0]);
            min = Integer.parseInt(separated[2]);
        }else if (time.toLowerCase().contains("mins") || time.toLowerCase().contains("min")) {
            min = Integer.parseInt(separated[0]);
        }
        hourm = hour * 60 * 60;
        minm = 60 * min;
        timem = hourm + minm;
        Calendar calendar = Calendar.getInstance();
        hour24hrs = calendar.get(Calendar.HOUR);
        minutes = calendar.get(Calendar.MINUTE);

        finalm = (60 * minutes) + (hour24hrs*60*60);

        finaltime = timem + finalm;

        return finaltime;

    }



    private void hideloading() {
        if (progressDialog != null) if (progressDialog.isShowing())
        progressDialog.dismiss();
    }

    private void showLoading() {
        if (!progressDialog.isShowing()) {
        progressDialog.show();
    }

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
