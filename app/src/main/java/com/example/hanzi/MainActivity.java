package com.example.hanzi;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanzi.entity.Character;
import com.example.hanzi.entity.Rate;
import com.example.hanzi.util.MyAppCompatApplication;
import com.example.hanzi.util.ReplayListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends MyAppCompatApplication implements View.OnClickListener {

    private Context context;
    private TextView hint;
    private boolean isOver;//通关flag
    private int errorTimes;//笔划点击错误总次数

    private volatile ReplayListener replay;//replay类似一个监听器

    private ExecutorService pool;//Callable实现类要用线程池

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示，会使状态栏消失
        window.setFlags(flag, flag);

        context = getApplicationContext();
        hint = (TextView) findViewById(R.id.hint);
        Character.init(context, (FrameLayout) findViewById(R.id.han_zi));

        isOver = false;
        replay = new ReplayListener();

        //注册响应事件
        for (int i = 0; i < buttonNumbers; i++)
            findViewById(R.id.bi_hua_01 + i).setOnClickListener(this);
        findViewById(R.id.again).setOnClickListener(this);

        nextCharacter();//显示下一个汉字

        pool = Executors.newCachedThreadPool();
    }

    @Override
    public void onClick(View v) {
        if (isOver)
            return;
        if (replay.isReplay())
            return;
        switch (v.getId()) {
            //显示答案
            case R.id.again:
                replay.setReplay(true);
                characterList.get(currentNumber).replay(replay);
                break;

            //背景音乐开关
            //case R.id.
            //musicBinder.changeStatus();
            //break;

            //点击笔划
            default:
                try {
                    Rate rate = pool.submit(new GetJSONThread()).get(1000, TimeUnit.MILLISECONDS);
                    //Toast.makeText(context, rate.getSection() + "关，错误" + rate.getErrorTime() + "次",
                    //        Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "当前网络状况不好", Toast.LENGTH_SHORT).show();
                }
                boolean isFinish = characterList.get(currentNumber).write(v.getId() - R.id.bi_hua_01);
                if (isFinish)
                    nextCharacter();
                break;
        }
    }

    private void nextCharacter() {
        //数据库记录
        if (currentNumber >= 0) {
            errorTimes += characterList.get(currentNumber).error_time;
            Runnable sendToServer = new SendJsonThread(currentNumber + 1, errorTimes);
            new Thread(sendToServer).start();
        } else {
            new Thread(new SendJsonThread(0, 0)).start();
        }

        currentNumber++;
        if (currentNumber < number) {
            Character.clear();
            hint.setText(hints[currentNumber]);
            Toast.makeText(context, "下一个字", Toast.LENGTH_SHORT).show();
        } else {
            //通关
            Toast.makeText(context, "通关", Toast.LENGTH_SHORT).show();
            isOver = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (SystemClock.elapsedRealtime() - time < 2000) {
            System.exit(0);
        } else {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            time = SystemClock.elapsedRealtime();
        }
    }


    private class SendJsonThread implements Runnable {
        private Rate rate;

        public SendJsonThread(int currentNumber, int errorTime) {
            this.rate = new Rate(ANDROID_ID, currentNumber, errorTime);
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://111.230.47.56:8088/hanzi/upload");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset", "UTF-8");
                connection.setDoOutput(true);

                //向服务器传送数据
                OutputStream out = connection.getOutputStream();
                byte[] data = rate.toString().getBytes();
                out.write(data);
                out.close();

                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();//这句必须有，不然服务器收不到数据，没发现为什么
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder s = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        s.append(line);
                    }
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetJSONThread implements Callable<Rate> {
        @Nullable
        @Override
        public Rate call() throws Exception {
            Rate rate = null;
            try {
                URL url = new URL("http://111.230.47.56:8088/hanzi/download/" + ANDROID_ID);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("charset", "UTF-8");

                connection.setDoInput(true);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();//这句必须有，不然服务器收不到数据，没发现为什么
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder s = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        s.append(line);
                    }
                    reader.close();

                    Gson gson = new Gson();
                    rate = gson.fromJson(s.toString(), Rate.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rate;
        }

    }

}
