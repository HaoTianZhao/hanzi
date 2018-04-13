package com.example.hanzi.entity;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.hanzi.R;

/**
 * Created by 赵 on 2018/4/5.
 * <p>每个汉字存储它自己的名字，笔划长度，笔划顺序编号和对应图片</p>
 */

public class Character {
    private static Context context;
    private static FrameLayout frame;

    public static void init(Context con, FrameLayout frameLayout) {
        context = con;
        frame = frameLayout;
    }

    private String name;
    private int length;
    private int index;
    private int[] order;
    private int[] imageIds;

    public Character(String name, int[] order, int firstImage) {
        this.name = name;
        this.length = order.length;
        this.index = 0;
        this.order = order;
        this.imageIds = new int[length];
        for (int i = 0; i < length; i++) {
            imageIds[i] = firstImage + i;
        }
    }

    public boolean write(int id) {
        if (id == order[index]) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResource(imageIds[index]);
            frame.addView(imageView);
            //Toast.makeText(context, String.format("%s的第%d划是%d", name, index, id), Toast.LENGTH_SHORT).show();
            index++;
            if (index == length)
                return true;
        }
        return false;
    }

    public void replay() {
        clear();
        for (int i = 0; i < length; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResource(imageIds[i]);
            frame.addView(imageView);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clear() {
        frame.removeAllViews();
        ImageView image = new ImageView(context);
        image.setImageResource(R.drawable.tian_zi_ge);
        frame.addView(image);
    }
}
