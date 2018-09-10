package com.ruiwenliu.imgagemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ruiwenliu.imgagemanager.inter.PhotoView;
import com.ruiwenliu.imgagemanager.presenter.PhotoPresenter;
import com.ruiwenliu.imgagemanager.presenter.CommonPresenter;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.ParameterizedType;

/**
 * Created by ruiwen
 * Data:2018/9/6 0006
 * Email:1054750389@qq.com
 * Desc:
 */

public abstract class PhotoActivity<V extends PhotoPresenter> extends AppCompatActivity implements PhotoView {
    public final int TYPE_CAMERA_CODE = 1;
    public final int TYPE_ALBUM_CODE = 2;
    public final int TYPE_CAMERA_CROP_CODE = 3;
    public final int TYPE_ALBUM_CROP_CODE = 4;
    public final int TYPE_CROP_CODE = 5;
    public V presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type，然后将其转换ParameterizedType。
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<? extends CommonPresenter> presenterClass = (Class<? extends CommonPresenter>) type.getActualTypeArguments()[0];
        try {
            this.presenter = (V) presenterClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        presenter.attachView(this);
    }

    @Override
    public int getCameraRequestCode() {

        return TYPE_CAMERA_CODE;
    }

    @Override
    public int getAlbumRequestCode() {

        return TYPE_ALBUM_CODE;
    }

    @Override
    public int getCropRequestCode() {

        return TYPE_CROP_CODE;
    }

    @Override
    public int getAlbumAndCropRequestCode() {
        return TYPE_ALBUM_CROP_CODE;
    }

    @Override
    public int getCameraAndCropRequestCode() {
        return TYPE_CAMERA_CROP_CODE;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }
}
