package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // TODO 定义数据库名、版本；创建数据库

    public TodoDbHelper(Context context) {
//        super(context, "todo", null, 0);
        //定义数据库名称、版本
        super(context,"TodoDB",null,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        db.execSQL(TodoContract.CREATETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //增加优先级后，更新数据库版本为
        for(int i = oldVersion;i<newVersion;i++){
            switch (1){
                case 1:
                    db.execSQL(TodoContract.ADDPRIORITY);
                    break;
            }
        }



    }

}
