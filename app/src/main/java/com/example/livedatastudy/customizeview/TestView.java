//package com.example.livedatastudy.customizeview;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//
//import com.example.livedatastudy.R;
//
//public class TestView extends View {
//    /**
//     * 在java代码里new的时候会用到
//     * @param context
//     */
//    public TestView(Context context) {
//        super(context);
//    }
//    /**
//     * 在xml布局文件中使用时自动调用
//     * @param context
//     *
//     * 在View的构造方法中通过TypedArray获取
//     */
//    public TestView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.test);
//        String text = typedArray.getString(R.styleable.test_testAttr);
//        int textAttr = typedArray.getInteger(R.styleable.test_text, -1);
//        Log.e("自定义TestView", "text = " + text + " , textAttr = " + textAttr);
//        typedArray.recycle();
//    }
//    /**
//     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
//     * @param context
//     * @param attrs
//     * @param defStyleAttr
//     */
//    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//    /**
//     * 只有在API版本>21时才会用到
//     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
//     * @param context
//     * @param attrs
//     * @param defStyleAttr
//     * @param defStyleRes
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//}
