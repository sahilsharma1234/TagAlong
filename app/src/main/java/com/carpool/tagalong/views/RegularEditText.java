package com.carpool.tagalong.views;

import android.content.Context;
import android.util.AttributeSet;



public class RegularEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String asset = "fonts/roboto/roboto_regular.ttf";

    public RegularEditText(Context context) {
        super(context);
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, asset);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, asset);
    }

    public boolean setCustomFont(Context ctx, String asset) {

        setTypeface(TypeFace.get(ctx, asset));
        return true;
    }
}
