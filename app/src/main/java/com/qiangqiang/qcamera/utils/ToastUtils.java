package com.qiangqiang.qcamera.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToast(Context context,String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context,int id){
        Toast.makeText(context,id,Toast.LENGTH_SHORT).show();
    }
}
