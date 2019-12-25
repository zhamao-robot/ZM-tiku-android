package xin.zhamao.zm_tiku.value;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import xin.zhamao.zm_tiku.utils.ZMUtil;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private final Context context;
    private int bgColor;
    private int textColor;
    public RoundBackgroundColorSpan(int bgColor, int textColor, Context context) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.context = context;
    }
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //设置宽度为文字宽度加16dp
        return ((int)paint.measureText(text, start, end)+ ZMUtil.px2dp(context, 16));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int originalColor = paint.getColor();
        paint.setColor(this.bgColor);
        //画圆角矩形背景
        canvas.drawRoundRect(new RectF(x,
                        top+ ZMUtil.px2dp(context, 3),
                        x + ((int) paint.measureText(text, start, end)+ ZMUtil.px2dp(context, 16)),
                        bottom-ZMUtil.px2dp(context, 1)),

                ZMUtil.px2dp(context, 20),
                ZMUtil.px2dp(context, 20),
                paint);
        paint.setColor(this.textColor);
        //画文字,两边各增加8dp
        canvas.drawText(text, start, end, x+ZMUtil.px2dp(context, 8), y, paint);
        //将paint复原
        paint.setColor(originalColor);
    }
}