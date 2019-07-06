package com.qiangqiang.qcamera.beans;

import java.io.Serializable;

public class ImageItem implements Serializable {
    /**
     *
     */
    private String sourcePath;//原文件Path
    private String zipPath;//压缩文件 path
    private String firstFramePath;//视频封面图
    private boolean isVideo = false;//是不是Video
    private long videoDuration;//video duration
    private String videoThumbPath;//video 缩略图路径
    private long createTime;//创建日期
    private int width;//宽
    private int height;//高
    private long length;//文件大小
    private String imageThumbPath;
    private boolean bSendFullPhoto = false;//是否發送原圖

    public boolean ismReServer() {
        return mReServer;
    }

    public void setmReServer(boolean mReServer) {
        this.mReServer = mReServer;
    }

    //是否取消服务器返回的照片
    private boolean mReServer ;

    public boolean ismReedit() {
        return mReedit;
    }

    public void setmReedit(boolean mReedit) {
        this.mReedit = mReedit;
    }

    //是否再次编辑
    private boolean mReedit = false;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumUrl() {
        return thumUrl;
    }

    public void setThumUrl(String thumUrl) {
        this.thumUrl = thumUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    private String videoUrl;
    private String thumUrl;
    private String url;
    private String photoId;

    public int getS3UploadStatus() {
        return S3UploadStatus;
    }

    public void setS3UploadStatus(int s3UploadStatus) {
        S3UploadStatus = s3UploadStatus;
    }

    private int S3UploadStatus;

    public int getObserverId() {
        return observerId;
    }

    public void setObserverId(int observerId) {
        this.observerId = observerId;
    }

    private int observerId;




	public int getHeight() {
		return height;
	}

    public boolean getUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    private boolean upload;

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public String getVideoThumbPath() {
        return videoThumbPath;
    }

    public void setVideoThumbPath(String videoThumbPath) {
        this.videoThumbPath = videoThumbPath;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sourcePath == null) ? 0 : sourcePath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageItem other = (ImageItem) obj;
        if (sourcePath == null) {
            if (other.sourcePath != null)
                return false;
        } else if (!sourcePath.equals(other.sourcePath))
            return false;
        return true;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFirstFramePath() {
        return firstFramePath;
    }

    public void setFirstFramePath(String firstFramePath) {
        this.firstFramePath = firstFramePath;
    }

    public String getImageThumbPath() {
        return imageThumbPath;
    }

    public void setImageThumbPath(String imageThumbPath) {
        this.imageThumbPath = imageThumbPath;
    }

    public boolean isbSendFullPhoto() {
        return bSendFullPhoto;
    }

    public void setbSendFullPhoto(boolean bSendFullPhoto) {
        this.bSendFullPhoto = bSendFullPhoto;
    }

    public void toggleBSendFullPhoto() {
        bSendFullPhoto = !bSendFullPhoto;
    }
}
