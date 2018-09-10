package com.ruiwenliu.imgagemanager.util.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by ruiwen
 * Data:2018/9/10 0010
 * Email:1054750389@qq.com
 * Desc:
 */

public class RxPermission {
    public static final String TAG = "RxPermission";
    public static final Object TRIGGER = new Object();

    // 定义RxPermissions的对象
    public RxPermissionsFragment mRxPermissionsFragment;

    // RxPermissions的构造方法
    public RxPermission(@NonNull Activity activity) {
        // 获取Fragment的实例
        mRxPermissionsFragment = getRxPermissionsFragment(activity);
    }

    // 获取Fragment的方法
    private RxPermissionsFragment getRxPermissionsFragment(Activity activity) {
        // 查询是否已经存在了该Fragment，这样是为了让该Fragment只有一个实例
        RxPermissionsFragment rxPermissionsFragment = null;
        try {
            rxPermissionsFragment = findRxPermissionsFragment(activity);
            boolean isNewInstance = rxPermissionsFragment == null;
            // 如果还没有存在，则创建Fragment，并添加到Activity中
            if (isNewInstance) {
                rxPermissionsFragment = new RxPermissionsFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .add(rxPermissionsFragment, TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rxPermissionsFragment;
    }

    // 利用tag去找是否已经有该Fragment的实例
    private RxPermissionsFragment findRxPermissionsFragment(Activity activity) {
        return (RxPermissionsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public void setLogging(boolean logging) {
        mRxPermissionsFragment.setLogging(logging);
    }

    /**
     * 上面代码的作用是传入一个Observable<Object>对象，然后转换成一个Observable<Boolean>对象，
     * 而这个Observable<Object>对象就是前面Observable.just(Object)所创建的对象，
     * 同时ensure()方法传入了permissions权限申请列表，在转换的初步，
     * 就是需要根据permissions权限列表去申请权限，具体操作在request()方法中，下面看看request()的实现
     *
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Boolean> ensure(final String... permissions) {
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<T> o) {
                return request(o, permissions)
                        // Transform Observable<Permission> to Observable<Boolean>
                        .buffer(permissions.length)
                        .flatMap(new Function<List<Permission>, ObservableSource<Boolean>>() {
                            @Override
                            public ObservableSource<Boolean> apply(List<Permission> permissions) throws Exception {
                                if (permissions.isEmpty()) {
                                    // Occurs during orientation change, when the subject receives onComplete.
                                    // In that case we don't want to propagate that empty list to the
                                    // subscriber, only the onComplete.
                                    return Observable.empty();
                                }
                                // Return true if all permissions are granted.
                                for (Permission p : permissions) {
                                    if (!p.granted) {
                                        return Observable.just(false);
                                    }
                                }
                                return Observable.just(true);
                            }
                        });
            }
        };
    }

    /**
     * Map emitted items from the source observable into {@link Permission} objects for each
     * permission in parameters.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Permission> ensureEach(final String... permissions) {
        return new ObservableTransformer<T, Permission>() {
            @Override
            public ObservableSource<Permission> apply(Observable<T> o) {
                return request(o, permissions);
            }
        };
    }

    /**
     * compose()的参数就为Observable.Transformer对象，该对象的作用是将一个类型的Observable对象转换成另外一个类型的Observable对象，
     * 同时也可以对自身对象的一些重复操作进行封装，避免重复编写代码
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Boolean> request(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensure(permissions));
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Permission> requestEach(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEach(permissions));
    }

    private Observable<Permission> request(final Observable<?> trigger, final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");
        }
        return oneOf(trigger, pending(permissions))
                .flatMap(new Function<Object, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> apply(Object o) throws Exception {
                        return requestImplementation(permissions);
                    }
                });
    }


    /**
     * 首先会判断申请权限列表是否为空，如果为空就会抛出异常，然后通过oneOf方法和pending()方法来创建合并Observable对象，下面看一下这两个方法
     * @param permissions
     * @return
     */
    private Observable<?> pending(final String... permissions) {
        // 循环遍历，查询该权限是否已经在申请过了
        for (String p : permissions) {
            if (!mRxPermissionsFragment.containsByPermission(p)) {
                // 如果列表中有一个权限未在Fragment的HashMap集合中保存
                // 则返回Observeble.empty()，返回的这个Observable对象
                // 只会调用onComplete()方法，所以并不会进入flatMap等操作// 符号中
                return Observable.empty();
            }
        }
        return Observable.just(TRIGGER);
    }

    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
        if (trigger == null) {
            return Observable.just(TRIGGER);
        }
        return Observable.merge(trigger, pending);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Observable<Permission> requestImplementation(final String... permissions) {
        // 创建集合保存已经申请的权限
        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        // 创建集合保存未申请的权限
        List<String> unrequestedPermissions = new ArrayList<>();

        // 循环权限申请列表
        for (String permission : permissions) {
            mRxPermissionsFragment.log("Requesting permission " + permission);
            // 如果权限已经申请过了，则直接保存到集合中
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(new Permission(permission, true, false)));
                continue;
            }
            // 如果权限被撤销，则将其作为申请被拒绝的权限保存到集合中
            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(Observable.just(new Permission(permission, false, false)));
                continue;
            }
            // 先去RxPermissionsFragment中查询是否已经存在了该权限
            PublishSubject<Permission> subject = mRxPermissionsFragment.getSubjectByPermission(permission);
            // Create a new subject if not exists

            // 如果还未存在，则创建一个PublishSubject对象
            if (subject == null) {
                // 执行到这里代表说，权限暂时未被申请，将其保存到“未申请”的集合中
                unrequestedPermissions.add(permission);
                subject = PublishSubject.create();
                mRxPermissionsFragment.setSubjectForPermission(permission, subject);
            }
            // 将对象保存到集合中
            list.add(subject);
        }
        // 如果有未申请的权限，则进行权限的申请操作
        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            // 权限的申请操作
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        }
        // 利用list集合创建Observable对象，并且用concat进行链接，返回一个Observable<Permission>对象
        return Observable.concat(Observable.fromIterable(list));
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    @SuppressWarnings("WeakerAccess")
    public Observable<Boolean> shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        if (!isMarshmallow()) {
            return Observable.just(false);
        }
        return Observable.just(shouldShowRequestPermissionRationaleImplementation(activity, permissions));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mRxPermissionsFragment.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mRxPermissionsFragment.requestPermissions(permissions);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() || mRxPermissionsFragment.isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() && mRxPermissionsFragment.isRevoked(permission);
    }

    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults) {
        mRxPermissionsFragment.onRequestPermissionsResult(permissions, grantResults, new boolean[permissions.length]);
    }


}
