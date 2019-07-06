package com.qiangqiang.qcamera.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.qiangqiang.qcamera.utils.DeviceUtils;
import com.qiangqiang.qcamera.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraView extends SurfaceView implements CameraOperation {
    public final static String TAG = "CameraView";
    private Camera mCamera;
    private Context context;
    private boolean mIsFrontCamera;
    private MediaRecorder mMediaRecorder;
    private int mRotation;
    private Camera.Parameters mParameters;
    private String recordPath;
    private FlashMode mFlashMode;
    private int mZoom;

    public CameraView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(callback);
        openCamera();
        mIsFrontCamera = false;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getHolder().addCallback(callback);
        openCamera();
        mIsFrontCamera = false;
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getHolder().addCallback(callback);
        openCamera();
        mIsFrontCamera = false;
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        getHolder().addCallback(callback);
        openCamera();
        mIsFrontCamera = false;
    }
    protected boolean isRecording(){
        return mMediaRecorder!=null;
    }
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            openCamera();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(getHolder());
                    Camera.Parameters params = mCamera.getParameters();
                    params.setRotation(90);
                    mCamera.setParameters(params);
                    mCamera.setDisplayOrientation(90);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(context, "相机启动失败", Toast.LENGTH_SHORT).show();
//                ((Activity)context).finish();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            if (mCamera != null) {
                setCameraParams();
                mCamera.startPreview();
                mCamera.cancelAutoFocus();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            releaseCamera();

        }
    };

    private boolean openCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mIsFrontCamera) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

                    try {
                        mCamera = Camera.open(i);
                    } catch (Exception e) {
                        mCamera = null;
                        return false;
                    }
                }
            }
        } else {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                mCamera = null;
                return false;
            }

        }
        return true;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean startRecord() {
        if (mCamera == null) {
            openCamera();
        }
        if (mCamera == null) {
            return false;
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        } else {
            mMediaRecorder.reset();
        }
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder
                .setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder
                .setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//设置音频输出格式为3gp
        mMediaRecorder.setVideoSize(1280, 720);//设置录制视频尺寸
        mMediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        //mMediaRecorder.setVideoFrameRate(30);//每秒3帧
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//录制视频编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//设置音频编码为amr_nb

        //设置录像参数，由于应用需要此处取一个较小格式的视频
        //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        //设置输出视频朝向，便于播放器识别。由于是竖屏录制，需要正转90°
        //if(mIsFrontCamera){
        mMediaRecorder.setOrientationHint(mRotation);
        //}else{
        //	mMediaRecorder.setOrientationHint(90);
        //}

        try {
            String name = "video" + FileUtils.createFileNameByTime(".mp4");
            recordPath = FileUtils.getQCameraPath() + File.separator + name;
            File recordFile = new File(recordPath);
            mMediaRecorder.setOutputFile(recordFile
                    .getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String stopRecord() {
        try {
            if(mMediaRecorder!=null){
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder=null;
                //保存视频的缩略图
            }
            
            if(mParameters!=null&&mCamera!=null){
                //重新连接相机
                mCamera.reconnect();
                //停止预览，注意这里必须先调用停止预览再设置参数才有效
                mCamera.stopPreview();
                //设置参数为录像前的参数，不然如果录像是低配，结束录制后预览效果还是低配画面
                mCamera.setParameters(mParameters);
                //重新打开
                mCamera.startPreview();
                mParameters=null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return recordPath;
    }

    @Override
    public void switchCamera() {
        mIsFrontCamera=!mIsFrontCamera;
        openCamera();
        setCameraParams();
        try {
            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public FlashMode getFlashMode() {
        return mFlashMode;
    }

    @Override
    public void setFlashMode(FlashMode flashMode) {
        if(mCamera==null) return;
        mFlashMode = flashMode;
        Camera.Parameters parameters=mCamera.getParameters();
        switch (flashMode) {
            case ON:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                break;
            case AUTO:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case TORCH:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            default:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
        }
        if(!mIsFrontCamera){
            mCamera.setParameters(parameters);
        }
    }

    @Override
    public void takePicture(Camera.PictureCallback callback, CameraContainer.TakePictureListener listener) {
        mCamera.takePicture(null, null, callback);
    }


    @Override
    public int getMaxZoom() {
        if(mCamera==null) return -1;
        Camera.Parameters parameters=mCamera.getParameters();
        if(!parameters.isZoomSupported()) return -1;
        return parameters.getMaxZoom()>40?40:parameters.getMaxZoom();
    }
    public int getmRotation(){
        return mRotation;
    }
    @Override
    public void setZoom(int zoom) {
        if(mCamera==null) return;
        Camera.Parameters parameters;
        //注意此处为录像模式下的setZoom方式。在Camera.unlock之后，调用getParameters方法会引起android框架底层的异常
        //stackoverflow上看到的解释是由于多线程同时访问Camera导致的冲突，所以在此使用录像前保存的mParameters。
        if(mParameters!=null)
            parameters=mParameters;
        else {
            parameters=mCamera.getParameters();
        }

        if(!parameters.isZoomSupported()) return;
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
        mZoom=zoom;

    }

    @Override
    public int getZoom() {
        return mZoom;
    }

    /**
     * @Description: 闪光灯类型枚举 默认为关闭
     */
    public enum FlashMode {
        /**
         * ON:拍照时打开闪光灯
         */
        ON,
        /**
         * OFF：不打开闪光灯
         */
        OFF,
        /**
         * AUTO：系统决定是否打开闪光灯
         */
        AUTO,
        FlashMode,
        /**
         * TORCH：一直打开闪光灯
         */
        TORCH
    }
    private void setCameraParams() {
        Camera.Parameters params = mCamera.getParameters();
        setPreviewSize(params);
        setPictureSize(params);
        List<String> FocusModes = params.getSupportedFocusModes();
        if(FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        startOrientationChangeListener();


    }
    /**
     *   启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
     */
    private OrientationEventListener mOrEventListener;
    private  void startOrientationChangeListener() {
        mOrEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)){
                    rotation=0;
                }else if ((rotation > 45) && (rotation <= 135))  {
                    rotation=90;
                }
                else if ((rotation > 135) && (rotation <= 225)) {
                    rotation=180;
                }
                else if((rotation > 225) && (rotation <= 315)) {
                    rotation=270;
                }else {
                    rotation=0;
                }
                updateCameraOrientation(rotation);
            }
        };
        mOrEventListener.enable();
    }
    /**
     *   根据当前朝向修改保存图片的旋转角度
     */
    private void updateCameraOrientation(int mOrientation){
        if(mCamera!=null){
            Camera.Parameters parameters = mCamera.getParameters();
            //rotation参数为 0、90、180、270。水平方向为0。
            int rotation=90+mOrientation==360?0:90+mOrientation;
            //前置摄像头需要对垂直方向做变换，否则照片是颠倒的
            if(mIsFrontCamera){
                if(rotation==90) rotation=270;
                else if (rotation==270) rotation=90;
            }
            parameters.setRotation(rotation);//生成的图片转90°
            mRotation=rotation;
            //预览图片旋转90°
            //预览转90°
            mCamera.setParameters(parameters);
        }
    }
    public void setPreviewSize(Camera.Parameters parametes) {
        List<Camera.Size> localSizes = parametes.getSupportedPreviewSizes();
        Camera.Size biggestSize = null;
        Camera.Size fitSize = null;// 优先选屏幕分辨率
        Camera.Size targetSize = null;// 没有屏幕分辨率就取跟屏幕分辨率相近(大)的size
        Camera.Size targetSiz2 = null;// 没有屏幕分辨率就取跟屏幕分辨率相近(小)的size
        if(localSizes != null) {
            int cameraSizeLength = localSizes.size();
            for (int n = 0; n < cameraSizeLength; n++) {
                Camera.Size size = localSizes.get(n);
                if(biggestSize == null ||
                        (size.width >= biggestSize.width && size.height >= biggestSize.height)) {
                    biggestSize = size;
                }

                if(size.width == DeviceUtils.screenHPixels(context)
                        && size.height == DeviceUtils.screenWPixels(context)) {
                    fitSize = size;
                } else if (size.width == DeviceUtils.screenHPixels(context)
                        || size.height == DeviceUtils.screenWPixels(context)) {
                    if(targetSize == null) {
                        targetSize = size;
                    } else if (size.width < DeviceUtils.screenHPixels(context)
                            || size.height < DeviceUtils.screenWPixels(context)) {
                        targetSiz2 = size;
                    }
                }
            }

            if(fitSize == null) {
                fitSize = targetSize;
            }

            if(fitSize == null) {
                fitSize = targetSiz2;
            }

            if(fitSize == null) {
                fitSize = biggestSize;
            }
            parametes.setPreviewSize(fitSize.width, fitSize.height);
        }

    }
    /** 输出的照片为最高像素 */
    public void setPictureSize(Camera.Parameters parametes) {
        List<Camera.Size> localSizes = parametes.getSupportedPictureSizes();
        Camera.Size biggestSize = null;
        Camera.Size fitSize = null;// 优先选预览界面的尺寸
        Camera.Size previewSize = parametes.getPreviewSize();
        float previewSizeScale = 0;
        if(previewSize != null) {
            previewSizeScale = previewSize.width / (float) previewSize.height;
        }

        if(localSizes != null) {
            int cameraSizeLength = localSizes.size();
            for (int n = 0; n < cameraSizeLength; n++) {
                Camera.Size size = localSizes.get(n);
                if(biggestSize == null) {
                    biggestSize = size;
                } else if(size.width >= biggestSize.width && size.height >= biggestSize.height) {
                    biggestSize = size;
                }

                // 选出与预览界面等比的最高分辨率
                if(previewSizeScale > 0
                        && size.width >= previewSize.width && size.height >= previewSize.height) {
                    float sizeScale = size.width / (float) size.height;
                    if(sizeScale == previewSizeScale) {
                        if(fitSize == null) {
                            fitSize = size;
                        } else if(size.width >= fitSize.width && size.height >= fitSize.height) {
                            fitSize = size;
                        }
                    }
                }
            }

            // 如果没有选出fitSize, 那么最大的Size就是FitSize
            if(fitSize == null) {
                fitSize = biggestSize;
            }

            parametes.setPictureSize(fitSize.width, fitSize.height);
        }
    }
    protected void onFocus(Point point, Camera.AutoFocusCallback callback){
        if(mCamera==null){
            return;
        }
        Camera.Parameters parameters=mCamera.getParameters();
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas()<=0) {
            mCamera.autoFocus(callback);
            return;
        }
        List<Camera.Area> areas=new ArrayList<Camera.Area>();
        int left=point.x-300;
        int top=point.y-300;
        int right=point.x+300;
        int bottom=point.y+300;
        left=left<-1000?-1000:left;
        top=top<-1000?-1000:top;
        right=right>1000?1000:right;
        bottom=bottom>1000?1000:bottom;
        areas.add(new Camera.Area(new Rect(left,top,right,bottom), 100));
        parameters.setFocusAreas(areas);
        try {
            //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
            //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
            mCamera.setParameters(parameters);
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
    public void focusOnTouch(MotionEvent event, Camera.AutoFocusCallback callback) {
        Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
        Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1.5f);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 1000));

            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));

            parameters.setMeteringAreas(meteringAreas);
        }
        try {
            mCamera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }

        mCamera.autoFocus(callback);
    }

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (x / getResolution().width * 2000 - 1000);
        int centerY = (int) (y / getResolution().height * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    public Camera.Size getResolution() {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}
