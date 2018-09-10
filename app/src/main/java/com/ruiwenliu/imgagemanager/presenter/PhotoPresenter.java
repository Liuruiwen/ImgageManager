package com.ruiwenliu.imgagemanager.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.ruiwenliu.imgagemanager.inter.PhotoView;
import com.ruiwenliu.imgagemanager.util.FileStorage;

import java.io.File;
import java.io.IOException;

/**
 * Created by ruiwen
 * Data:2018/9/6 0006
 * Email:1054750389@qq.com
 * Desc:
 */

public class PhotoPresenter extends CommonPresenter<PhotoView> {


    private String cameraUrl;
    private File imageFile;
    private Uri cropUri;

    /**
     * 打开系统相机获取图片
     */
    public void openCamera(boolean isCrop) {
        imageFile = getFileStore().createCameraFile();
        if (imageFile == null || isViewAttached() == false) { //当地址存储为空或未绑定Activity 、fragment则不执行下面操作
            return;
        }
        /*调用系统拍照*/
        Intent intent = new Intent();
        cropUri = null;
        try {
            //API>=24 android 7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cropUri = FileProvider.getUriForFile(mContext, "com.ruiwenliu.imgagemanager.fileprovider", imageFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            } else {//<24
                cropUri = Uri.fromFile(imageFile);
            }
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            cameraUrl = String.format("%1$s%2$s", "file://", imageFile.getPath());//相机存储地址
            ((Activity) mContext).startActivityForResult(intent, isCrop ? getView().getCameraAndCropRequestCode() : getView().getCameraRequestCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 获得相机存储地址
     *
     * @return
     */
    public String gerCameraStoreUrl() {
        return cameraUrl;
    }

    /**
     * 打开系统相册获取图片
     */
    public void openAlbum(boolean isCrop) {
        if (isViewAttached() == false) { //当地址存储为空或未绑定Activity 、fragment则不执行下面操作
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((Activity) mContext).startActivityForResult(intent, isCrop ? getView().getAlbumAndCropRequestCode() : getView().getAlbumRequestCode());
    }

    /**
     * 获得uri String 地址
     *
     * @param uri
     * @return
     */
    public String getUriUrl(Uri uri) {
        if (uri != null) {
            return uri.toString();
        }
        return "";
    }


    public Uri getCropUri() {
        return cropUri;
    }

    /**
     * 调用系统裁剪功能
     */
    public void cropPhoto(Uri uri) {
        File file = new FileStorage().createCropFile();
        if (file == null && isViewAttached() == false) { //当地址存储为空或未绑定Activity 、fragment则不执行下面操作
            return;
        }
        Uri outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        cameraUrl = String.format("%1$s%2$s", "file://", outputUri.getPath());//相机存储地址
        ((Activity) mContext).startActivityForResult(intent, getView().getCropRequestCode());
    }



}
