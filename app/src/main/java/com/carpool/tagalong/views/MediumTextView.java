package com.carpool.tagalong.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class MediumTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String asset = "fonts/roboto/roboto_medium.ttf";

    public MediumTextView(Context context) {
        super(context);
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, asset);
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, asset);
    }

    public boolean setCustomFont(Context ctx, String asset) {

        setTypeface(TypeFace.get(ctx, asset));
        return true;
    }
}
