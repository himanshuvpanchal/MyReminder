package com.reminder.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lvReminders;
    EditText txtSearch;
    DatabaseHelper db;
    ArrayList<Reminder> remindersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvReminders = findViewById(R.id.lvReminders);
        txtSearch = findViewById(R.id.txtSearch);
        db = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchActivity = new Intent(getApplicationContext(), AddReminder.class);
                startActivity(launchActivity);
            }
        });
        Cursor data = db.getData();
        BindListView(data);

        lvReminders.setOnItemClickListener(this);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Cursor data = db.getDataBySearch(s.toString());
                BindListView(data);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txtIDList = (TextView) view.findViewById(R.id.txtIDList);
        Intent launchActivity = new Intent(getApplicationContext(), Edit.class);
        launchActivity.putExtra("ReminderId", Integer.parseInt(txtIDList.getText().toString()));
        startActivity(launchActivity);
    }

    public void BindListView(Cursor data) {
        remindersList = new ArrayList<>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    String _24HourTime = data.getString(5);
                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                    Date _24HourDt = _24HourSDF.parse(_24HourTime);
                    SimpleDateFormat olddateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                    Date newdate = olddateFormat.parse(data.getString(3));
                    remindersList.add(new Reminder(data.getString(0), data.getString(1),
                            dateFormat.format(newdate) + " " + _12HourSDF.format(_24HourDt)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        final ReminderListAdapter reminderListAdapter =
                new ReminderListAdapter(this, R.layout.reminderlist_adapter, remindersList);
        lvReminders.setAdapter(reminderListAdapter);
        lvReminders.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvReminders.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO  Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO  Auto-generated method stub
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                // TODO  Auto-generated method stub
                mode.getMenuInflater().inflate(R.menu.list_option, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                // TODO  Auto-generated method stub
                switch (item.getItemId()) {
                    case R.id.selectAll:
                        final int checkedCount = remindersList.size();
                        reminderListAdapter.removeSelection();
                        for (int i = 0; i < checkedCount; i++) {
                            lvReminders.setItemChecked(i, true);
                            reminderListAdapter.toggleSelection(i, true);
                        }
                        mode.setTitle(checkedCount + "  Selected");
                        return true;
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this);
                        builder.setMessage("Do you  want to delete selected record(s)?");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO  Auto-generated method stub
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO  Auto-generated method stub
                                SparseBooleanArray selected = reminderListAdapter.getSelectedIds();
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        Reminder selecteditem = reminderListAdapter.getItem(selected.keyAt(i));
                                        // Remove  selected items following the ids
                                        boolean deleted = db.delete(Integer.parseInt(selecteditem.getID()));
                                        if (deleted) {
                                            Intent notificationIntent = new Intent(getApplicationContext(), MyNotificationPublisher.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(selecteditem.getID()), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                            alarmManager.cancel(pendingIntent);
                                            reminderListAdapter.remove(selecteditem);
                                        }
                                    }
                                }
                                // Close CAB
                                mode.finish();
                                selected.clear();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Confirmation"); // dialog  Title
                        alert.show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // TODO  Auto-generated method stub
                final int checkedCount = lvReminders.getCheckedItemCount();
                // Set the  CAB title according to total checked items
                mode.setTitle(checkedCount + "  Selected");
                // Calls  toggleSelection method from ListViewAdapter Class
                reminderListAdapter.toggleSelection(position, checked);
            }
        });
    }
}
