package com.qiangqiang.qcamera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.text.TextUtils;

import com.qiangqiang.qcamera.beans.ImageItem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by qiangqiang on 2017/3/14 0014.
 */

public class BitmapTools {

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     *            传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    public static void compressBitmap2File(Bitmap bitmap, String path, int max){
        File file=new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos=compress(bitmap,max);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void compressImageFile(String origPath, String targetPath, int max){
        File file=new File(targetPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos=compress(BitmapFactory.decodeFile(origPath),max);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap compressImageFile2Bitmap(String origPath, String targetPath, int max){
        File file=new File(targetPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos=compress(BitmapFactory.decodeFile(origPath),max);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());

    }
    public static Bitmap compressBitmap(Bitmap bitmap, String path, int max){
        File file=new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos=compress(bitmap,max);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    private static ByteArrayOutputStream compress(Bitmap bitmap, int max) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 99;
        while (baos.toByteArray().length / 1024 > max) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;// 每次都减少10
            //压缩比小于0，不再压缩
            if (options < 0) {
                break;
            }
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        return baos;

    }


    public static Bitmap getVideoThumbnail(String videoPath, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap == null) {
            return null;
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(),
                bitmap.getHeight(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }




    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void generateMiddleBmp(ImageItem imageItem, String origPath, String targetPath,
                                         double maxResolution, int maxSize) {
        if (!TextUtils.isEmpty(origPath)) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                // true,只是读图片大小，不申请bitmap内存
                opts.inJustDecodeBounds = true;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                BitmapFactory.decodeFile(origPath, opts);
                // 计算sampleSize的大小,samplesize大小是2的幂，任何其他值将四舍五入到最接近2的幂。
                opts.inSampleSize = computeSampleSize(opts, -1,
                        (int) (maxResolution * maxResolution));
                // 设置成true，加载图片
                opts.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(origPath, opts);
                // 如果图片有角度旋转，将图片旋转成正常角度
                int degree = getBitmapDegree(origPath);
                if (degree > 0) {
                    bm = rotateBitmapByDegree(bm, degree);
                }
                imageItem.setHeight(bm.getHeight());
                imageItem.setWidth(bm.getWidth());
                // 以下是将图片控制在固定大小内
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // scale
                int options = 100;
                // Store the bitmap into output stream(no compress)
                bm.compress(Bitmap.CompressFormat.JPEG, options, os);
                // Compress by loop
                while (os.toByteArray().length / 1024 > maxSize && options > 0) {
                    // Clean up os
                    os.reset();
                    // interval 10
                    bm.compress(Bitmap.CompressFormat.JPEG, options, os);
                    options -= 10;
                }
                // Generate compressed image file

                FileOutputStream fos = new FileOutputStream(targetPath);
                fos.write(os.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    /**
     * 对图片进行旋转
     *
     * @param bm
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        if (bm != null) {
            // 根据旋转角度，生成旋转矩阵
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            try {
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), matrix, true);
            } catch (OutOfMemoryError e) {
            }
            if (returnBm == null) {
                returnBm = bm;
            }
            if (bm != returnBm) {
                bm.recycle();
            }
        }
        return returnBm;
    }

    public static void generateMiddleBmp(String origPath, String targetPath,
                                         double maxResolution, int maxSize) {
        if (!TextUtils.isEmpty(origPath)) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                // true,只是读图片大小，不申请bitmap内存
                opts.inJustDecodeBounds = true;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                BitmapFactory.decodeFile(origPath, opts);
                // 计算sampleSize的大小,samplesize大小是2的幂，任何其他值将四舍五入到最接近2的幂。
                opts.inSampleSize = computeSampleSize(opts, -1,
                        (int) (maxResolution * maxResolution));
                // 设置成true，加载图片
                opts.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(origPath, opts);
                // 如果图片有角度旋转，将图片旋转成正常角度
                int degree = getBitmapDegree(origPath);
                if (degree > 0) {
                    bm = rotateBitmapByDegree(bm, degree);
                }
                // 以下是将图片控制在固定大小内
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // scale
                int options = 100;
                // Store the bitmap into output stream(no compress)
                bm.compress(Bitmap.CompressFormat.JPEG, options, os);
                // Compress by loop
                while (os.toByteArray().length / 1024 > maxSize && options > 0) {
                    // Clean up os
                    os.reset();
                    // interval 10
                    bm.compress(Bitmap.CompressFormat.JPEG, options, os);
                    options -= 10;
                }
                // Generate compressed image file

                FileOutputStream fos = new FileOutputStream(targetPath);
                fos.write(os.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 获取图片旋转的角度
     *
     * @param path
     * @return
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static void saveBitmapFile(Bitmap bitmap, String path){
        File file=new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();            //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                        opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);           //这里一定要将其设置回false，因为之前我们将其设置成了true
                        opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 调整拍照后图片旋转的问题
     * @param filePath
     */
    public static void adjustPhoto(String filePath){
        if (!TextUtils.isEmpty(filePath)) {
            
            BufferedOutputStream bos = null;
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeFile(filePath, new BitmapFactory.Options());
                // 如果图片有角度旋转，将图片旋转成正常角度
                int degree = getBitmapDegree(filePath);
                if (degree == 0) {
                    return;
                } else {
                    bm = rotateBitmapByDegree(bm, degree);
                }
                File file = new File(filePath);//将要保存图片的路径
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (bm != null) {
                        bm.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
