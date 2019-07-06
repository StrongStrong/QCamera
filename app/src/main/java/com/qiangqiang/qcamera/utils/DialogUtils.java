package com.qiangqiang.qcamera.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.qiangqiang.qcamera.R;


public class DialogUtils {
    public static ProgressDialog dialog;
    private static AlertDialog askAvoidDialog;
    private static ProgressDialog loadPoiDataDialog;

    /**
     * @param context
     * @param mesg
     * @param isCancel false 不能取消，true能取消
     */
    public static void showDialog(Context context, String mesg, boolean isCancel) {
        try {
            if (context != null && context instanceof Activity) {
                //如果对应的activity已销毁，那么就不显示dialog了
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage(mesg);
            dialog.setCancelable(isCancel);
            dialog.show();
        } catch (Exception e) {

        }
    }



    public static void dimissDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
        }
    }





    public static void dimissExitDialog() {
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
            exitDialog = null;
        }
    }



    public static void dimissAskAvoidDialog() {
        if (askAvoidDialog != null && askAvoidDialog.isShowing()) {
            askAvoidDialog.dismiss();
            askAvoidDialog = null;
        }
    }


    private static Runnable dimissExitDialogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (exitDialog != null && exitDialog.isShowing()) {
                    exitDialog.dismiss();
                    exitDialog = null;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };

    public static boolean isDialogShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    private static AlertDialog exitDialog;
    private static AlertDialog user4GDialog;
    private static AlertDialog deleteMapDialog;

}
