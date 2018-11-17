package com.example.administrator.gestureoperation;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationView extends View {
    private List<MyBitMap> myBitMaps;
    private MyBitMap curBitmap;//当前操作的图形


    private Context context;
    private Matrix currentMatrix = new Matrix();
    Paint mPaint = new Paint(); //边界画笔
    private Paint paintBitmap;  //bitmap 画笔
    public static final int DEFAULT_FRAME_COLOR = Color.RED; //边框颜色
    public static final int DEFAULT_FRAME_WIDTH = 2;  //边框宽度

    private enum MODE {
        NONE, DRAG, ZOOM
    }

    /**
     * 画外围框的Path
     */
    private Path mPath = new Path();

    private MODE mode = MODE.NONE;// 默认模式

    public OperationView(Context context) {
        super(context);
        this.context = context;
        myBitMaps = new ArrayList<>();

        mPaint.setAntiAlias(true);
        mPaint.setColor(DEFAULT_FRAME_COLOR);
        mPaint.setStrokeWidth(DEFAULT_FRAME_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);

        paintBitmap = new Paint();
        paintBitmap.setStyle(Paint.Style.FILL);
        paintBitmap.setAntiAlias(true);

    }


    //删除bitmap
    public void delete(List<ImageBean> metaList) {
        if (curBitmap == null) {
            return;
        }
        myBitMaps.remove(curBitmap);
        metaList.remove(curBitmap.getMetaBean());
        invalidate();
    }

    public void addBitmap(MyBitMap bitmap) {
        myBitMaps.add(bitmap);
        float[] values = new float[9];
        bitmap.matrix.getValues(values);   //记录初始位置
        float globalX = values[Matrix.MTRANS_X];
        float globalY = values[Matrix.MTRANS_Y];
        float width = values[Matrix.MSCALE_X] * bitmap.getBitmap().getWidth();
        float height = values[Matrix.MSCALE_Y] * bitmap.getBitmap().getHeight();
        //左上
        bitmap.mLTPoint.x = globalX;
        bitmap.mLTPoint.y = globalY;
        //右上
        bitmap.mRTPoint.x = globalX + width;
        bitmap.mRTPoint.y = globalY;
        // 右下
        bitmap.mRBPoint.x = globalX + width;
        bitmap.mRBPoint.y = globalY + height;
        //左下
        bitmap.mLBPoint.x = globalX;
        bitmap.mLBPoint.y = globalY + height;

        bitmap.curLTPoint.x = (int) bitmap.mLTPoint.x;
        bitmap.curLTPoint.y = (int) bitmap.mLTPoint.y;
        bitmap.curRTPoint.x = (int) bitmap.mRTPoint.x;
        bitmap.curRTPoint.y = (int) bitmap.mRTPoint.y;
        bitmap.curRBPoint.x = (int) bitmap.mRBPoint.x;
        bitmap.curRBPoint.y = (int) bitmap.mRBPoint.y;
        bitmap.curLBPoint.x = (int) bitmap.mLBPoint.x;
        bitmap.curLBPoint.y = (int) bitmap.mLBPoint.y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {  //画边界
        super.onDraw(canvas);
        for (MyBitMap bitmap : myBitMaps) {
            canvas.drawBitmap(bitmap.getBitmap(), bitmap.matrix, paintBitmap);
            if (curBitmap == null) return;
            float[] mLTPoint = new float[2];
            float[] mRTPoint = new float[2];
            float[] mRBPoint = new float[2];
            float[] mLBPoint = new float[2];
            bitmap.matrix.mapPoints(mLTPoint, new float[]{bitmap.mLTPoint.x, bitmap.mLTPoint.y});
            bitmap.matrix.mapPoints(mRTPoint, new float[]{bitmap.mRTPoint.x, bitmap.mRTPoint.y});
            bitmap.matrix.mapPoints(mRBPoint, new float[]{bitmap.mRBPoint.x, bitmap.mRBPoint.y});
            bitmap.matrix.mapPoints(mLBPoint, new float[]{bitmap.mLBPoint.x, bitmap.mLBPoint.y});
            //赋值位置
            bitmap.curLTPoint.x = (int) mLTPoint[0];
            bitmap.curLTPoint.y = (int) mLTPoint[1];
            bitmap.curRTPoint.x = (int) mRTPoint[0];
            bitmap.curRTPoint.y = (int) mRTPoint[1];
            bitmap.curRBPoint.x = (int) mRBPoint[0];
            bitmap.curRBPoint.y = (int) mRBPoint[1];
            bitmap.curLBPoint.x = (int) mLBPoint[0];
            bitmap.curLBPoint.y = (int) mLBPoint[1];
            //
            if (bitmap.equals(curBitmap) && curBitmap.isNeedFrame) {
                mPath.reset();
                mPath.moveTo(mLTPoint[0], mLTPoint[1]);
                mPath.lineTo(mRTPoint[0], mRTPoint[1]);
                mPath.lineTo(mRBPoint[0], mRBPoint[1]);
                mPath.lineTo(mLBPoint[0], mLBPoint[1]);
                mPath.lineTo(mLTPoint[0], mLTPoint[1]);
                canvas.drawPath(mPath, mPaint);
            }

        }

    }

    //选中view 下一层
    public int[] downLayer() {
        int[] swapIndex = new int[2];
        if (curBitmap == null || myBitMaps.size() < 2) return null;
        int index = -1;
        for (int i = 0; i < myBitMaps.size(); i++) { //找到当前操作bitmap 下标
            if (curBitmap.equals(myBitMaps.get(i))) {
                index = i;
            }
        }
        if (index != -1 && index != 0) {
            Collections.swap(myBitMaps, index, index - 1);
            swapIndex[0] = index;
            swapIndex[1] = index - 1;
            invalidate();
            return swapIndex;
        }
        return null;

    }

    public void reverse() {
        if (curBitmap == null) {
            return;
        }
        int w = curBitmap.getBitmap().getWidth();
        int h = curBitmap.getBitmap().getHeight();
        Canvas cv = new Canvas(curBitmap.getBitmap());
        Matrix m = new Matrix();
        m.postScale(-1, 1);   //镜像水平翻转
        Bitmap new2 = Bitmap.createBitmap(curBitmap.getBitmap(), 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w, h), null);
        curBitmap.bitmap = new2;
        invalidate();

    }

    //删除bitmap
    public int delete() {
        if (curBitmap == null) {
            return -1;
        }
        int id = curBitmap.getId();
        myBitMaps.remove(curBitmap);
        invalidate();
        return id;
    }

    //删除bitmap
    public void delete(int index) {
        myBitMaps.remove(myBitMaps.get(index));
        invalidate();

    }

    public void clear() {
        myBitMaps.clear();
        invalidate();
    }

    //选中view 上一层
    public int[] upLayer() {
        int[] swapIndex = new int[2];
        if (curBitmap == null || myBitMaps.size() < 2) return null;
        int index = -1;
        for (int i = 0; i < myBitMaps.size(); i++) { //找到当前操作bitmap 下标
            if (curBitmap.equals(myBitMaps.get(i))) {
                index = i;
            }
        }
        if (index != -1 && index + 1 != myBitMaps.size()) {
            Collections.swap(myBitMaps, index, index + 1);
            swapIndex[0] = index;
            swapIndex[1] = index + 1;
            invalidate();
            return swapIndex;
        }
        return null;
    }

    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return
     */

    public float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点之间的中间点
     *
     * @param event
     * @return
     */
    public PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    public float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:// 手指压下屏幕
                mode = MODE.DRAG;
                for (int i = 0; i < myBitMaps.size(); i++) {
                    MyBitMap customBitmap = myBitMaps.get(i);
                    ArrayList<Point> pointsList = new ArrayList<>();
                    pointsList.add(customBitmap.curLTPoint);
                    pointsList.add(customBitmap.curRTPoint);
                    pointsList.add(customBitmap.curRBPoint);
                    pointsList.add(customBitmap.curLBPoint);
                    if (isPolygonContainsPoint(pointsList, new Point((int) event.getX(), (int) event.getY()))) {
                        curBitmap = customBitmap;
                    }
                }
                if (curBitmap == null) return true;
                curBitmap.isNeedFrame = true;
                currentMatrix.set(curBitmap.matrix);// 记录ImageView当前的移动位置
                curBitmap.startPoint.set(event.getX(), event.getY());
                postInvalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:// 当屏幕上还有触点（手指），再有一个手指压下屏幕
                mode = MODE.ZOOM;
                if (curBitmap == null) return true;
                curBitmap.oldRotation = rotation(event);
                curBitmap.startDis = distance(event);
                if (curBitmap.startDis > 10f) {
                    curBitmap.midPoint = mid(event);
                    currentMatrix.set(curBitmap.matrix);// 记录ImageView当前的缩放倍数
                }
                break;

            case MotionEvent.ACTION_MOVE:// 手指在屏幕移动，该 事件会不断地触发
                if (curBitmap == null) {
                    return true;
                }
                if (mode == MODE.DRAG) {
                    float dx = event.getX() - curBitmap.startPoint.x;// 得到在x轴的移动距离
                    float dy = event.getY() - curBitmap.startPoint.y;// 得到在y轴的移动距离
                    curBitmap.matrix.set(currentMatrix);// 在没有进行移动之前的位置基础上进行移动
                    curBitmap.matrix.postTranslate(dx, dy);
                } else if (mode == MODE.ZOOM) {// 缩放与旋转
                    float endDis = distance(event);// 结束距离
                    curBitmap.rotation = rotation(event) - curBitmap.oldRotation;
                    if (endDis > 10f) {
                        float scale = endDis / curBitmap.startDis;// 得到缩放倍数
                        curBitmap.matrix.set(currentMatrix);
                        curBitmap.matrix.postScale(scale, scale, curBitmap.midPoint.x, curBitmap.midPoint.y);
                        curBitmap.matrix.postRotate(curBitmap.rotation, curBitmap.midPoint.x, curBitmap.midPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:// 手指离开屏
                break;
            case MotionEvent.ACTION_POINTER_UP:// 有手指离开屏幕,但屏幕还有触点（手指）
                mode = MODE.NONE;
                break;
        }
        invalidate();
        return true;
    }

    public static boolean isPolygonContainsPoint(List<Point> mPoints, Point point) {
        int nCross = 0;
        for (int i = 0; i < mPoints.size(); i++) {
            Point p1 = mPoints.get(i);
            Point p2 = mPoints.get((i + 1) % mPoints.size());
            // 取多边形任意一个边,做点point的水平延长线,求解与当前边的交点个数
            // p1p2是水平线段,要么没有交点,要么有无限个交点
            if (p1.y == p2.y)
                continue;
            // point 在p1p2 底部 --> 无交点
            if (point.y < Math.min(p1.y, p2.y))
                continue;
            // point 在p1p2 顶部 --> 无交点
            if (point.y >= Math.max(p1.y, p2.y))
                continue;
            // 求解 point点水平线与当前p1p2边的交点的 X 坐标
            double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
            if (x > point.x) // 当x=point.x时,说明point在p1p2线段上
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }


    public void resetFocus() {  //取消边框绘制
        if (curBitmap != null)
            curBitmap.isNeedFrame = false;
        invalidate();
    }


}
