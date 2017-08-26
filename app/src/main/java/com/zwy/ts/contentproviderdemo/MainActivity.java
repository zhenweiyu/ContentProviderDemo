package com.zwy.ts.contentproviderdemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.zwy.ts.contentproviderdemo.database.DatabaseManager;

public class MainActivity extends Activity {

    private EditText editNameInsert;
    private EditText editAgeInsert;
    private Button btnInsert;
    private EditText editNameQuery;
    private Button btnQuery;
    private EditText editNameNeedUpdate;
    private EditText editNameUpdate;
    private EditText editAgeUpdate;
    private Button btnUpdate;
    private EditText editNameDelete;
    private Button btnDelete;
    private ListView listView;
    private ContentResolver contentResolver;
    private static final String AUTHORITY = "com.zwy.custom.provider";
    //userInfo数据改变后指定通知的Uri
    private static final Uri URI_ALL_USER_INFO = Uri.parse("content://" + AUTHORITY + "/userInfo");
    //private static final Uri URI_ONLY_ONE_USER_INFO = Uri.parse("content://" + AUTHORITY + "/userInfo/#");
    private UserInfoContentObserver userInfoContentObserver;

    private Cursor userInfoCursor;
    private SimpleCursorAdapter cursorAdapter;
    private boolean isQuery = false;

    private static final int REFRESH_DATA = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == REFRESH_DATA){
                if(!isQuery) {
                    userInfoCursor = DatabaseManager.getInstance(getApplicationContext()).queryAll("userInfo",
                            new String[]{"_id", "username", "age"});
                }
                Toast.makeText(getApplicationContext(),"进行刷新...",Toast.LENGTH_SHORT).show();
                if(userInfoCursor !=null){
                    cursorAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.layout_user_info_item,
                                    userInfoCursor, new String[]{"_id", "username", "age"},
                                    new int[]{R.id.tv_user_id, R.id.tv_user_name, R.id.tv_user_age});
                    listView.setAdapter(cursorAdapter);
                    cursorAdapter.notifyDataSetChanged();

                    }
                }

            }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editNameInsert = (EditText) findViewById(R.id.edit_name_insert);
        editAgeInsert = (EditText) findViewById(R.id.edit_age_insert);
        btnInsert = (Button) findViewById(R.id.btn_insert);
        editNameQuery = (EditText) findViewById(R.id.edit_name_query);
        btnQuery = (Button) findViewById(R.id.btn_query);
        editNameNeedUpdate = (EditText)findViewById(R.id.edit_name_old_update);
        editNameUpdate = (EditText) findViewById(R.id.edit_name_update);
        editAgeUpdate = (EditText) findViewById(R.id.edit_age_update);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        editNameDelete = (EditText) findViewById(R.id.edit_name_delete);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        listView = (ListView)findViewById(R.id.lv_user_info);

        //使用ContentResolver操作数据库
        contentResolver = getContentResolver();
        userInfoContentObserver = new UserInfoContentObserver(mHandler);
        contentResolver.registerContentObserver(URI_ALL_USER_INFO,true,userInfoContentObserver);
        //contentResolver.registerContentObserver(URI_ONLY_ONE_USER_INFO,false,userInfoContentObserver);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNameInsert.getText().toString();
                String age = editAgeInsert.getText().toString();
                ContentValues values = new ContentValues();
                values.put("username", name);
                values.put("age", age);
                isQuery = false;
                contentResolver.insert(URI_ALL_USER_INFO, values);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName = editNameNeedUpdate.getText().toString();
                String name = editNameUpdate.getText().toString();
                String age = editAgeUpdate.getText().toString();
                ContentValues values = new ContentValues();
                values.put("username", name);
                values.put("age", age);
                String selection = null;
                String []selectArgs = null;
                if(!TextUtils.isEmpty(oldName)){
                    selection = "username=?";
                    selectArgs = new String[]{oldName};
                }
                isQuery = false;
                contentResolver.update(URI_ALL_USER_INFO, values, selection, selectArgs);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNameDelete.getText().toString();
                String selection = null;
                String []selectArgs = null;
                if(!TextUtils.isEmpty(name)){
                    selection = "username = ?";
                    selectArgs = new String[]{name};
                }
                isQuery = false;
                contentResolver.delete(URI_ALL_USER_INFO, selection, selectArgs);
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNameQuery.getText().toString();
                String [] column = new String[]{"_id","username","age"};
                String selection = null;
                String []selectArgs = null;
                if(!TextUtils.isEmpty(name)){
                     selection = "username = ?";
                     selectArgs = new String[]{name};
                }
                isQuery = true;
                userInfoCursor =contentResolver.query(URI_ALL_USER_INFO, column, selection, selectArgs, null);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(contentResolver!=null){
            contentResolver.unregisterContentObserver(userInfoContentObserver);
        }
    }

    public class UserInfoContentObserver extends ContentObserver{

        private Handler handler;

        public UserInfoContentObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }


        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.v("UserInfoContentObserver",uri.toString());
            handler.sendEmptyMessage(REFRESH_DATA);
        }
    }


}
