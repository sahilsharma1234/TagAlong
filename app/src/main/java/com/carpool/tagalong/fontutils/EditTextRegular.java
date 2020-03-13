package com.carpool.tagalong.fontutils;

/**
 * Create By Rahul Mangal
 * Project SignupLibrary Screen
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class EditTextRegular extends android.support.v7.widget.AppCompatEditText {

    private final String fontFamily = "fonts/grotesk_regular.otf";

    public EditTextRegular(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    public EditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    public EditTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontFamily);
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}