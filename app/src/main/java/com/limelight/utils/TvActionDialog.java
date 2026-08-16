package com.limelight.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/** A fully self-rendered action dialog for TV firmware with broken framework menus. */
public final class TvActionDialog {
    public static final class Action {
        public final CharSequence label;
        public final Runnable callback;

        public Action(CharSequence label, Runnable callback) {
            this.label = label;
            this.callback = callback;
        }
    }

    private TvActionDialog() {
    }

    public static Dialog show(Activity activity, CharSequence title, CharSequence message,
                              List<Action> actions) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int outerPadding = dp(activity, 28);
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(outerPadding, dp(activity, 22), outerPadding, dp(activity, 22));
        root.setBackground(roundedBackground(Color.WHITE, dp(activity, 22)));

        TextView titleView = new TextView(activity);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(28);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, dp(activity, 6), 0, dp(activity, 16));
        root.addView(titleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (message != null && message.length() != 0) {
            TextView messageView = new TextView(activity);
            messageView.setText(message);
            messageView.setTextColor(Color.BLACK);
            messageView.setTextSize(18);
            messageView.setGravity(Gravity.CENTER);
            messageView.setPadding(dp(activity, 8), 0, dp(activity, 8), dp(activity, 16));
            root.addView(messageView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        LinearLayout actionList = new LinearLayout(activity);
        actionList.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.addView(actionList, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button firstButton = null;
        for (final Action action : actions) {
            Button button = createActionButton(activity, action.label);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(activity, 58));
            params.topMargin = dp(activity, 6);
            params.bottomMargin = dp(activity, 6);
            actionList.addView(button, params);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (action.callback != null) {
                        action.callback.run();
                    }
                }
            });
            if (firstButton == null) {
                firstButton = button;
            }
        }

        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams attributes = window.getAttributes();
            int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            attributes.width = Math.min((int)(screenWidth * 0.68f), dp(activity, 680));
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.dimAmount = 0.65f;
            window.setAttributes(attributes);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        if (firstButton != null) {
            firstButton.requestFocus();
        }
        return dialog;
    }

    private static Button createActionButton(Activity activity, CharSequence label) {
        Button button = new Button(activity);
        button.setText(label);
        button.setTextSize(20);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setSingleLine(false);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused },
                new int[] { android.R.attr.state_pressed },
                new int[] {}
        };
        button.setTextColor(new ColorStateList(states, new int[] {
                Color.WHITE, Color.WHITE, Color.BLACK
        }));

        StateListDrawable background = new StateListDrawable();
        background.addState(new int[] { android.R.attr.state_focused },
                roundedBackground(Color.rgb(20, 112, 230), dp(activity, 8)));
        background.addState(new int[] { android.R.attr.state_pressed },
                roundedBackground(Color.rgb(14, 88, 190), dp(activity, 8)));
        background.addState(new int[] {},
                roundedBackground(Color.rgb(235, 235, 235), dp(activity, 8)));
        button.setBackground(background);
        return button;
    }

    private static GradientDrawable roundedBackground(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private static int dp(Activity activity, int value) {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}
