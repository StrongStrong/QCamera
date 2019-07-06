package com.qiangqiang.qcamera.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    /**
     * 获取QCamera根路径
     * @return
     */
    public static String getQCameraPath() {
        File file = new File("/storage/emulated/0/DCIM/qcamera");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
    /**
     *
     * @param extension 后缀名 如".jpg"
     * @return
     */
    public static String createFileNameByTime(String extension){
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        //查看是否带"."
        if(!extension.startsWith("."))
            extension="."+extension;
        return formatDate+extension;
    }

    public static void scanFileAsync(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    public static String getCachePath(String cachePath) {
        File f = new File(cachePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return cachePath;
    }

    public static String getAvatarPath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/avatar/");
    }

    public static String getCachePicPath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/cachePic/");

    }

    public static String getVideoPath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/video/");
    }

    public static String getVoicePath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/voice/");
    }

    public static String getFilePath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/file/");
    }

    public static String getPhotoPath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/photo/");
    }

    public static String getChatBackgroundPath(Context context) {
        return getCachePath(context.getExternalFilesDir(null).getAbsolutePath()
                + "/chatBackground/");
    }
}
