package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;

    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase dbInsert;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        todoDbHelper = new TodoDbHelper(this);
        dbInsert = todoDbHelper.getWritableDatabase();
        radioGroup = findViewById(R.id.priority_radio);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbInsert.close();
        todoDbHelper.close();
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        //安全性检查
        if((dbInsert == null)||TextUtils.isEmpty(content)){
            Log.d("插入数据失败：数据库为空", "saveNote2Database() called with: content = [" + content + "]");
            return false;
        }


        //通过ContentValues写入DB
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoNote.COLUMN_NAME_Con,content);
        values.put(TodoContract.TodoNote.COLUMN_NAME_DATE,System.currentTimeMillis());//获取系统的当前时间
        values.put(TodoContract.TodoNote.COLUMN_NAME_STATE, State.TODO.intValue);
        values.put(TodoContract.TodoNote.COLUMN_NAME_PRIORITY, getpriority().intValue);

        long newRowId = -1;
        newRowId = dbInsert.insert(TodoContract.TodoNote.TABLE_NAME,null,values);
        if(newRowId != -1){
            Log.d("插入数据成功", "saveNote2Database() called with: content = [" + content + "]");
            return true;
        }
        Log.d("插入数据失败", "saveNote2Database() called with: content = [" + content + "]");
        return false;
    }
    public Priority getpriority(){
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.priority_low:
                return Priority.low;
            case R.id.priority_middle:
                return Priority.middle;
            case R.id.priority_high:
                return Priority.high;
            default:
                return Priority.low;
        }
    }
}
