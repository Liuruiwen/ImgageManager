package com.ruiwenliu.imgagemanager.util.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ruiwenliu.imgagemanager.R;


import java.io.File;
import java.math.BigDecimal;

/**
 Created by ruiwen
 * Data:2018/9/7 0007
 * Email:1054750389@qq.com
 * Desc:图片管理器
 */

public class GlideImgManager {
    private Context mContext;
    private static GlideImgManager glideImgManager;

    public GlideImgManager(Context context) {
        this.mContext = context;
    }

    public static GlideImgManager getInstance(Context context) {
        if (glideImgManager == null) {
            glideImgManager = new GlideImgManager(context.getApplicationContext());
        }
        return glideImgManager;
    }


    /**
     * 加载图片
     *
     * @param url
     * @param img
     */
    public void loadImage(String url, final ImageView img) {
        //正常加载
        Glide.with(mContext).load(url).placeholder(R.drawable.ic_loading).error(R.drawable.ic_loading_error)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(img);
    }



    /**
     * 加载圆型图片
     *
     * @param url
     * @param img
     */
    public void loadingCircle(String url, final ImageView img) {
        Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideCircleTransform(mContext)).placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                img.setImageDrawable(resource);
            }
        });
    }



    /**
     * 加载gif图片
     *
     * @param url
     * @param img
     */
    public void loadingGif(String url, ImageView img) {
        Glide.with(mContext)
                .load(url)
                .asGif()
//                .override(width, height)  //限制图片的加载大小
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error)
                .dontAnimate() //去掉显示动画
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE) //DiskCacheStrategy.NONE
                .into(img);
    }

    /**
     * 获取Glide磁盘缓存大小
     *
     * @return
     */
    public String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(new File(mContext.getExternalCacheDir() + "/" + GlideCatchConfig.GLIDE_CARCH_DIR)));
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }


    /*
       * 清除磁盘缓存
       *
       * */
    public boolean clearCacheDiskSelf() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(mContext).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(mContext).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清除Glide内存缓存
     */
    public static void clearMemoryError(Context context) {
        Glide.get(context).clearMemory();
    }


    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


    // 获取指定文件夹内所有文件大小的和
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }





}
