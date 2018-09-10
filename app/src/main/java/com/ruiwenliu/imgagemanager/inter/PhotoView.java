package com.ruiwenliu.imgagemanager.inter;

/**
 * Created by ruiwen
 * Data:2018/9/6 0006
 * Email:1054750389@qq.com
 * Desc:
 */

public  interface PhotoView {

    
    /**
          * 相机code
          * @return
          */
    int getCameraRequestCode();

    /**
     * 相册code
     *
     * @return
     */
    int getAlbumRequestCode();

    /**
     * 裁剪code
     *
     * @return
     */
    int getCropRequestCode();

    /**
     * 相机并裁剪code
     * @return
     */
    int getCameraAndCropRequestCode();

    /**
     * 相册并裁剪code
     *
     * @return
     */
    int getAlbumAndCropRequestCode();
}
