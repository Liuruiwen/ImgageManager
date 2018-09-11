package com.ruiwenliu.imgagemanager;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ruiwenliu.imgagemanager.adapter.AlbumAdapter;
import com.ruiwenliu.imgagemanager.bean.AlbumBean;
import com.ruiwenliu.imgagemanager.presenter.PhotoPresenter;
import com.ruiwenliu.imgagemanager.util.glide.GlideImgManager;
import com.ruiwenliu.imgagemanager.util.permission.RxPermission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class MainActivity extends PhotoActivity<PhotoPresenter> {

    private final int ivCount = 9;//你要获取图片的数量
    private final int TYPE_REQIEST_CODE = 6;
    @BindView(R.id.act_img)
    ImageView mImg;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private AlbumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TYPE_CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    GlideImgManager.getInstance(this).loadImage(presenter.gerCameraStoreUrl(), mImg);
                }
                break;
            case TYPE_ALBUM_CODE:
                if (data != null && data.getData() != null) {
                    GlideImgManager.getInstance(this).loadImage(presenter.getUriUrl(data.getData()), mImg);
                }
                break;
            case TYPE_CAMERA_CROP_CODE:
                if (resultCode == RESULT_OK) {
                    presenter.cropPhoto(presenter.getCropUri());
                }
            case TYPE_ALBUM_CROP_CODE:
                if (data != null && data.getData() != null) {
                    presenter.cropPhoto(data.getData());
                }
                break;
            case TYPE_CROP_CODE:
                if (resultCode == RESULT_OK) {
                    GlideImgManager.getInstance(this).loadImage(presenter.gerCameraStoreUrl(), mImg);
                }
                break;
            case TYPE_REQIEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<AlbumBean> listAlbum = (List<AlbumBean>) data.getSerializableExtra("list");
                    if (mAdapter == null) {
                        mAdapter = new AlbumAdapter();
                        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
                        recyclerview.setAdapter(mAdapter);
                        mAdapter.setNewData(listAlbum);
                    }else {
                        mAdapter.setNewData(listAlbum);
                    }
                }

                break;
        }
    }

    @OnClick({R.id.act_camera, R.id.act_camera_crop, R.id.act_album_crop, R.id.act_custom})
    public void onViewClicked(final View view) {
        /**
         * 6.0以上手机做权限处理
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new RxPermission(MainActivity.this).request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean == true) {
                        viewClick(view.getId());
                    }
                }
            });
        } else {
            viewClick(view.getId());
        }
    }


    /**
     * 打开相机做权限处理
     *
     * @param id
     */
    private void viewClick(final int id) {
        switch (id) {
            case R.id.act_camera:
                presenter.openCamera(false);
                break;
            case R.id.act_camera_crop:
                presenter.openCamera(true);
                break;
            case R.id.act_album_crop:
                presenter.openAlbum(true);//true裁剪、false不裁减
                break;
            case R.id.act_custom:
                startActivityForResult(AlbumActivity.getIntent(this, ivCount), TYPE_REQIEST_CODE);
                break;
        }
    }
}
