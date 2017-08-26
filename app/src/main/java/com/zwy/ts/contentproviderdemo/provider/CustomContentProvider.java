package com.zwy.ts.contentproviderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.zwy.ts.contentproviderdemo.database.DatabaseManager;

public class CustomContentProvider extends ContentProvider {


    private static final String AUTHORITY = "com.zwy.custom.provider";

    //userInfo data in db
    private static final String PATH_USER_INFO_ALL = "userInfo";//同时也是table name
    private static final String PATH_USER_INFO_ONLY_ONE = "userInfo/#";
    private static final int CODE_USER_INFO_ALL = 0x100;
    private static final int CODE_USER_INFO_ONLY_ONE = 0x101;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY,PATH_USER_INFO_ALL,CODE_USER_INFO_ALL);
        uriMatcher.addURI(AUTHORITY,PATH_USER_INFO_ONLY_ONE,CODE_USER_INFO_ONLY_ONE);
    }



    public CustomContentProvider() {
    }



    //type rule
    private static final String SINGLE_ITEM = "vnd.android.cursor.item/";
    private static final String MORE_ITEM = "vnd.android.cursor.dir/";


    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        String type = "";
        switch (match){
            case CODE_USER_INFO_ONLY_ONE:
                 type = SINGLE_ITEM + "userInfo.onlyOne";
            break;
            case CODE_USER_INFO_ALL:
                type = MORE_ITEM + "userInfo.list";
            break;
            default:
                break;

        }
        return type;
    }


    @Override
    public boolean onCreate() {
        DatabaseManager.getInstance(getContext());
        return false;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //插入只能是属于一种"全表操作",uri里面 path/# 这种方式是不合法的
        if(uriMatcher.match(uri) == CODE_USER_INFO_ALL){
                int rawId = DatabaseManager.getInstance(getContext()).
                        insert(PATH_USER_INFO_ALL, null,values);
                if(rawId >=0){

                    notifyChange(uri);
                    return ContentUris.withAppendedId(uri,rawId);
                }
        }
        return null;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        int match = uriMatcher.match(uri);
        Cursor cursor = null;
        switch (match){

            case CODE_USER_INFO_ALL://操作数据库userInfo表 返回 查询到所有集合
               cursor = DatabaseManager.getInstance(getContext()).query(PATH_USER_INFO_ALL,
                       projection,selection,selectionArgs,null,null,sortOrder,null);
               break;
            case CODE_USER_INFO_ONLY_ONE://操作数据库userInfo表 返回 特定id
                String id = String.valueOf(ContentUris.parseId(uri));
                String realSelection = selection + "and _id =" +id;
                cursor = DatabaseManager.getInstance(getContext()).query(PATH_USER_INFO_ALL,
                        projection,realSelection,selectionArgs,null,null,sortOrder,null);
                break;
            default:
                break;

        }
        if(cursor != null){
            notifyChange(uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        int resultCode = -1;
        switch (match){
            case CODE_USER_INFO_ALL://全表更新
                resultCode = DatabaseManager.getInstance(getContext()).update(
                        PATH_USER_INFO_ALL, values, selection, selectionArgs);
                break;
            case CODE_USER_INFO_ONLY_ONE://只更新 uri带的id这个数据
                String id = String.valueOf(ContentUris.parseId(uri));
                String realSelection = selection + "and _id="+id;
                resultCode = DatabaseManager.getInstance(getContext()).update(
                        PATH_USER_INFO_ALL, values, realSelection, selectionArgs);
                break;
            default:
                break;
            }
        if(resultCode>=0){
            notifyChange(uri);
        }
        return resultCode;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        int resultCode = -1;
        switch (match){
            case CODE_USER_INFO_ALL://全表筛选删除
                resultCode = DatabaseManager.getInstance(getContext()).
                        delete(PATH_USER_INFO_ALL, selection, selectionArgs);
                break;
            case CODE_USER_INFO_ONLY_ONE://只根据id删除一个，selection 和 selectionArgs将失效
                int id = (int) ContentUris.parseId(uri);
                resultCode = DatabaseManager.getInstance(getContext()).
                        delete(PATH_USER_INFO_ALL, "where _id = ?", new String[]{String.valueOf(id)});
                break;
            default:
                break;
            }
        if(resultCode>=0){
            notifyChange(uri);
        }
        return resultCode;
    }

    private void notifyChange(Uri uri){
        if(getContext()!=null) {
            ContentResolver resolver = getContext().getContentResolver();
            if (resolver != null) {
                resolver.notifyChange(uri, null);
            }
        }
    }

}
