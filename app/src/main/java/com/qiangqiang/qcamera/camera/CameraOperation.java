package com.qiangqiang.qcamera.camera;

import android.hardware.Camera.PictureCallback;

public interface CameraOperation {
	/**  
	 *  开始录像
	 *  @return  是否成功开始录像
	 */
	public boolean startRecord();

	/**  
	 *  停止录像
	 *  @return 录像缩略图
	 */
	public String stopRecord();
	/**  
	 *   切换前置和后置相机
	 */
	public void switchCamera();
	/**  
	 *  获取当前闪光灯模式
	 *  @return   
	 */
	public CameraView.FlashMode getFlashMode();
	/**  
	 *  设置闪光灯模式
	 *  @param flashMode   
	 */
	public void setFlashMode(CameraView.FlashMode flashMode);
	/**  
	 *  拍照
	 *  @param callback 拍照回调函数 
	 *  @param listener 拍照动作监听函数  
	 */
	public void takePicture(PictureCallback callback, CameraContainer.TakePictureListener listener);
	/**  
	 *  相机最大缩放级别
	 *  @return   
	 */
	public int getMaxZoom();
	/**  
	 *  设置当前缩放级别
	 *  @param zoom   
	 */
	public void setZoom(int zoom);
	/**  
	 *  获取当前缩放级别
	 *  @return   
	 */
	public int getZoom();
}
