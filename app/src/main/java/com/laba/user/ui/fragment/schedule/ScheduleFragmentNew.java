package com.laba.user.ui.fragment.schedule;

import static com.laba.user.base.BaseActivity.RIDE_REQUEST;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.DateDifference;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ScheduleFragmentNew extends BaseFragment implements ScheduleIView {

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

    private SchedulePresenter<ScheduleFragmentNew> presenter = new SchedulePresenter<>();

    public ScheduleFragmentNew() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(BaseActivity.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
            if(!DateDifference.isDateBefore15Days(date.getText().toString())){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Please select a date within 15 days.");
                dialog.setPositiveButton("Yes",null);
                dialog.show();
                return;
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String timeString = "";

                if(selectedHour > 12){
                    timeString = (selectedHour -12)+":"+selectedMinute+" PM";
                }
                else {
                    timeString = (selectedHour)+":"+selectedMinute+" AM";
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

               /* SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = fmt.parse(selectedTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                String formattedTime = fmtOut.format(date);*/
//                time.setText(selectedTime + " "+ AM_PM);
                time.setText(timeString);
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
            dialog.setTitle("Please select a date within 15 days.");
            dialog.setPositiveButton("Yes",null);
            dialog.show();
            return;
        }

        //checking the date is today
        if(DateDifference.isDateToday(date.getText().toString())){
            if(!DateDifference.isTimeAbove1Hours(time.getText().toString())){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("You can only schedule ride before 1 hours of time.");
                dialog.setPositiveButton("Yes",null);
                dialog.show();
                return;
            }

        }

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("schedule_date", date.getText().toString());
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
