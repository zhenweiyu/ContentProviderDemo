package com.zwy.ts.contentproviderdemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/8/26.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final String CREATE_TABLE_SQL = "create table userInfo ("
            +"_id integer primary key autoincrement,"
            +"username text,"
            +"age text"
            +")";

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "USER_INFO.db";
    private Context context;


    public DatabaseOpenHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
