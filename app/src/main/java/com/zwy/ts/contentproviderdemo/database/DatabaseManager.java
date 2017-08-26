package com.zwy.ts.contentproviderdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/8/26.
 */
public class DatabaseManager {
    private static volatile DatabaseManager instance = null;

    private DatabaseOpenHelper myDatabaseOpenHelper;

    private DatabaseManager(Context context){
        myDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseManager getInstance(Context context){
        if(instance == null){
            synchronized (DatabaseManager.class){
                if(instance == null){
                    instance = new DatabaseManager(context);
                }
            }
        }
        return instance;
    }


    public int insert(String table, String nullColumnHack, ContentValues values){
        SQLiteDatabase database = getDatabase();
        if(database != null){
            return (int) database.insert(table,nullColumnHack,values);
        }
        return -1;
    }


    public int delete(String table, String whereClause, String[] whereArgs){
        SQLiteDatabase database = getDatabase();
        if(database != null){
            return database.delete(table,whereClause,whereArgs);
        }
        return -1;
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit){
        SQLiteDatabase database = getDatabase();
        if(database != null) {
            return    database.query(table, columns, selection,
                    selectionArgs, groupBy, having,
                    orderBy, limit);
        }
        return null;
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase database = getDatabase();
        if(database!=null) {
            return database.update(table, values, whereClause, whereArgs);
        }
        return -1;
    }

    public Cursor queryAll(String table,String[] columns){
        SQLiteDatabase database = getDatabase();
        if(database != null) {
            return    database.query(table, columns, null,
                    null, null, null,
                    null, null);
        }
        return null;
    }


    private SQLiteDatabase getDatabase(){
        if(myDatabaseOpenHelper!=null){
            return myDatabaseOpenHelper.getWritableDatabase();
        }
        return null;
    }
}
