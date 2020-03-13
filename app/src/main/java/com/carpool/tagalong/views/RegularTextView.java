package com.carpool.tagalong.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;


public class RegularTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String asset = "fonts/roboto_regular.ttf";

    public RegularTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), asset);
        setTypeface(face);

    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    public void setCustomFont(Context ctx, AttributeSet attrs) {

//        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.RegularTextView);
//        String customFont = a.getString(R.styleable.RegularTextView_customFont);
        setCustomFont(ctx, asset);
//        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("ABFONT", "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
