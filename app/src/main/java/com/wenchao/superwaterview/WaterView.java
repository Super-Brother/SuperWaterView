package com.wenchao.superwaterview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author wenchao
 * @date 2019/7/31.
 * @time 19:28
 * description：
 */
public class WaterView extends FrameLayout {

    /**
     * 定义一个问本控件
     */
    private TextView textView;
    /**
     * 文本框的初始坐标
     */
    private PointF initPosition;
    /**
     * 手指是否触摸到了控件
     */
    private boolean isClicked = false;
    /**
     * 手指移动到的坐标 或者 也可以是终点坐标
     */
    private PointF movePosition;
    /**
     * 绘制的圆的半径
     */
    private float mRadius = 40;
    /**
     * 绘制的画笔
     */
    private Paint paint;
    private Path path;
    /**
     * 判断文本框是否离开某个范围
     */
    private boolean isOut = false;
    //爆炸的效果图片控件
    private ImageView imageView;

    public WaterView(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化整个效果的控件
     */
    private void init() {
        initPosition = new PointF(500, 500);
        movePosition = new PointF();

        //初始化画笔
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        path = new Path();

        textView = new TextView(getContext());
        textView.setPadding(20, 20, 20, 20);
        textView.setText("99+");
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.shape_text_view_bg);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        addView(textView);

        imageView = new ImageView(getContext());
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.anim);
        imageView.setVisibility(View.GONE);
        addView(imageView);
    }

    /**
     * 绘制包括本身的
     *
     * @param canvas
     */
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//    }

    /**
     * 绘制当前控件里面的内容里面的控件
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        //保存canvas的状态
        canvas.save();
        if (isClicked) {
            textView.setX(movePosition.x - textView.getWidth() / 2);
            textView.setY(movePosition.y - textView.getHeight() / 2);
            drawPath();
            if (!isOut) {
                //画第一个圆
                canvas.drawCircle(initPosition.x, initPosition.y, mRadius, paint);
                //画第二个圆
                canvas.drawCircle(movePosition.x, movePosition.y, mRadius, paint);
                //画连接桥
                canvas.drawPath(path, paint);
            }
        } else {
            textView.setX(initPosition.x - textView.getWidth() / 2);
            textView.setY(initPosition.y - textView.getHeight() / 2);
        }
        //恢复canvas的状态
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 找到绘制连接桥的四个点并且用贝塞尔曲线
     */
    private void drawPath() {
        final float edgeX = movePosition.x - initPosition.x;
        final float edgeY = movePosition.y - initPosition.y;
        //计算两个点之间的距离
        final double distance = Math.sqrt(Math.pow(edgeX, 2) + Math.pow(edgeY, 2));
        mRadius = (float) (40 - distance / 30);
        if (distance >= 400) {
            isOut = true;
        } else {
            isOut = false;
        }
        //得到三角形的锐角的角度值，正切值
        final double atan = Math.atan(edgeY / edgeX);
        final float offsetX = (float) (mRadius * Math.sin(atan));
        final float offsetY = (float) (mRadius * Math.cos(atan));
        //A坐标
        final float AX = initPosition.x + offsetX;
        final float AY = initPosition.y - offsetY;
        //B坐标
        final float BX = movePosition.x + offsetX;
        final float BY = movePosition.y - offsetY;
        //C坐标
        final float CX = movePosition.x - offsetX;
        final float CY = movePosition.y + offsetY;
        //D坐标
        final float DX = initPosition.x - offsetX;
        final float DY = initPosition.y + offsetY;
        //获取起点坐标和终点坐标的中心点
        final float middleX = (initPosition.x + movePosition.x) / 2;
        final float middleY = (initPosition.y + movePosition.y) / 2;

        path.reset();
        path.moveTo(AX, AY);
        path.quadTo(middleX, middleY, BX, BY);
        path.lineTo(CX, CY);
        path.quadTo(middleX, middleY, DX, DY);
        path.lineTo(AX, AY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                movePosition.set(initPosition.x, initPosition.y);
                //判断当前点击的位置是否是在问本控件里面
                //这个对象是用来封装文本控件的范围的对象
                Rect rect = new Rect();
                int[] location = new int[2];
                //获取到textView控件再窗体中的下X，Y坐标
                textView.getLocationOnScreen(location);
                //初始化Rect对象
                rect.left = location[0];
                rect.top = location[1];
                rect.right = location[0] + textView.getWidth();
                rect.bottom = location[1] + textView.getHeight();
                //判断当前点击的坐标是否在范围之内
                if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    isClicked = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                isClicked = false;
                if (isOut) {
                    textView.setVisibility(View.GONE);
                    imageView.setX(movePosition.x - imageView.getWidth() / 2);
                    imageView.setY(movePosition.y - imageView.getHeight() / 2);
                    imageView.setVisibility(View.VISIBLE);
                    ((AnimationDrawable) (imageView.getDrawable())).start();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                /**
                 * getRawX() 相对于父控件的坐标
                 * getX() 相对于整个屏幕的坐标
                 */
                movePosition.set(event.getX(), event.getY());
                break;

            default:
                break;
        }
        //通过这个Api可以调用到dispatchDraw方法
        postInvalidate();
        return true;
    }
}
