package com.reminder.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Edit extends AppCompatActivity implements View.OnClickListener {
    EditText txtReminder, txtReminderDesc, txtBeforeDay;
    TextView txtDate, txtTime;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Switch swtEveryDay;
    Button btnSave, btnCancel;
    DatabaseHelper db;
    int intValue = 0;
    Common common;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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
        Intent mIntent = getIntent();
        intValue = mIntent.getIntExtra("ReminderId", 0);
        //Toast.makeText(getApplicationContext(), "ID " + intValue, Toast.LENGTH_SHORT).show();
        if (intValue > 0) {
            Cursor data = db.getDataByID(intValue);
            if (data.getCount() > 0) {
                //Toast.makeText(getApplicationContext(), "ID " + data.getString(0), Toast.LENGTH_SHORT).show();
                txtReminder.setText(data.getString(1));
                txtReminderDesc.setText(data.getString(2));
                txtDate.setText(data.getString(3));
                txtBeforeDay.setText(data.getString(4));
                txtTime.setText(data.getString(5));
                swtEveryDay.setChecked(Integer.parseInt(data.getString(6)) != 0);
            }
        }
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
                datePickerDialog = new DatePickerDialog(Edit.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                //txtDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
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
                timePickerDialog = new TimePickerDialog(Edit.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

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
            if (intValue > 0) {
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
                            boolean isEdit = db.getDataByDateAndTime(intValue, txtDate.getText().toString(), txtTime.getText().toString());
                            if (isEdit) {
                                Toast.makeText(getApplicationContext(), "Reminder exists with same date and time", Toast.LENGTH_SHORT).show();
                            } else {
                                db.updateData(intValue, txtReminder.getText().toString(), txtReminderDesc.getText().toString(), txtDate.getText().toString(),
                                        Integer.parseInt(txtBeforeDay.getText().toString()), txtTime.getText().toString(), swtEveryDay.isChecked());

                                common.updateLabel(txtDate.getText().toString(), txtTime.getText().toString(), intValue, txtReminder.getText().toString(), txtReminderDesc.getText().toString());
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
                Toast.makeText(getApplicationContext(), "Id not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent launchActivity = new Intent(getApplicationContext(), MainActivity.class);
            launchActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchActivity);
            finish();
        }
    }
}
