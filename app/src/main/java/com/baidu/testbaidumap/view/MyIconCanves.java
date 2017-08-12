package com.baidu.testbaidumap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.baidu.testbaidumap.R;

/**
 * @anthor ydl
 * @time 2017/8/10 11:46
 * TestBaiduMap 描述:
 */

public class MyIconCanves extends View {

    private static final String TAG = "Gao";
    private Bitmap mBitmap;
    public MyIconCanves(Context context) {
        super(context);
        //  btn_map_current是图片资源文件，自己找个图标文件就是
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_mark);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, this.getWidth() / 2 - mBitmap.getWidth() / 2, this.getHeight()  / 2 - mBitmap.getHeight() / 2, null);
    }
}
