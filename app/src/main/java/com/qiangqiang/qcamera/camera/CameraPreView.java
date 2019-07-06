package com.qiangqiang.qcamera.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.qiangqiang.qcamera.R;
import com.qiangqiang.qcamera.beans.ImageItem;
import com.qiangqiang.qcamera.utils.BitmapTools;
import com.qiangqiang.qcamera.utils.Constant;
import com.qiangqiang.qcamera.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraPreView extends RelativeLayout implements View.OnClickListener{
	private Context context;
	private MatrixImageView matrixImageView;
	private ImageView img_cancel,img_done;
	private ImageView btnBack;
	private VideoView video;
	private String sendType;
	private String fileType;
	private ImageItem imageItem;
	private FrameLayout control_view;
	public CameraPreView(Context context){
		super(context);
		this.context=context;
		initView();
	}
	public CameraPreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initView();
	}
	private void initView() {
		inflate(context, R.layout.camera_preview, this);
		matrixImageView= (MatrixImageView) findViewById(R.id.image);
		video= (VideoView) findViewById(R.id.videoview);
		img_cancel= (ImageView) findViewById(R.id.img_cancel);
		img_done= (ImageView) findViewById(R.id.img_done);
		btnBack= (ImageView) findViewById(R.id.btnBack);
		control_view= (FrameLayout) findViewById(R.id.control_view);
		clickListener();
	}

	private void clickListener() {
		img_done.setOnClickListener(this);
		img_cancel.setOnClickListener(this);
		btnBack.setOnClickListener(this);
	}
	private byte[] bytes;
	public void setImage(final byte[] bytes){
		setVisibility(View.VISIBLE);
		imageItem=new ImageItem();
		this.bytes=bytes;
		fileType="image";
		video.setVisibility(View.GONE);
		matrixImageView.setImageBitmap(null);
		control_view.setVisibility(View.GONE);
		matrixImageView.setVisibility(View.VISIBLE);

		new Thread(new Runnable() {

			@Override
			public void run() {
				long start= System.currentTimeMillis();
				String sourcePath= FileUtils.getQCameraPath()+ File.separator+ System.currentTimeMillis() + ".jpeg";
				getFile(bytes,sourcePath);
				imageItem.setSourcePath(sourcePath);
				FileUtils.scanFileAsync(context,sourcePath);
				String zipPath=FileUtils.getCachePicPath(context)+ File.separator+ System.currentTimeMillis() + ".jpeg";
				BitmapTools.generateMiddleBmp(imageItem,sourcePath,zipPath,Constant.PHOTO_MAX_EDGE_RESOLUTION,
						Constant.IMAGE_MAX_SIZE);
				imageItem.setZipPath(zipPath);
				Bitmap bitmap= BitmapFactory.decodeFile(zipPath);
				handler.sendMessage(handler.obtainMessage(HANDLER_BITMAP,bitmap));
//				try {
//					Bitmap bitmap  = Bimp.revitionImageSize(sourcePath);
//					if(Build.BRAND.equals("samsung")){
//						Bitmap angleBitmap = getCameraAngle(bitmap);
//						if(angleBitmap!=null){
//							bitmap=angleBitmap;
//						}
//
//					}
//					handler.sendMessage(handler.obtainMessage(HANDLER_BITMAP,bitmap));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}


			}
		}).start();

	}
	public void setVideo(final String path){
		setVisibility(View.VISIBLE);
		fileType="video";
		video.setVisibility(View.VISIBLE);
		matrixImageView.setVisibility(View.GONE);
		control_view.setVisibility(View.GONE);
		video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return true;
			}
		});
		video.setVideoPath(path);
		video.start();
		video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				video.setVideoPath(path);
				video.start();
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				imageItem=getVideoItem(path);
				handler.sendMessage(handler.obtainMessage(HANDLER_V));
			}
		}).start();
	}
	public void setSendType(String sendType){
		this.sendType=sendType;
	}
	public void releaseBitmap(){
	}
	private final int HANDLER_IMAGE=101;
	private final int HANDLER_VIDEO=102;
	private final int HANDLER_BITMAP=103;
	private final int HANDLER_V=104;
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case HANDLER_IMAGE:
					setVisibility(View.GONE);
					ImageItem imageItem= (ImageItem) msg.obj;
					preViewDoneClickListener.preViewDoneClick(imageItem.getZipPath());
					break;
				case HANDLER_VIDEO:
					video.setVisibility(View.GONE);
					video.suspend();
					setVisibility(View.GONE);
					ImageItem videoItem= (ImageItem) msg.obj;
					preViewDoneClickListener.preViewDoneClick(videoItem.getVideoThumbPath());
					break;
				case HANDLER_BITMAP:
					matrixImageView.setImageBitmap((Bitmap) msg.obj);
					control_view.setVisibility(View.VISIBLE);
					break;
				case HANDLER_V:
					control_view.setVisibility(View.VISIBLE);
					break;
				default:
					break;
			}
		}
	};
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.img_done) {


		} else if (i == R.id.img_cancel) {

		} else if (i == R.id.btnBack) {

		} else {
		}
	}
	/*
	* 根据byte数组，生成文件
	*/
	public static void getFile(byte[] bytes, String filePath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	private PreViewCancelClickListener preViewCancelClickListener;
	public void setPreViewCancelClickListener(PreViewCancelClickListener preViewCancelClickListener){
		this.preViewCancelClickListener=preViewCancelClickListener;
	}
	public interface PreViewCancelClickListener{
		public void preViewCancelClick();
	}
	private PreViewDoneClickListener preViewDoneClickListener;
	public void setPreViewDoneClickListener(PreViewDoneClickListener preViewDoneClickListener){
		this.preViewDoneClickListener=preViewDoneClickListener;
	}
	public interface PreViewDoneClickListener{
		public void preViewDoneClick(String path);
	}
	private ImageItem getImageItem(String path) {
		ImageItem bean = new ImageItem();
		bean.setSourcePath(path);
		bean.setZipPath(path);
		File file = new File(path);
		bean.setLength(file.length());
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		bean.setWidth(options.outWidth);
		bean.setHeight(options.outHeight);
		FileUtils.scanFileAsync(context,path);
		return bean;
	}

	private ImageItem getVideoItem(String path) {
		ImageItem bean = new ImageItem();
		android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
		mmr.setDataSource(path);
		String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
		bean.setVideoDuration(Long.parseLong(duration));
		bean.setVideo(true);
		bean.setSourcePath(path);
		bean.setZipPath(path);
		File file = new File(path);
		bean.setLength(file.length());
		Bitmap firstFrameBitmap = BitmapTools.getVideoThumbnail(path, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
		bean.setWidth(firstFrameBitmap.getWidth());
		bean.setHeight(firstFrameBitmap.getHeight());
//		String firstFramePath = CommonTools.getCachePicPath(context) + System.currentTimeMillis() + ".jpeg";
//		BitmapTools.compressBitmap2File(firstFrameBitmap, firstFramePath, Const.IMAGE_MAX_SIZE);
//		bean.setFirstFramePath(firstFramePath);
		String VideoThumbPath = FileUtils.getCachePicPath(context) + "thumb_" + System.currentTimeMillis() + ".jpeg";
		BitmapTools.compressBitmap2File(firstFrameBitmap, VideoThumbPath, Constant.THUMB_IMAGE_MAX_SIZE);
		bean.setVideoThumbPath(VideoThumbPath);
		FileUtils.scanFileAsync(context,path);
		return bean;
	}


	private Bitmap getCameraAngle(Bitmap bit) {
		CameraContainer cameraContainer= (CameraContainer) ((Activity)context).findViewById(R.id.cameraContainer);
		Bitmap b = rotateBitmapByDegree(bit,cameraContainer.getCameraView().getmRotation());
		return b;
	}

	private Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;


		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		return returnBm;
	}
	public void releaseVideo(){
		if(video!=null){
			video.suspend();
		}
	}

}