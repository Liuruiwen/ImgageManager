package com.ruiwenliu.imgagemanager.util.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;

import java.io.File;
import java.io.InputStream;

/**
 Created by ruiwen
 * Data:2018/9/7 0007
 * Email:1054750389@qq.com
 * Desc:
 */

public class GlideConfiguration implements GlideModule {

    // 需要在AndroidManifest.xml中声明
    //https://github.com/hpdx/fresco-helper
    // <meta-data
    //    android:name="com.yaphetzhao.glidecatchsimple.glide.GlideConfiguration"
    //    android:value="GlideModule" />

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        //自定义缓存目录

//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
//        builder.setDiskCache(new DiskLruCacheFactory(dirFolder, size));


        //设置磁盘缓存大小
//        int size = 100 * 1024 * 1024;
//        String dirFolder = "xia";
//        String dirName = "yu";
        //设置磁盘缓存

//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //内存缓存
//        MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(context);
//        int defaultMemoryCacheSize = memorySizeCalculator.getMemoryCacheSize();
//        int defalutBitmapPoolSize = memorySizeCalculator.getBitmapPoolSize();
//        builder.setMemoryCache(new LruResourceCache((int) (defalutBitmapPoolSize * 1.2)));//内部
//        builder.setBitmapPool(new LruBitmapPool((int) (defalutBitmapPoolSize * 1.2)));


        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                File cacheLocation = new File(context.getExternalCacheDir(),GlideCatchConfig.GLIDE_CARCH_DIR);
                cacheLocation.mkdirs();

                return DiskLruCacheWrapper.get(cacheLocation, 1024 * 1024 * 1500);
            }
        }).setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);



//        builder.setDiskCache(new DiskLruCacheFactory(GlideCatchConfig.GLIDE_CARCH_DIR, GlideCatchConfig.GLIDE_CATCH_SIZE));

//        XiayuGlideModule
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context,
//                GlideCatchConfig.GLIDE_CARCH_DIR,
//                GlideCatchConfig.GLIDE_CATCH_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //nil
        glide.register(CustomImageSizeModel.class, InputStream.class,new CustomImageSizeModelFactory());
    }
}
