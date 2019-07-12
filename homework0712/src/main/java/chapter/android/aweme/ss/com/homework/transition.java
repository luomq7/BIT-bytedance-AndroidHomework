package chapter.android.aweme.ss.com.homework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class transition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        String data = getIntent().getStringExtra("提示");
        TextView tv = findViewById(R.id.tips);
        tv.setText(data);
    }
}
