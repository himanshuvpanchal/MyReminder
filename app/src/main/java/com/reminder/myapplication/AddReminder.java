package com.reminder.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.Edits;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddReminder extends AppCompatActivity implements View.OnClickListener {
    EditText txtReminder, txtReminderDesc, txtBeforeDay;
    TextView txtDate, txtTime;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Switch swtEveryDay;
    Button btnSave, btnCancel;
    DatabaseHelper db;
    Common common;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        txtReminder = findViewById(R.id.txtReminder);
        txtReminderDesc = findViewById(R.id.txtReminderDesc);
        txtDate = findViewById(R.id.txtDate);
        txtBeforeDay = findViewById(R.id.txtBeforeDay);
        txtTime = findViewById(R.id.txtTime);
        swtEveryDay = ((Switch) findViewById(R.id.swtEveryDay));

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        db = new DatabaseHelper(this);
        common = new Common(this, (AlarmManager) getSystemService(Context.ALARM_SERVICE));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                if (txtDate.getText().length() > 0) {
                    String[] separated = txtDate.getText().toString().split("/");
                    mDay = Integer.parseInt(separated[1]);
                    mMonth = Integer.parseInt(separated[0]) - 1;
                    mYear = Integer.parseInt(separated[2]);
                }
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AddReminder.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                txtDate.setText(((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "/"
                                        + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                if (txtTime.getText().length() > 0) {
                    String[] separated = txtTime.getText().toString().split(":");
                    mHour = Integer.parseInt(separated[0]);
                    mMinute = Integer.parseInt(separated[1]);
                }
                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(AddReminder.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                //txtTime.setText(hourOfDay + ":" + minute);
                                txtTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" +
                                        (minute < 10 ? "0" + minute : minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            try {
                if (txtReminder.getText().toString().trim().length() == 0 && txtReminderDesc.getText().toString().trim().length() == 0
                        && txtDate.getText().toString().trim().length() == 0 && txtTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please add required field ", Toast.LENGTH_SHORT).show();
                } else {
                    java.util.Date date = null;
                    String FinalDate = txtDate.getText().toString() + " " + txtTime.getText().toString();
                    SimpleDateFormat datetime = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    date = datetime.parse(FinalDate);
                    if (date.before(new Date())) {
                        Toast.makeText(getApplicationContext(), "Past date not allow ", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isEdit = db.getDataByDateAndTime(0, txtDate.getText().toString(), txtTime.getText().toString());
                        if (isEdit) {
                            Toast.makeText(getApplicationContext(), "Reminder exists with same date and time", Toast.LENGTH_SHORT).show();
                        } else {
                            long Id = db.addData(txtReminder.getText().toString(), txtReminderDesc.getText().toString(), txtDate.getText().toString(),
                                    Integer.parseInt(txtBeforeDay.getText().toString()), txtTime.getText().toString(), swtEveryDay.isChecked());
                            common.updateLabel(txtDate.getText().toString(), txtTime.getText().toString(), (int) Id, txtReminder.getText().toString(), txtReminderDesc.getText().toString());

                            Intent launchActivity = new Intent(getApplicationContext(), MainActivity.class);
                            launchActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(launchActivity);
                            finish();
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Intent launchActivity = new Intent(getApplicationContext(), MainActivity.class);
            launchActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchActivity);
            finish();
        }
    }
}
