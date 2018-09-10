package com.ruiwenliu.imgagemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiwenliu.imgagemanager.bean.AlbumBean;
import com.ruiwenliu.imgagemanager.util.glide.GlideImgManager;
import com.ruiwenliu.rxcarouselview.CarouselView;
import com.ruiwenliu.rxcarouselview.inter.OnPageChangeListerer;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends AppCompatActivity {

    @BindView(R.id.carouselview)
    CarouselView carouselview;
    @BindView(R.id.tv_count)
    TextView tvCount;
    private List<AlbumBean> listAlbum;

    public static Intent getIntent(Context context, List<AlbumBean> list) {
        return new Intent(context, PreviewActivity.class).putExtra("list", (Serializable) list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        listAlbum= (List<AlbumBean>) getIntent().getSerializableExtra("list");
        carouselview.setLoop(1);
        tvCount.setText(String.format("%1$s%2$d","1/",listAlbum.size()));
         carouselview.setCarouselData( getData());
         carouselview.setOnPageChangeListerer(new OnPageChangeListerer() {
             @Override
             public void onPageChange(int i) {
                 tvCount.setText(String.format("%1$d%2$s%3$d",i+1,"/",listAlbum.size()));
             }
         });
    }

    private List<ImageView> getData(){
        List<ImageView> list=new ArrayList<>();
        for(AlbumBean bean:listAlbum){
            ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(R.layout.item_imagely, null);
            GlideImgManager.getInstance(this).loadImage(bean.albumUrl, imageView);
            list.add(imageView);
        }

        return list;
    }
    @OnClick(R.id.tv_confirm)
    public void onViewClicked() {

    }
}
