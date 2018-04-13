package com.example.hanzi;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanzi.entity.Character;
import com.example.hanzi.util.MyAppCompatApplication;

public class MainActivity extends MyAppCompatApplication implements View.OnClickListener {

    private Context context;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        hint = (TextView) findViewById(R.id.hint);
        Character.init(context, (FrameLayout) findViewById(R.id.han_zi));

        //注册响应事件
        for (int i = 0; i < super.buttonNumbers; i++)
            findViewById(R.id.bi_hua_01 + i).setOnClickListener(this);
        findViewById(R.id.again).setOnClickListener(this);

        nextCharacter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.again:
                characterList.get(currentNumber).replay();
                //Toast.makeText(context, "重写汉字", Toast.LENGTH_SHORT).show();
                break;
            default:
                boolean isOver = characterList.get(currentNumber).write(v.getId() - R.id.bi_hua_01);
                if (isOver)
                    nextCharacter();
                break;
        }
    }

    private void nextCharacter() {
        currentNumber++;
        if (currentNumber < number) {
            Character.clear();
            hint.setText(hints[currentNumber]);
            Toast.makeText(context, "下一个字", Toast.LENGTH_SHORT).show();
        } else {
            //通关
            Toast.makeText(context, "通关", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (SystemClock.elapsedRealtime() - times < 2000) {
            System.exit(0);
        } else {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            times = SystemClock.elapsedRealtime();
        }
    }

}
