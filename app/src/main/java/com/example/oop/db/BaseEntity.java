package com.example.oop.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Entity(tableName = "base")
public class BaseEntity {
    public static final String BASETABLE_COL_ID = BaseColumns._ID;
    public static final String BASETABLE_COL_URL = "url";
    public static final String BASETABLE_COL_TITLE = "title";
    public static final String BASETABLE_COL_IMAGE = "image";
    public static final String BASETABLE_COL_PRICE = "price";
    public static final String BASETABLE_COL_DATE = "date";
    public static final String BASETABLE_NAME_PLACEHOLDER = ":tablename:";
    public static final String BASETABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + BASETABLE_NAME_PLACEHOLDER
            + "("+BASETABLE_COL_ID+" INTEGER PRIMARY KEY, "+ BASETABLE_COL_URL + " TEXT, " + BASETABLE_COL_TITLE + " TEXT, "+ BASETABLE_COL_IMAGE + " TEXT, "
            + BASETABLE_COL_PRICE + " TEXT, "+ BASETABLE_COL_DATE + " TEXT)";
    @PrimaryKey
    @ColumnInfo(name = BASETABLE_COL_ID)
    Long id;
    @ColumnInfo(name = BASETABLE_COL_URL)
    String url;
    @ColumnInfo(name = BASETABLE_COL_TITLE)
    String title;
    @ColumnInfo(name = BASETABLE_COL_IMAGE)
    String image;
    @ColumnInfo(name = BASETABLE_COL_PRICE)
    String price;
    @ColumnInfo(name = BASETABLE_COL_DATE)
    String date;

    public BaseEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Ignore
    public static Long insertRow(SupportSQLiteDatabase sdb, String tableName, String url, String title, String price, String date, String image) {
        ContentValues cv = new ContentValues();
        cv.put(BASETABLE_COL_URL,url);
        cv.put(BASETABLE_COL_TITLE,title);
        cv.put(BASETABLE_COL_PRICE,price);
        cv.put(BASETABLE_COL_IMAGE,image);
        cv.put(BASETABLE_COL_DATE,date);
        return sdb.insert(tableName, OnConflictStrategy.IGNORE,cv);
    }
    @Ignore
    public static BaseEntity getLast(SupportSQLiteDatabase sdb, String tableName) {
        Cursor cursor = sdb.query("SELECT * FROM " + tableName,null);
        cursor.moveToLast();
        BaseEntity model = new BaseEntity();
        model.setUrl(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_URL)));
        model.setTitle(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_TITLE)));
        model.setImage(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_IMAGE)));
        model.setPrice(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_PRICE)));
        model.setDate(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_DATE)));
        return model;
    }
    @Ignore
    public static ArrayList<String> getDates(SupportSQLiteDatabase sdb, String tableName) {
        Cursor cursor = sdb.query("SELECT * FROM " + tableName,null);
        cursor.moveToFirst();
        ArrayList<String> arrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(BASETABLE_COL_DATE)));
                cursor.moveToNext();
            }
        }
        return arrayList;
    }
    @Ignore
    public static ArrayList<Integer> getPrices(SupportSQLiteDatabase sdb, String tableName) {
        Cursor cursor = sdb.query("SELECT * FROM " + tableName,null);
        cursor.moveToFirst();
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                arrayList.add(cursor.getInt(cursor.getColumnIndex(BASETABLE_COL_PRICE)));
                cursor.moveToNext();
            }
        }
        return arrayList;
    }

    @Ignore
    public static int getTableRowCount(SupportSQLiteDatabase sdb,String tableName) {
        int rv = 0;
        Cursor csr = sdb.query("SELECT * FROM " + tableName,null);
        rv = csr.getCount();
        csr.close();
        return rv;
    }
}
