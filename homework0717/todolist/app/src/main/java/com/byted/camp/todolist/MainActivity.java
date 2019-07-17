package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;


    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase dbQuery,dbDelete;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todoDbHelper = new TodoDbHelper(this);
        dbQuery = todoDbHelper.getReadableDatabase();
        dbDelete = todoDbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbDelete.close();
        dbQuery.close();
        todoDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        //安全性检查
        if(dbQuery == null){
            return Collections.emptyList();
        }

        List<Note> result = new LinkedList<>();
        cursor = null;
        try{
            //从数据库中获取数据
            cursor = dbQuery.query(TodoContract.TodoNote.TABLE_NAME,
                    null,
                    null,null,null,null,
                    TodoContract.TodoNote.COLUMN_NAME_DATE + " DESC");

            //将数据从cursor转换到JavaBeans--Note
            while (cursor.moveToNext()){
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_NAME_Con));
                long DateMs = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_NAME_DATE));
                int intState = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_NAME_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_NAME_PRIORITY));

                Note note = new Note(id);
                note.setContent(content);
                note.setDate(new Date(DateMs));
                note.setState(State.from(intState));
                note.setPriority(Priority.from(intPriority));

                result.add(note);
            }
        }finally {
            //避免内存泄漏
            if (cursor != null){
                cursor.close();
            }
        }

        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据

//        安全性检查
        if(dbDelete == null){
            Log.d("删除数据失败，表为空", "deleteNote() called with: note = [" + note + "]");
        }

        String selection = TodoContract.TodoNote._ID + "=?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int deletedRow = -1;
        deletedRow = dbDelete.delete(TodoContract.TodoNote.TABLE_NAME,selection,selectionArgs);

        //删除后刷新界面
        notesAdapter.refresh(loadNotesFromDatabase());

        //删除成功、删除失败打印信息
        if (deletedRow != -1){
            Log.d("删除数据成功", "deleteNote() called with: note = [" + note + "]");
        }
        else{
            Log.d("删除数据失败", "deleteNote() called with: note = [" + note + "]");
        }

    }

    private void updateNode(Note note) {
        // 更新数据

        if (dbQuery == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoNote.COLUMN_NAME_STATE, note.getState().intValue);

        int rows = dbQuery.update(TodoContract.TodoNote.TABLE_NAME, values,
                TodoContract.TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

}
