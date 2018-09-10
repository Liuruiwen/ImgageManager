package com.ruiwenliu.imgagemanager.util.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 *Created by ruiwen
 * Data:2018/9/7 0007
 * Email:1054750389@qq.com
 * Desc:
 */
public class CustomImageSizeModelFactory implements ModelLoaderFactory<CustomImageSizeModel,InputStream>{
    @Override
    public ModelLoader<CustomImageSizeModel, InputStream> build(Context context, GenericLoaderFactory factories) {

        return new CustomImageSizeUrlLoader(context);
    }

    @Override
    public void teardown() {

    }
}
