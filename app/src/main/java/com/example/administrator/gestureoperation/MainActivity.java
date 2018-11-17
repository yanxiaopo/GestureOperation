package com.example.administrator.gestureoperation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ${yanxuesong} on 2018/4/3.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static ArrayList<ImageBean> metaBeanList = new ArrayList<>(); //添加的商品集合
    private View delete;
    private View up_layer;
    private View reverse;
    private View down_layer;
    private RelativeLayout contain;
    private View frame_hide;
    private OperationView drawingView;
    private View add_pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
    }


    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.main_activity);
        contain = findViewById(R.id.contain);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
        reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(this);
        up_layer = findViewById(R.id.up_layer);
        up_layer.setOnClickListener(this);
        down_layer = findViewById(R.id.down_layer);
        frame_hide = findViewById(R.id.frame_hide);
        add_pic = findViewById(R.id.add_pic);
        down_layer.setOnClickListener(this);
        frame_hide.setOnClickListener(this);
        add_pic.setOnClickListener(this);

        drawingView = new OperationView(this);

        ImageBean imageBean = new ImageBean(R.drawable.virgo);
        metaBeanList.add(imageBean);
        loadBitmap(this, drawingView, imageBean);


        contain.addView(drawingView);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete: // TODO: 2018/4/20
                drawingView.delete(metaBeanList);
                break;
            case R.id.reverse:
                drawingView.reverse();
                break;
            case R.id.up_layer:
                int[] ints = drawingView.upLayer();
                break;
            case R.id.down_layer:
                int[] intss = drawingView.downLayer();
                break;
            case R.id.frame_hide:
                drawingView.resetFocus();
                break;
            case R.id.add_pic:
                ImageBean imageBean = new ImageBean(R.drawable.headdress);
                metaBeanList.add(imageBean);
                loadBitmap(this, drawingView, imageBean);

                break;
            default:
                break;
        }
    }


    private void loadBitmap(final Context mContext, final OperationView drawingView, final ImageBean bean) {
        Bitmap copy = BitmapFactory.decodeResource(mContext.getResources(), bean.getImage()).copy(Bitmap.Config.ARGB_8888, true);
        drawingView.addBitmap(new MyBitMap(copy));

    }


}
