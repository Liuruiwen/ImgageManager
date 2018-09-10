package com.ruiwenliu.imgagemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ruiwenliu.imgagemanager.adapter.AlbumAdapter;
import com.ruiwenliu.imgagemanager.bean.AlbumBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_count)
    TextView tvCount;
    private AlbumAdapter mAdapter;
    private int ivCount;

    public static Intent getIntent(Context context, int count) {
        return new Intent(context, AlbumActivity.class).putExtra("count", count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        ivCount = getIntent().getIntExtra("count", 0);
        mAdapter = new AlbumAdapter();
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setNewData(getAlbumData());
        if(ivCount>getAlbumData().size()){
            ivCount=getAlbumData().size();
        }
        tvCount.setText(String.format("%1$s%2$d","0/",ivCount));
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.setSelectState(position,ivCount);
                tvCount.setText(String.format("%1$d%2$s%3$d",mAdapter.getSelectNumber(),"/",ivCount));

            }
        });

    }

    private List<AlbumBean> getAlbumData() {
        List<AlbumBean> listAlbum = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            AlbumBean bean = new AlbumBean();
            //获取图片的地址
//            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            //获取图片的名称
//            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//            //获取图片的生成日期
//            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            //获取图片的详细信息
//            String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
            bean.albumUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            listAlbum.add(bean);
        }

        return listAlbum;
    }

    @OnClick({R.id.iv_back, R.id.tv_preview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_preview:
                if(mAdapter!=null && mAdapter.getSelectNumber()>0){
                    startActivity(PreviewActivity.getIntent(this,mAdapter.getSelectData()));
                }
                break;
        }
    }
}
