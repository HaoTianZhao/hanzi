package com.example.hanzi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanzi.entity.Character;
import com.example.hanzi.service.MusicService;
import com.example.hanzi.util.MyAppCompatApplication;
import com.example.hanzi.util.ReplayListener;

public class MainActivity extends MyAppCompatApplication implements View.OnClickListener {

    private Context context;
    private TextView hint;
    private boolean isOver;//通关flag

    private volatile ReplayListener replay;//replay类似一个监听器

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
            //点击笔划
            default:
                boolean done = characterList.get(currentNumber).write(v.getId() - R.id.bi_hua_01);
                if (done)
                    nextCharacter();
                break;
        }
    }

    private void nextCharacter() {
        if (currentNumber + 1 < number) {
            currentNumber++;
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

}
