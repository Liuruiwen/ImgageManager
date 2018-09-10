package com.ruiwenliu.imgagemanager.inter;

/**
 * Created by ruiwen
 * Data:2018/9/6 0006
 * Email:1054750389@qq.com
 * Desc:
 */

public interface CommonView<V> {
    /**
     * 将Activity或者Fragment绑定到Presenter
     */
    void attachView(V view);

    /**
     * 将Activity或者Fragment从Presenter中分离出来
     */
    void detachView();
}
