package com.yahoo.topics.utils;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;

import com.yahoo.topics.R;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Helper class to handle system / device utils
 *
 * @author hdong
 */
public class AndroidUtil {
    private static float sDisplayDensity = -1;
    private static String sVersionName = null;
    private static int sVersionCode = 0;

    private static float sScreenRatio = 0;

    /**
     * Get os version of android
     *
     * @return
     */
    public static String getAndroidOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get version name of the polyvore app
     *
     * @return
     */
    public static String getAppVersionName(@NonNull Context context) {

        if (sVersionName == null) {
            try {
                sVersionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sVersionName;
    }

    public static String getDisplayName(@NonNull Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        return context.getPackageManager().getApplicationLabel(appInfo).toString();
    }

    /**
     * Return device info as a group
     *
     * @return
     */
    public static String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("device_name: " + getDeviceName());
        sb.append(" device_model: " + getDeviceModel());
        return sb.toString();
    }

    /**
     * Get the name of the device
     *
     * @return
     */
    public static String getDeviceName() {
        if (isKindleFire()) {
            return "amazon_kindle";
        }
        return Build.MODEL;
    }

    /**
     * Return model of device, phone or tablet?
     *
     * @return
     */
    public static String getDeviceModel() {
        // TODO need to improve this
        return "phone";
    }

    /**
     * Get version code of polyvore app
     *
     * @return
     */
    public static int getAppVersionCode(@NonNull Context context) {

        if (sVersionCode == 0) {
            try {
                sVersionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sVersionCode;
    }

    /**
     * return current active network's type name
     *
     * @return if it can find one, it can return null value.
     */
    public static String getActiveNetworkTypeName(@NonNull Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            return null;
        }

        return networkInfo.getTypeName();
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static long getAvailMem(@NonNull Context context) {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return availableMegs;
    }

    /**
     * Convert dp to pixel
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(@NonNull Context context, int dp) {
        return complexUnitToPx(context, dp, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * Convert complex unit to px
     *
     * @param context
     * @param value   value need to be converted
     * @param unit    Unit of the value need to be converted
     * @return
     */
    public static int complexUnitToPx(@NonNull Context context, int value, int unit) {
        float px =
                TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
        return (int) px;
    }

    /**
     * return display density
     *
     * @return
     */
    public static float getDisplayDensity(@NonNull Context context) {
        if (sDisplayDensity == -1) {
            sDisplayDensity = context.getResources().getDisplayMetrics().density;
        }
        return sDisplayDensity;
    }

    /**
     * Get screen height in dp
     *
     * @return
     */
    public static int getScreenHeightInDp(@NonNull Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        return (int) (height / metrics.density);
    }

    /**
     * Get screen width in dp
     *
     * @return
     */
    public static int getScreenWidthInDp(@NonNull Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        return (int) (width / metrics.density);
    }

    public static float getScreenRatio(@NonNull Context context) {
        if (sScreenRatio == 0 && getScreenHeightInDp(context) != 0) {
            sScreenRatio =
                    (float) getScreenWidthInDp(context) / (float) getScreenHeightInDp(context);
        }

        return sScreenRatio;
    }

    /**
     * Get status bar height of screen
     *
     * @return
     */
    public static int getStatusBarHeight(@NonNull Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * get action bar height
     *
     * @return
     */
    public static int getActionbarHeight(@NonNull Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, context.getResources().getDisplayMetrics()
            );
        }
        if (actionBarHeight == 0) {
            actionBarHeight = context.getResources()
                    .getDimensionPixelOffset(R.dimen.abc_action_bar_default_height_material);
        }
        return actionBarHeight;
    }

    /**
     * Get the shipping region
     *
     * @return
     */
    public static Object getShipRegion() {
        return Locale.getDefault().getCountry();
    }

    /**
     * Copy string to clipboard
     *
     * @param s
     */
    public static void copyTextToClipboard(@NonNull Context context, @NonNull String s) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", s);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Read string from clipboard
     *
     * @return
     */
    public static String getTextFromClipboard(@NonNull Context context) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription()
                .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            return item.getText().toString();
        }

        return null;
    }

    /**
     * Check whether the device has a camera
     *
     * @return
     */
    public static boolean isCameraAvailable(@NonNull Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * try to find android phone user's primary email address
     * google account type has precedence over other accounts.
     *
     * @return
     */
    public static String getPrimaryEmailAddress(@NonNull Context context) {

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = new Account[10];
        String firstEmail = null;
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                String type = account.type;

                if (TextUtils.isEmpty(possibleEmail)) {
                    continue;
                }
                if ("com.google".equals(type)) {
                    firstEmail = possibleEmail;
                    break;
                }
                if (firstEmail == null) {
                    firstEmail = possibleEmail;
                }
            }
        }

        return firstEmail;
    }

    /**
     * Check whether a certain app exits using the package name
     *
     * @param packName
     * @return
     */
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Check network connection
     *
     * @return return if there is network connection
     */
    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * check whetehr the current device is kindle fire.
     *
     * @return true if it is.
     */
    public static boolean isKindleFire() {
        return Build.MANUFACTURER.equals("Amazon")
                && (Build.MODEL.equals("Kindle Fire")
                || Build.MODEL.startsWith("KF"));
    }
}
