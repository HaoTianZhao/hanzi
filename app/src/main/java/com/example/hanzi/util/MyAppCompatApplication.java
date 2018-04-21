package com.example.hanzi.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.hanzi.R;
import com.example.hanzi.entity.Character;
import com.example.hanzi.service.MusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵 on 2018/4/4.
 * .
 */

public class MyAppCompatApplication extends AppCompatActivity {
    /**
     * 汉字个数，当前汉字id，汉字名，汉字笔划id，笔划图片id，汉字列表，笔划数量，按钮id
     */
    protected static int number;
    protected static int currentNumber;
    protected static String[] names;
    protected static String[] hints;
    protected static int[][] orders;
    protected static int[] imageIds;
    protected static List<Character> characterList;

    protected static int buttonNumbers;
    protected static int[] buttonIds;

    /**
     * 控制程序流程的状态标志
     */
    protected static long time;
    protected static boolean isFirstStart = true;

    /**
     * 绑定服务的代码
     */
    public static MusicService.MusicBinder musicBinder;
    public static ServiceConnection connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicBinder != null)
            musicBinder.startMusic();
    }

    //当用户按Home键等操作使程序进入后台时调用
    @Override
    protected void onUserLeaveHint() {
        if (musicBinder != null)
            musicBinder.pauseMusic();
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        stopService(intent);
        unbindService(connection);
        super.onDestroy();
    }


    private void init() {
        if (!isFirstStart) {
            return;
        }
        isFirstStart = false;

        //Service的绑定是异步的
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicBinder = (MusicService.MusicBinder) service;
                musicBinder.startMusic();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBinder = null;
            }
        };
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        //startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //初始化资源
        Resources resources = getApplication().getResources();
        number = Integer.parseInt(resources.getString(R.string.number));
        currentNumber = -1;
        names = resources.getStringArray(R.array.name);
        hints = resources.getStringArray(R.array.hint);

        orders = new int[number][];
        imageIds = new int[number];
        characterList = new ArrayList<>(number);
        String[] orderArray = resources.getStringArray(R.array.order);
        for (int i = 0; i < number; i++) {
            String[] string = orderArray[i].split(",");
            orders[i] = new int[string.length];
            for (int j = 0; j < string.length; j++)
                orders[i][j] = Integer.parseInt(string[j]);
            int resID = getResources().getIdentifier(getName(names, i), "drawable", "com.example.hanzi");
            imageIds[i] = resID;
            characterList.add(new Character(names[i], orders[i], imageIds[i]));
        }

        buttonNumbers = Integer.parseInt(resources.getString(R.string.buttonNumber));
        buttonIds = new int[buttonNumbers];
        for (int i = 0; i < buttonNumbers; i++)
            buttonIds[i] = R.id.bi_hua_01;
    }

    private String getName(String[] names, int i) {
        if (i < 10)
            return names[i] + "_0" + i;
        return names[i] + "_" + i;
    }
}
