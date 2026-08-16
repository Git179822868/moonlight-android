package com.limelight.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.limelight.utils.TvUtils;

/**
 * A ListPreference with a self-rendered, D-pad-friendly choice dialog on TVs.
 *
 * <p>Some TV firmware renders the framework ListPreference choice list as an
 * empty dialog. Using regular RadioButtons avoids that framework dialog while
 * preserving the normal preference behavior on phones and tablets.</p>
 */
public class TvListPreference extends ListPreference {
    public TvListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TvListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TvListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TvListPreference(Context context) {
        super(context);
    }

    private boolean isTelevision() {
        return TvUtils.isTelevision(getContext());
    }

    private int dp(int value) {
        return (int) (value * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onClick() {
        if (!isTelevision() || getEntries() == null || getEntryValues() == null) {
            super.onClick();
            return;
        }

        showTvChoiceDialog();
    }

    private void showTvChoiceDialog() {
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        final int selectedIndex = findIndexOfValue(getValue());
        final RadioButton[] optionViews = new RadioButton[entries.length];
        final AlertDialog[] dialogHolder = new AlertDialog[1];

        LinearLayout optionContainer = new LinearLayout(getContext());
        optionContainer.setOrientation(LinearLayout.VERTICAL);
        optionContainer.setPadding(dp(12), 0, dp(12), 0);

        for (int i = 0; i < entries.length; i++) {
            final int index = i;
            RadioButton option = new RadioButton(getContext());
            option.setText(entries[i]);
            option.setTextColor(Color.BLACK);
            option.setTextSize(20);
            option.setGravity(android.view.Gravity.CENTER_VERTICAL);
            option.setPadding(dp(20), dp(10), dp(20), dp(10));
            option.setMinHeight(dp(56));
            option.setFocusable(true);
            option.setFocusableInTouchMode(true);
            option.setBackgroundResource(android.R.drawable.list_selector_background);
            option.setChecked(i == selectedIndex);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newValue = entryValues[index].toString();
                    if (callChangeListener(newValue)) {
                        setValue(newValue);
                    }
                    dialogHolder[0].dismiss();
                }
            });
            option.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BUTTON_A &&
                            event.getAction() == KeyEvent.ACTION_UP) {
                        view.performClick();
                        return true;
                    }
                    return false;
                }
            });

            optionContainer.addView(option, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            optionViews[i] = option;
        }

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setFillViewport(true);
        scrollView.addView(optionContainer);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getTitle())
                .setView(scrollView)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialogHolder[0] = dialog;
        dialog.setOnShowListener(new android.content.DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                int focusIndex = selectedIndex >= 0 ? selectedIndex : 0;
                if (optionViews.length > 0) {
                    optionViews[focusIndex].requestFocus();
                }
            }
        });
        dialog.show();
    }
}
