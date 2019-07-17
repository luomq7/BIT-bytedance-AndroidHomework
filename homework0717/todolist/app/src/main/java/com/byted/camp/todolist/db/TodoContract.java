package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static class TodoNote implements BaseColumns{
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_Con= "con";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE= "state";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }
    public static final String CREATETABLE =
            "CREATE TABLE " + TodoNote.TABLE_NAME + "("
                    + TodoNote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TodoNote.COLUMN_NAME_Con + " TEXT, "
                    + TodoNote.COLUMN_NAME_DATE + " INTEGER, "
                    +TodoNote.COLUMN_NAME_STATE + " INTEGER"
                    +TodoNote.COLUMN_NAME_PRIORITY + " INTEGER)";

    public static final String ADDPRIORITY =
            "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_NAME_PRIORITY + " INTEGER";



    private TodoContract() {
    }

}
