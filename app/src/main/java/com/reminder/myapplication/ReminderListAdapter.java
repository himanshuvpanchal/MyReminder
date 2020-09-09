package com.reminder.myapplication;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReminderListAdapter extends ArrayAdapter<Reminder> {

    private LayoutInflater mInflater;
    private ArrayList<Reminder> reminders;
    private int mviewResourceId;
    private SparseBooleanArray mSelectedItemsIds;

    public ReminderListAdapter(Context context, int textViewResourceId, ArrayList<Reminder> reminders) {
        super(context, textViewResourceId, reminders);
        this.reminders = reminders;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mviewResourceId = textViewResourceId;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(mviewResourceId, null);

        Reminder reminder = reminders.get(position);
        if (reminder != null) {
            TextView ID = (TextView) convertView.findViewById(R.id.txtIDList);
            TextView NAME = (TextView) convertView.findViewById(R.id.txtNameList);
            TextView Date = (TextView) convertView.findViewById(R.id.txtDate);
            if (ID != null) {
                ID.setText(reminder.getID());
            }
            if (NAME != null) {
                NAME.setText(reminder.getNAME());
            }
            if (Date != null) {
                Date.setText(reminder.getDate());
            }
        }
        return convertView;
    }

    @Override
    public void remove(Reminder object) {
        reminders.remove(object);
        notifyDataSetChanged();
    }

    // get List after update or delete
    public void toggleSelection(int position, boolean value) {
        selectView(position, value);
    }

    // Remove selection after unchecked
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    // Item checked on selection
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    // Get number of selected item
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
