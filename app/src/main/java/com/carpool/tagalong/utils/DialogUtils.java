package com.carpool.tagalong.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.carpool.tagalong.R;

public class DialogUtils {

    int xDelta;

//    private void showRideCompleteDialog(Activity activity) {
//
//        final Dialog delayDialog = new Dialog(activity);
//        delayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        delayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        delayDialog.setContentView(R.layout.accept_ride_dialog_layout);
//        delayDialog.setCancelable(false);
//        delayDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        com.carpool.tagalong.views.RegularTextView tv_txt_ok = delayDialog.findViewById(R.id.tv_driver_name);
//        final com.carpool.tagalong.views.RegularTextView tv_slide = delayDialog.findViewById(R.id.tv_slide);
//        final ImageView iv_drop_slider = delayDialog.findViewById(R.id.iv_drop_slider);
//        final RelativeLayout ll_slider_parent = delayDialog.findViewById(R.id.ll_slider_parent);
//        tv_txt_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delayDialog.dismiss();
//            }
//        });
//        delayDialog.show();
//
//        iv_drop_slider.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                int action = event.getAction();
//    /*            switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());*/
//                final int x = (int) event.getRawX();
//                final int y = (int) event.getRawY();
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
//                                view.getLayoutParams();
//
//                        xDelta = x - lParams.leftMargin;
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        int width = ll_slider_parent.getWidth() - view.getWidth();
//                        if ((x - xDelta) >= 0 && (x - xDelta) <= width) {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//                                    .getLayoutParams();
//
//                            if (x - xDelta <= width / 2) {
//                                layoutParams.leftMargin = 0;
//                                layoutParams.topMargin = 0;
//                                layoutParams.rightMargin = 0;
//                                layoutParams.bottomMargin = 0;
//                                view.setLayoutParams(layoutParams);
//
//                            } else if (x - xDelta > width / 2) {
//
//                                layoutParams.leftMargin = width;
//                                layoutParams.topMargin = 0;
//                                layoutParams.rightMargin = 0;
//                                layoutParams.bottomMargin = 0;
//                                view.setLayoutParams(layoutParams);
//                            }
//                        }
//                        break;
//                    case MotionEvent.ACTION_HOVER_EXIT:
//
//                        Log.e("@@parent wi", "---- Exit");
//
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//
//                        Log.e("@@parent wi", "---- Cancel");
//
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        int width2 = ll_slider_parent.getWidth() - view.getWidth();
///*                        Log.e("@@parent wi", "----" + width2);
//                        Log.e("@@parent (x - xDelta)", "----" + (x - xDelta));*/
//
//                        if ((x - xDelta) >= 0 && (x - xDelta) <= width2) {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//                                    .getLayoutParams();
//
//                            Log.e("@@parent x", "---- " + x);
//                            Log.e("@@parent xD", "---- " + xDelta);
////
////                            Log.e("@@parent x-xD", "---- "+(x - xDelta) );
//
//                            Log.e("@@parent x-xD", "---- " + x / 10);
//                            String color = "#00008000";
//                            if (x / 10 < 10) {
//                                color = "#0" + x / 10 + "008000";
//                            } else {
//                                color = "#" + x / 10 + "008000";
//                            }
//
//                            ll_slider_parent.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.DARKEN);
//
//                            layoutParams.leftMargin = x - xDelta;
//                            layoutParams.topMargin = 0;
//                            layoutParams.rightMargin = 0;
//                            layoutParams.bottomMargin = 0;
//                            view.setLayoutParams(layoutParams);
//                        }
//                        break;
//                }
//                return true;
//            }
//        });
//    }

//    private void showRatingSubmitDialog(Activity activity) {
//
//        final Dialog delayDialog = new Dialog(activity);
//        delayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        delayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        delayDialog.setContentView(R.layout.submit_review_dialog_layout);
//        delayDialog.setCancelable(true);
//        delayDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        com.carpool.tagalong.views.RegularTextView tv_txt_ok = delayDialog.findViewById(R.id.tv_driver_name);
//        final com.carpool.tagalong.views.RegularTextView tv_slide = delayDialog.findViewById(R.id.tv_slide);
//        final ImageView iv_drop_slider = delayDialog.findViewById(R.id.iv_drop_slider);
//        final RelativeLayout ll_slider_parent = delayDialog.findViewById(R.id.ll_slider_parent);
//        tv_txt_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delayDialog.dismiss();
//            }
//        });
//        delayDialog.show();
//    }

    private void showCommonForOkeyReviewDialog(Activity activity, String title, String msg, boolean isReach) {

        final Dialog delayDialog = new Dialog(activity);
        delayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delayDialog.setContentView(R.layout.ok_review_dialog_layout);
        delayDialog.setCancelable(true);
        delayDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        com.carpool.tagalong.views.RegularTextView title_text = delayDialog.findViewById(R.id.title_text);
        final com.carpool.tagalong.views.RegularTextView message_text = delayDialog.findViewById(R.id.message_text);
        final ImageView iv_topImage_image = delayDialog.findViewById(R.id.iv_topImage_image);

        title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayDialog.dismiss();
            }
        });
        delayDialog.show();
    }
}