package com.ruiwenliu.imgagemanager.util.glide;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 *Created by ruiwen
 * Data:2018/9/7 0007
 * Email:1054750389@qq.com
 * Desc:
 */
public class CustomImageSizeUrlLoader extends BaseGlideUrlLoader<CustomImageSizeModel>{
    public CustomImageSizeUrlLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(CustomImageSizeModel model, int width, int height) {

        return model.requestCustomSizeUrl(width,height);
    }
}
