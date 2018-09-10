package com.ruiwenliu.imgagemanager.presenter;

import android.content.Context;
import android.support.v4.content.Loader;

import com.ruiwenliu.imgagemanager.inter.CommonView;
import com.ruiwenliu.imgagemanager.inter.PhotoView;
import com.ruiwenliu.imgagemanager.util.FileStorage;

import java.lang.ref.WeakReference;

/**
 * Created by ruiwen
 * Data:2018/9/6 0006
 * Email:1054750389@qq.com
 * Desc:
 */

public class CommonPresenter<V extends PhotoView> implements CommonView<V> {

    private WeakReference<V> viewReference; //子类的弱引用
    public Context mContext;
    public FileStorage fileStore = null;



    @Override
    public void attachView(V view) {
        viewReference = new WeakReference<>(view);
        if(isViewAttached()){
            mContext = (Context) getView();
        }

    }

    /**
     * 检查Activity或者Fragment是否已经绑定到了Presenter层
     *
     * @return 是否已经绑定
     */
    public boolean isViewAttached() {
        return viewReference != null && viewReference.get() != null;
    }


    /**
     * @return 获取实现了MvpView接口的Activity或者Fragment的引用用来实现回调
     */
    public V getView() {
        return viewReference == null ? null : viewReference.get();
    }

    @Override
    public void detachView() {
        if (viewReference != null) {
            viewReference.clear();
            viewReference = null;
        }
        getFileStore().destory();;
        fileStore = null;
    }

    /**
     * 获取图片存储地址
     * @return
     */
    public FileStorage getFileStore() {

        if (fileStore == null) {
            fileStore = new FileStorage();
        }
        return fileStore;
    }


}
