package com.psm.mytable.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.psm.mytable.App;


public class ToastUtils {
    private ToastUtils(){ }

    public static void showHErrorttpStatusToast(int httpStatus){
        showToast(App.Companion.getInstance(), "HTTP Status : " + httpStatus, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String message){
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int stringResId){
        showToast(context, context.getString(stringResId));
    }

    public static void showLongToast(Context context, int stringResId){
        showLongToast(context, context.getString(stringResId));
    }

    public static void showLongToast(Context context, String message){
        showToast(context, message, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }

    public static void showToast(String message){
        showToast(App.Companion.getInstance(), message, Toast.LENGTH_SHORT);
    }

    public static void showToast(@StringRes int resId){
        showToast(App.Companion.getInstance(), App.Companion.getInstance().getString(resId), Toast.LENGTH_SHORT);
    }

}
