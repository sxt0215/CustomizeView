package com.example.livedatastudy.customizeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.livedatastudy.R;

import java.util.Calendar;

public class WatchBoard extends View {
    private float mRadius; //外圆半径
    private float mPadding; //边距
    private float mTextSize; //文字大小
    private float mHourPointWidth; //时针宽度
    private float mMinutePointWidth; //分针宽度
    private float mSecondPointWidth; //秒针宽度
    private int mPointRadius; // 指针圆角
    private float mPointEndLength; //指针末尾的长度

    private int mColorLong; //长线的颜色
    private int mColorShort; //短线的颜色
    private int mHourPointColor; //时针的颜色
    private int mMinutePointColor; //分针的颜色
    private int mSecondPointColor; //秒针的颜色

    private Paint mPaint; //画笔
    private Context viewContext;

    public WatchBoard(Context context) {
        super(context,null);
    }

    public WatchBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewContext = context;
        obtainStyledAttrs(attrs); //获取自定义的属性
        init(); //初始化画笔
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.WatchBoard);
            mPadding = array.getDimension(R.styleable.WatchBoard_wb_padding, DptoPx(10));
            mTextSize = array.getDimension(R.styleable.WatchBoard_wb_text_size, SptoPx(16));
            mHourPointWidth = array.getDimension(R.styleable.WatchBoard_wb_hour_pointer_width, DptoPx(5));
            mMinutePointWidth = array.getDimension(R.styleable.WatchBoard_wb_minute_pointer_width, DptoPx(3));
            mSecondPointWidth = array.getDimension(R.styleable.WatchBoard_wb_second_pointer_width, DptoPx(2));
            mPointRadius = (int) array.getDimension(R.styleable.WatchBoard_wb_pointer_corner_radius, DptoPx(10));
            mPointEndLength = array.getDimension(R.styleable.WatchBoard_wb_pointer_end_length, DptoPx(10));

            mColorLong = array.getColor(R.styleable.WatchBoard_wb_scale_long_color, Color.argb(225, 0, 0, 0));
            mColorShort = array.getColor(R.styleable.WatchBoard_wb_scale_short_color, Color.argb(125, 0, 0, 0));
            mHourPointColor = array.getColor(R.styleable.WatchBoard_wb_minute_pointer_color, Color.BLACK);
            mMinutePointColor = array.getColor(R.styleable.WatchBoard_wb_minute_pointer_color, Color.BLACK);
            mSecondPointColor = array.getColor(R.styleable.WatchBoard_wb_second_pointer_color, Color.RED);
        } catch (Exception e) {
            //一旦出现错误全部使用默认值
            mPadding = DptoPx(10);
            mTextSize = SptoPx(16);
            mHourPointWidth = DptoPx(5);
            mMinutePointWidth = DptoPx(3);
            mSecondPointWidth = DptoPx(2);
            mPointRadius = (int) DptoPx(10);
            mPointEndLength = DptoPx(10);

            mColorLong = Color.argb(225, 0, 0, 0);
            mColorShort = Color.argb(125, 0, 0, 0);
            mMinutePointColor = Color.BLACK;
            mSecondPointColor = Color.RED;
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }

    //Dp转px
    private float DptoPx(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, viewContext.getResources().getDisplayMetrics());
    }

    //sp转px
    private float SptoPx(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, viewContext.getResources().getDisplayMetrics());
    }

    public static int Dp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //画笔初始化
    private void init() {
        mPaint = new Paint();
        //防锯齿  会损失一定的性能
        mPaint.setAntiAlias(true);
        //设置是否使用图像抖动处理  会使绘制的图片等颜色更加的清晰以及饱满。（也是损失性能）
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 1000; //设定一个最小值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST ||
                widthMode == MeasureSpec.UNSPECIFIED ||
                heightMeasureSpec == MeasureSpec.AT_MOST ||
                heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            try {
                throw new NoDetermineSizeException("宽度高度至少有一个确定的值,不能同时为wrap_content");
            } catch (NoDetermineSizeException e) {
                e.printStackTrace();
            }
        } else { //至少有一个为确定值,要获取其中的最小值
            if (widthMode == MeasureSpec.EXACTLY) {
                width = Math.min(widthSize, width);
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                width = Math.min(heightSize, width);
            }
        }
        setMeasuredDimension(width, width);
    }

    /**
     * 5、获取表盘圆的半径值与尾部长度值
     * 获取值应该在测量完成之后,所以在onSizeChange里面获取
     * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w, h) - mPadding) / 2;
        mPointEndLength = mRadius / 6; //尾部指针默认为半径的六分之一
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        /**
         * 画布向（100，50）方向平移
         *
         * 参数1: 向X轴方向移动100距离
         * 参数2: 向Y轴方向移动50距离
         */
        canvas.translate(getWidth() / 2, getHeight() / 2);
        //绘制外圆背景
        paintCircle(canvas);
        //绘制刻度
        paintScale(canvas);
        //绘制指针
        paintPointer(canvas);
        canvas.restore();
        //刷新  每隔一秒钟刷新一次
        postInvalidateDelayed(1000);
    }

    //绘制外圆背景
    public void paintCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, mRadius, mPaint);
    }

    //绘制刻度
    private void paintScale(Canvas canvas) {
        //设置画笔宽度
        mPaint.setStrokeWidth(Dp2Px(getContext(), 1));
        int lineWidth = 0;
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) { //整点
                mPaint.setStrokeWidth(Dp2Px(getContext(), 1.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
                mPaint.setTextSize(mTextSize);
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                Rect textBound = new Rect();
                mPaint.getTextBounds(text, 0, text.length(), textBound);
                mPaint.setColor(Color.BLACK);
                canvas.save();
                //画布向（x，y）方向平移
                canvas.translate(0, -mRadius + DptoPx(5) + lineWidth + (textBound.bottom - textBound.top));
                //原点为中心，旋转xx度（顺时针方向为正方向 ）  参数: 旋转角度
                canvas.rotate(-6 * i);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawText(text, -(textBound.right - textBound.left) / 2,textBound.bottom, mPaint);
                canvas.restore();
            } else { //非整点
                lineWidth = 30;
                mPaint.setColor(mColorShort);
                mPaint.setStrokeWidth(Dp2Px(getContext(), 1));
            }
            canvas.drawLine(0, -mRadius + Dp2Px(getContext(), 10), 0, -mRadius + Dp2Px(getContext(), 10) + lineWidth, mPaint);
            canvas.rotate(6);
        }
    }

    //绘制指针
    private void paintPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); //时
        int minute = calendar.get(Calendar.MINUTE); //分
        int second = calendar.get(Calendar.SECOND); //秒
        int angleHour = (hour % 12) * 360 / 12; //时针转过的角度
        int angleMinute = minute * 360 / 60; //分针转过的角度
        int angleSecond = second * 360 / 60; //秒针转过的角度
        //绘制时针
        canvas.save();
        canvas.rotate(angleHour); //旋转到时针的角度
        RectF rectFHour = new RectF(-mHourPointWidth / 2, -mRadius * 3 / 5, mHourPointWidth / 2, mPointEndLength);
        mPaint.setColor(mHourPointColor); //设置指针颜色
        //设置画笔透明度  仅描边
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mHourPointWidth); //设置边界宽度
        canvas.drawRoundRect(rectFHour, mPointRadius, mPointRadius, mPaint); //绘制时针
        canvas.restore();// 返回最新的save状态
        //绘制分针
        canvas.save();
        canvas.rotate(angleMinute);
        RectF rectFMinute = new RectF(-mMinutePointWidth / 2, -mRadius * 3.5f / 5, mMinutePointWidth / 2, mPointEndLength);
        mPaint.setColor(mMinutePointColor);
        mPaint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectFMinute, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制秒针
        canvas.save();
        canvas.rotate(angleSecond);
        RectF rectFSecond = new RectF(-mSecondPointWidth / 2, -mRadius + 15, mSecondPointWidth / 2, mPointEndLength);
        mPaint.setColor(mSecondPointColor);
        mPaint.setStrokeWidth(mSecondPointWidth);
        canvas.drawRoundRect(rectFSecond, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制中心小圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSecondPointColor);
        canvas.drawCircle(0, 0, mSecondPointWidth * 4, mPaint);
    }



    //自定义的异常
    class NoDetermineSizeException extends Exception {
        public NoDetermineSizeException(String message) {
            super(message);
        }
    }


}
