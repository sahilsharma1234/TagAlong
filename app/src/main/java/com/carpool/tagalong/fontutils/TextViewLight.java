package com.carpool.tagalong.fontutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Create By Rahul Mangal
 * Project SignupLibrary Screen
 */

public class TextViewLight extends android.support.v7.widget.AppCompatTextView{

    private final String fontFamily = "grotesk_light.otf";

    public TextViewLight(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    public TextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}