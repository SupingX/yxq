package com.laputa.yxq;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import util.TaskExecutor;

public class OkHttpActivity extends AppCompatActivity {

    private OkHttpClient okHttp;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);

        okHttp = new OkHttpClient();
        initView();
    }

    private void initView() {

        tvInfo = (TextView) findViewById(R.id.tv_ok_http_info);
    }

    private void updateInfo(String info){
        tvInfo.setText(info==null?"":info);
    }

    public void testGetSync(View view)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个request
                Request request  = new Request.Builder()
                        .url("http://www.baidu.com")
                        .get()
                        .build();

                // 发起同步请求.并加入回调

                try {
                    final Response response = okHttp.newCall(request).execute();
                    if (!response.isSuccessful()){
                        return;
                    }
                    System.out.println(getResponseStr(response));
                    TaskExecutor.getUiHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateInfo(getResponseStr(response));
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getResponseStr( Response response){
        StringBuffer sb = new StringBuffer();
        Headers responseHeaders = response.headers();
        sb.append("\n"+"headers -->");
        for (int i = 0; i <responseHeaders.size() ; i++) {
            sb.append("\n"+"--> "+responseHeaders.name(i) + " : " + responseHeaders.value(i));
        }
        sb.append("\n");
        sb.append("\n"+"body --> "+response.body().toString());
        return sb.toString();
    }

    public void testGetAsync(View view)  {
        // 创建一个request
        Request request  = new Request.Builder()
                .url("http://www.baidu.com")
                .get()
                .build();

        // 发起异步请求.并加入回调
        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                TaskExecutor.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        updateInfo("请求失败；"+e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                TaskExecutor.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        updateInfo(getResponseStr(response));
                    }
                });

            }
        });
    }


    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    public void testPostSyncForString(View view)  {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
        String postBody = ""
                +"Release" +"\n"
                +"------" +"\n"
                +"\n"
                +"*_1.0_May 6,2013"+"\n"
                +"i miss u ! yxq !"+"\n"
                ;
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,postBody))
                .build()
                ;
                final Response response;

                    response = okHttp.newCall(request).execute();

                System.out.println(getResponseStr(response));
        TaskExecutor.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                updateInfo(getResponseStr(response));
            }
        });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void testPostSyncForSteam(View view){
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers : " + "\n");
                sink.writeUtf8(  "\n");
                for (int i = 1; i <99 ; i++) {
                        sink.writeUtf8(String.format("* %s=%s\n",i,factor(i)));
                }
            }
            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };
        ///////

    }
}
