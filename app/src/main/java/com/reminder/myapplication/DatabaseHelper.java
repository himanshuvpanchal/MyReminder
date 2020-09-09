package com.reminder.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ReminderDB";
    public static final String TABLE_NAME = "reminder_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "Reminder";
    public static final String COL3 = "ReminderDesc";
    public static final String COL4 = "Date";
    public static final String COL5 = "BeforeDay";
    public static final String COL6 = "Time";
    public static final String COL7 = "EveryDay";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Reminder TEXT," +
                "ReminderDesc TEXT,Date TEXT,BeforeDay INTEGER,Time TEXT,EveryDay INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addData(String Reminder, String ReminderDesc, String Date, Integer BeforeDay, String Time, Boolean EveryDay
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, Reminder);
        contentValues.put(COL3, ReminderDesc);
        contentValues.put(COL4, Date);
        contentValues.put(COL5, BeforeDay);
        contentValues.put(COL6, Time);
        contentValues.put(COL7, EveryDay);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result;
    }

    public boolean updateData(Integer Id, String Reminder, String ReminderDesc, String Date, Integer BeforeDay, String Time, Boolean EveryDay
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, Reminder);
        contentValues.put(COL3, ReminderDesc);
        contentValues.put(COL4, Date);
        contentValues.put(COL5, BeforeDay);
        contentValues.put(COL6, Time);
        contentValues.put(COL7, EveryDay);

        long result = db.update(TABLE_NAME, contentValues, "ID=" + Id, null);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY  Date asc ,Time asc", null);
        return data;
    }

    public Cursor getDataByID(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + ID, null);
        if (data != null) {
            data.moveToFirst();
        }
        return data;
    }

    public boolean getDataByDateAndTime(int ID, String Date, String Time) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (ID > 0) {
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID != " + ID + " AND Date = '" + Date + "' AND TIME = '" + Time + "'", null);
            if (data != null) {
                data.moveToFirst();
            }
            return data.getCount() > 0 ? true : false;
        } else {
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE Date = '" + Date + "' AND TIME = '" + Time + "'", null);
            if (data != null) {
                data.moveToFirst();
            }
            return data.getCount() > 0 ? true : false;
        }
    }

    public Cursor getDataBySearch(String Reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE Reminder LIKE '%" + Reminder + "%' ORDER BY  Date asc ,Time asc", null);
        return data;
    }
    public boolean delete(Integer ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID=" + ID, null) > 0;
    }
}
