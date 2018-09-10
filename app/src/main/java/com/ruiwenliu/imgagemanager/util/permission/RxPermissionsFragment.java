package com.ruiwenliu.imgagemanager.util.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by ruiwen
 * Data:2018/9/7 0007
 * Email:1054750389@qq.com
 * Desc:
 */

public class RxPermissionsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();
    private boolean mLogging;

    public RxPermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 禁止横竖屏切换时的Fragment的重建
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 如果请求码不符合，则直接返回
        if (requestCode != PERMISSIONS_REQUEST_CODE) return;
        // 以权限列表的长度为容器的size创建一个boolean数组
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];
        // 循环遍历，看权限是否被永久拒绝了
        for (int i = 0; i < permissions.length; i++) {
            // 这里会调用Fragment中的shouldShowRequestPermissionRationale()方法，然后这个方法如果是申请成功会返回true，如果被点击了不在提醒，并且拒绝权限时，则会返回false
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }
        // 调用重载方法
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        // 循环权限列表
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            // 查询mSubjects集合中是否存在代表该permission的PublishSubject对象
            PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
            // 如果没有，则直接返回
            if (subject == null) {
                // No subject found
                Log.e(RxPermission.TAG, "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            // 将集合中的permission的PublishSubject对象进行移除
            mSubjects.remove(permissions[i]);
            // 判断是否申请成功
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            // 返回相应的对象
            subject.onNext(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]));
            subject.onComplete();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    public PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public PublishSubject<Permission> setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        return mSubjects.put(permission, subject);
    }

    void log(String message) {
        if (mLogging) {
            Log.d(RxPermission.TAG, message);
        }
    }


}
