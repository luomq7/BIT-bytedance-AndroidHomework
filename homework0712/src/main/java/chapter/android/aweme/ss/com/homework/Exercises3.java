package chapter.android.aweme.ss.com.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 大作业:实现一个抖音消息页面,所需资源已放在res下面
 */
public class Exercises3 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);
        ListView list_item = findViewById(R.id.list_item);

        //Data
        String title[] = {"游戏小助手","抖音小助手","系统消息","我是豆豆啊哇塞","陌生人消息","喂喂卫","李","tesyyu","游戏小助手","抖音小助手","系统消息","我是豆豆啊哇塞","陌生人消息","喂喂卫","李","tesyyu","游戏小助手","抖音小助手","系统消息","我是豆豆啊哇塞","陌生人消息","喂喂卫","李","tesyyu"};
        String text[] = {"抖出好游戏","收下我的双下巴祝福","系统消息","转发视频","转发直播","Hi","转发视频","I'm tesyyu","抖出好游戏","收下我的双下巴祝福","系统消息","转发视频","转发直播","Hi","转发视频","I'm tesyyu","抖出好游戏","收下我的双下巴祝福","系统消息","转发视频","转发直播","Hi","转发视频","I'm tesyyu"};
        int imgSrc[] = {R.drawable.icon_micro_game_comment,R.drawable.session_robot,R.drawable.session_system_notice,R.drawable.icon_blacksend_touch,R.drawable.session_stranger,
                R.drawable.icon_micro_game_comment,R.drawable.session_robot,R.drawable.session_system_notice,R.drawable.icon_blacksend_touch,R.drawable.session_stranger,
                R.drawable.icon_micro_game_comment,R.drawable.session_robot,R.drawable.session_system_notice,R.drawable.icon_blacksend_touch,R.drawable.session_stranger,
                R.drawable.icon_micro_game_comment,R.drawable.session_robot,R.drawable.session_system_notice,R.drawable.icon_blacksend_touch,R.drawable.session_stranger,
                R.drawable.icon_micro_game_comment,R.drawable.session_robot,R.drawable.session_system_notice,R.drawable.icon_blacksend_touch,R.drawable.session_stranger};


        //定义一个动态数组
        ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String, Object>>();
        //在数组中存放数据
        for(int i=0;i<title.length;i++){
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("itemImg",imgSrc[i]);
            map.put("itemTitle",title[i]);
            map.put("itemText",text[i]);
            listItem.add(map);
        }

        //加载
        SimpleAdapter mySimpleAdapter = new SimpleAdapter(this,listItem,R.layout.adapter_demo,
                new String[] {"itemImg","itemTitle","itemText"},
                new int[] {R.id.img,R.id.title,R.id.text});
        list_item.setAdapter(mySimpleAdapter);

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Exercises3.this,transition.class);
                intent.putExtra("提示","您点击了第" + position + "个item");
                startActivity(intent);

            }
        });

//        仅显示title
//        list_item.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,title));
    }

}


