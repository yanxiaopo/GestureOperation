package com.example.administrator.gestureoperation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;


public class MyBitMap {
    //Matrix.MSKEW_X 控制X坐标的线性倾斜系数
    //Matrix.MSKEW_Y 控制Y坐标的线性倾斜系数
    //Matrix.MTRANS_X//左上顶点X坐标
    //Matrix.MTRANS_Y//左上顶点Y坐标
    //Matrix.MSCALE_X//宽度缩放倍数
    //Matrix.MSCALE_Y//高度缩放位数
    private int id;//唯一标识，实际项目中可替换为url
    public float startDis;// 开始距离
    public PointF midPoint;// 中间点
    public float oldRotation = 0;
    public float rotation = 0;
    public PointF startPoint = new PointF();
    public Matrix matrix = new Matrix();
    public Bitmap bitmap;
    /**
     * 图片四个点坐标
     */
    public PointF mLTPoint = new PointF();
    public PointF mRTPoint = new PointF();
    public PointF mRBPoint = new PointF();
    public PointF mLBPoint = new PointF();

    public Point curLTPoint = new Point();
    public Point curRTPoint = new Point();
    public Point curRBPoint = new Point();
    public Point curLBPoint = new Point();

    public MyBitMap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public boolean isNeedFrame = true; //是否绘制边框

    public ImageBean getMetaBean() {
        return metaBean;
    }

    public void setMetaBean(ImageBean metaBean) {
        this.metaBean = metaBean;
    }

    private ImageBean metaBean;
}
