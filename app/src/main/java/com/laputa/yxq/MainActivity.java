package com.laputa.yxq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.MenuAdapter;
import bean.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ListView lvMenu;
    private List<MenuItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initMenus();
    }

    private void initMenus() {
        list = new ArrayList<MenuItem>();
        MenuItem item0 = new MenuItem(0, "Test", "just测试", TestActivity.class);
        list.add(item0);
//		for (int i = 0; i < 3; i++) {
//			MenuItem item = new MenuItem();
//			list.add(item);
//		}
        MenuItem item1 = new MenuItem(1, "LiteBle", "LiteBle简单的操作", LiteBleActivity.class);
        list.add(item1);

        MenuItem item2 = new MenuItem(1, "刮刮卡", "模拟刮刮卡",R.drawable.ic_heygirl, ScratchActivity.class);
        list.add(item2);

        MenuItem item3 = new MenuItem(1, "OkHttp", "OkHttp 3.0 --" +
                "\n"+"(1) 高效的使用HTTP,使应用运行更快更省流量" +
                "\n"+"(2)响应缓存数据避免重复网络请求;" +
                "\n"+"(3)无缝的支持GZIP来减少数据流量" +
                "\n"+"(4)使用非常简单，请求和响应的Api具有流畅的建造和不变性，同时支持同步异步调用回调函数;" +
                "\n"+"(5)如果网络出现问题，它会从常见的连接问题中恢复;" +
                "\n"+"(6)如果服务器配置有多个IP地址，当第一个IP连接失败，它会尝试连接下一个IP",R.drawable.ic_ok_http, OkHttpActivity.class);
        list.add(item3);

        MenuAdapter adapter = new MenuAdapter(list);
        lvMenu.setAdapter(adapter);
    }

    private void initViews() {
        lvMenu = (ListView) findViewById(R.id.lv_menu);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MenuItem menuItem = list.get(position);
                Intent intent = new Intent(MainActivity.this,menuItem.getClz());
                startActivity(intent);
            }
        });

    }
}
