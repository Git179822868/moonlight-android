package com.limelight.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

public final class TvUtils {
    private TvUtils() {
    }

    public static boolean isTelevision(Context context) {
        PackageManager packageManager = context.getPackageManager();
        boolean hasTvFeature = packageManager != null &&
                (packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
                        packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION));
        int uiModeType = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_TYPE_MASK;
        return hasTvFeature || uiModeType == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
