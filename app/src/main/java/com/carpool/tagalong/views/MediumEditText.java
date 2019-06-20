package com.carpool.tagalong.views;

import android.content.Context;
import android.util.AttributeSet;



public class MediumEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String asset = "fonts/roboto/roboto_medium.ttf";

    public MediumEditText(Context context) {
        super(context);
    }

    public MediumEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, asset);
    }

    public MediumEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, asset);
    }

    public boolean setCustomFont(Context ctx, String asset) {

        setTypeface(TypeFace.get(ctx, asset));
        return true;
    }
}
