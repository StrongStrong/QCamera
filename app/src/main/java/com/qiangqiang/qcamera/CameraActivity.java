package com.qiangqiang.qcamera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qiangqiang.qcamera.camera.CameraContainer;
import com.qiangqiang.qcamera.camera.CameraPreView;
import com.qiangqiang.qcamera.camera.CameraView;
import com.qiangqiang.qcamera.camera.CircleImageView;
import com.qiangqiang.qcamera.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener,CameraContainer.TakePictureListener {

    private static final String TAG = "CameraActivity";
    private CameraContainer cameraContainer;

    private boolean mIsRecordMode=false;
    private CameraContainer mContainer;
    private CircleImageView mThumbView;
    private ImageButton mCameraShutterButton;
    private ImageButton mRecordShutterButton;
    private ImageView mFlashView;
    private ImageButton mSwitchModeButton;
    private ImageView mSwitchCameraView;
    private ImageView mSettingView;
    private View mHeaderBar;
    private boolean isRecording=false;
    private ImageView btnBack;
    private CameraPreView cameraPreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraContainer=findViewById(R.id.cameraContainer);
        getPermissionItem();
        initview();


    }


    private void getPermissionItem() {
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
        permissonItems.add(new PermissionItem(Manifest.permission.CAMERA, "相机", R.drawable.permission_ic_camera));
        HiPermission.create(this)
                .permissions(permissonItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        ToastUtils.showToast(CameraActivity.this,"关闭该权限");
                    }

                    @Override
                    public void onFinish() {
                        cameraContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        ToastUtils.showToast(CameraActivity.this,"拒绝了该权限");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        cameraContainer.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initview() {
        mHeaderBar=findViewById(R.id.camera_header_bar);
        mContainer=(CameraContainer)findViewById(R.id.cameraContainer);
        mThumbView=(CircleImageView)findViewById(R.id.btn_thumbnail);
        mCameraShutterButton=(ImageButton)findViewById(R.id.btn_shutter_camera);
        mRecordShutterButton=(ImageButton)findViewById(R.id.btn_shutter_record);
        mSwitchCameraView=(ImageView)findViewById(R.id.btn_switch_camera);
        mFlashView=(ImageView)findViewById(R.id.btn_flash_mode);
        mSwitchModeButton=(ImageButton)findViewById(R.id.btn_switch_mode);
        mSettingView=(ImageView)findViewById(R.id.btn_other_setting);
        btnBack=(ImageView)findViewById(R.id.btnBack);
        cameraPreView= (CameraPreView) findViewById(R.id.cameraPreView);
        clicklistener();
    }
    private void clicklistener() {

        mThumbView.setOnClickListener(this);
        mCameraShutterButton.setOnClickListener(this);
        mRecordShutterButton.setOnClickListener(this);
        mFlashView.setOnClickListener(this);
        mSwitchModeButton.setOnClickListener(this);
        mSwitchCameraView.setOnClickListener(this);
        mSettingView.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        cameraPreView.setPreViewCancelClickListener(new CameraPreView.PreViewCancelClickListener() {
            @Override
            public void preViewCancelClick() {
                mContainer.getCameraView().setVisibility(View.VISIBLE);
            }
        });
        cameraPreView.setPreViewDoneClickListener(new CameraPreView.PreViewDoneClickListener() {
            @Override
            public void preViewDoneClick(String path) {
                mContainer.getCameraView().setVisibility(View.VISIBLE);
                Bitmap bitmap= BitmapFactory.decodeFile(path);
                mThumbView.setImageBitmap(bitmap);
            }
        });
        mContainer.setOncompleteRecordListener(new CameraContainer.OncompleteRecordListener() {
            @Override
            public void onOncompleteRecord(String path) {
                mRecordShutterButton.setBackgroundResource(R.drawable.video_start);
                mContainer.getCameraView().setVisibility(View.GONE);
                cameraPreView.setVideo(path);
                if(isRecording){
                    isRecording=false;
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_shutter_camera) {
            mCameraShutterButton.setClickable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mContainer.takePicture(CameraActivity.this);
                }
            }).start();


        } else if (i == R.id.btn_thumbnail) {

        } else if (i == R.id.btn_flash_mode) {
            if (mContainer.getFlashMode() == CameraView.FlashMode.ON) {
                mContainer.setFlashMode(CameraView.FlashMode.OFF);
                mFlashView.setImageResource(R.drawable.btn_flash_off);
            } else if (mContainer.getFlashMode() == CameraView.FlashMode.OFF) {
                mContainer.setFlashMode(CameraView.FlashMode.AUTO);
                mFlashView.setImageResource(R.drawable.btn_flash_auto);
            } else if (mContainer.getFlashMode() == CameraView.FlashMode.AUTO) {
                mContainer.setFlashMode(CameraView.FlashMode.TORCH);
                mFlashView.setImageResource(R.drawable.btn_flash_torch);
            } else if (mContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                mContainer.setFlashMode(CameraView.FlashMode.ON);
                mFlashView.setImageResource(R.drawable.btn_flash_on);
            }

        } else if (i == R.id.btn_switch_mode) {
            if (mIsRecordMode) {
                mSwitchModeButton.setBackgroundResource(R.drawable.ic_switch_video);
                mCameraShutterButton.setVisibility(View.VISIBLE);
                mRecordShutterButton.setVisibility(View.GONE);
                //拍照模式下显示顶部菜单
                mHeaderBar.setVisibility(View.VISIBLE);
                mIsRecordMode = false;
                mContainer.switchMode(0);

//					stopRecord();
            } else {
                mSwitchModeButton.setBackgroundResource(R.drawable.ic_switch_camera);
                mCameraShutterButton.setVisibility(View.GONE);
                mRecordShutterButton.setVisibility(View.VISIBLE);
                //录像模式下隐藏顶部菜单
                mHeaderBar.setVisibility(View.VISIBLE);
                mIsRecordMode = true;
                mContainer.switchMode(0);
            }

        } else if (i == R.id.btn_shutter_record) {
            if (!isRecording) {
                mRecordShutterButton.setClickable(false);
                isRecording = mContainer.startRecord();
                fileType = "video";
                if (isRecording) {
                    mRecordShutterButton.setBackgroundResource(R.drawable.video_stop);
                }
                mRecordShutterButton.setClickable(true);
                mSwitchModeButton.setVisibility(View.INVISIBLE);
                mSwitchCameraView.setVisibility(View.INVISIBLE);
                mThumbView.setVisibility(View.GONE);
            } else {
                stopRecord();
                mSwitchModeButton.setVisibility(View.VISIBLE);
                mSwitchCameraView.setVisibility(View.VISIBLE);
                mThumbView.setVisibility(View.VISIBLE);
            }

        } else if (i == R.id.btn_switch_camera) {
            mContainer.switchCamera();

        } else if (i == R.id.btn_other_setting) {
            mContainer.setWaterMark();

        } else if (i == R.id.btnBack) {
            setResult(RESULT_OK);
            finish();

        } else {
        }

    }
    private void stopRecord() {
        if(3000>mContainer.recordTime){
            ToastUtils.showToast(this,R.string.sMiniRecordTime);
        }else{
            String path=mContainer.stopRecord(this);
            mContainer.recordTime=0;
            mRecordShutterButton.setBackgroundResource(R.drawable.video_start);
            mContainer.getCameraView().setVisibility(View.GONE);
            cameraPreView.setVideo(path);

            if(isRecording){
                isRecording=false;
            }
        }


    }

    @Override
    public void onTakePictureEnd(byte[] data) {
        fileType="image";
        mCameraShutterButton.setClickable(true);
        cameraPreView.setImage(data);
    }

    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {

    }
}
