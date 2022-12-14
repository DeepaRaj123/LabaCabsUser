package com.laba.user.ui.fragment.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.DateDifference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.laba.user.base.BaseActivity.RIDE_REQUEST;

public class ScheduleFragment extends BaseFragment implements ScheduleIView {

    Unbinder binder;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;

    private String selectedScheduledTime;
    private String selectedScheduledHour;
    private String AM_PM;
    private String selectedTime;

    private SchedulePresenter<ScheduleFragment> presenter = new SchedulePresenter<>();

    public ScheduleFragment() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

//            date.setText(BaseActivity.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
            date.setText(BaseActivity.STYLE_DATE_FORMAT.format(myCalendar.getTime()));

            if(!DateDifference.isDateBefore15Days(date.getText().toString())){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getString(R.string.date_15_days));
                dialog.setPositiveButton("Yes",null);
                dialog.show();
                return;
            }

        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String timeString = "";

                if(selectedHour == 0){
                    String minute = selectedMinute+"";
                    if((selectedMinute/10) == 0){
                        minute = String.format("%02d" , selectedMinute);
                    }

                    timeString = "12:"+minute+" AM";
                }
                else if(selectedHour == 12){
                    String minute = selectedMinute+"";
                    if((selectedMinute/10) == 0){
                        minute = String.format("%02d" , selectedMinute);
                    }


                    timeString = "12:"+minute+" PM";
                }
                else if(selectedHour > 12){
                    String minute = selectedMinute+"";
                    if((selectedMinute/10) == 0){
                        minute = String.format("%02d" , selectedMinute);
                    }

                    int hours = selectedHour - 12;
                    String hourSt = hours+"";
                    if(hours/10 == 0){
                        hourSt = String.format("%02d" , hours);
                    }

//                    timeString = (selectedHour -12)+":"+selectedMinute+" PM";
                    timeString = hourSt+":"+minute+" PM";
                }
                else {
                    String minute = selectedMinute+"";
                    if((selectedMinute/10) == 0){
                        minute = String.format("%02d" , selectedMinute);
                    }

                    String hourSt = String.format("%02d" , selectedHour);


                    timeString = hourSt+":"+minute+" AM";
                }

                if (selectedMinute < 10) {
                    selectedScheduledTime = "0" + selectedMinute;
                } else {
                    selectedScheduledTime = String.valueOf(selectedMinute);
                }
                if (selectedHour < 10) {
                    selectedScheduledHour = "0" + selectedHour;
                } else {
                    selectedScheduledHour = String.valueOf(selectedHour);
                }
                selectedTime = selectedScheduledHour + ":" + selectedScheduledTime;
                if (selectedHour < 12){
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }


                time.setText(timeString);


                if(DateDifference.isDateToday(date.getText().toString())){
                    String dateSt = date.getText().toString();
                    String timeSt = time.getText().toString();
                    String timeDiff = dateSt + " " +timeSt;

//                    if(!DateDifference.isTimeAbove1Hours(time.getText().toString())){
                    if(!DateDifference.isTimeAbove1Hours(timeDiff)){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getString(R.string.time_error));
                        dialog.setPositiveButton("Yes",null);
                        dialog.show();
                        return;
                    }

                }
            }
        };
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    public View initView(View view) {
        binder = ButterKnife.bind(this, view);
        presenter.attachView(this);

//        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat smf = new SimpleDateFormat("dd MMM yyyy");
        String todayDate = smf.format(new Date());
        date.setText(todayDate);

        SimpleDateFormat timef = new SimpleDateFormat("hh:mm aa");
        String nowTime = timef.format(new Date());
        selectedTime = nowTime;
        time.setText(nowTime);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.date, R.id.time, R.id.schedule_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date:
                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.schedule_request:
                sendRequest();
                break;
        }
    }

    private void sendRequest() {
        //checking date or time is empty
        if (date.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(activity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
            return;
        }

        //checking date is before 7days
        if(!DateDifference.isDateBefore15Days(date.getText().toString())){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.date_15_days));
            dialog.setPositiveButton("Yes",null);
            dialog.show();
            return;
        }

        //checking the date is today
        if(DateDifference.isDateToday(date.getText().toString())){
            String dateSt = date.getText().toString();
            String timeSt = time.getText().toString();
            String timeDiff = dateSt + " " +timeSt;

//            if(!DateDifference.isTimeAbove1Hours(time.getText().toString())){
            if(!DateDifference.isTimeAbove1Hours(timeDiff)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getString(R.string.time_error));
                dialog.setPositiveButton("Okay",null);
                dialog.show();
                return;
            }

        }

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        String st_date = date.getText().toString();
        String convertedDate = new DateDifference().convertDateFormats(st_date,
                "dd MMM yyyy","yyyy-MM-dd");
        Log.e("convertedDate",convertedDate);

//        map.put("schedule_date", date.getText().toString());
        map.put("schedule_date", convertedDate);
        map.put("schedule_time", selectedTime);
        showLoading();
        presenter.sendRequest(map);
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Intent intent = new Intent("INTENT_FILTER");
        activity().sendBroadcast(intent);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
    
}
