package com.ruiwenliu.imgagemanager.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruiwenliu.imgagemanager.R;
import com.ruiwenliu.imgagemanager.bean.AlbumBean;
import com.ruiwenliu.imgagemanager.util.glide.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiwen
 * Data:2018/9/10 0010
 * Email:1054750389@qq.com
 * Desc:
 */

public class AlbumAdapter extends BaseQuickAdapter<AlbumBean,BaseViewHolder>{
    public AlbumAdapter() {
        super(R.layout.item_albumly);
    }

    @Override
    protected void convert(BaseViewHolder helper, AlbumBean item) {
        helper.setImageResource(R.id.iv_select,item.selectState==1?R.drawable.select:R.drawable.select_no);
        GlideImgManager.getInstance(mContext).loadImage(item.albumUrl, (ImageView) helper.getView(R.id.image));
    }

    /**
     * 设置选中状态
     * @param postion
     */
    public void setSelectState(int postion,int maxNum){
        mData.get(postion).selectState=mData.get(postion).selectState==0 && getSelectNumber()<maxNum?1:0;
        notifyItemChanged(postion);
    }

    /**
     * 获取选中的数据
     * @return
     */
    public List<AlbumBean>  getSelectData(){
        List<AlbumBean> listBean=new ArrayList<>();
        for(AlbumBean bean: mData){
            if(bean.selectState==1){
                listBean.add(bean);
            }
        }
        return listBean;
    }

    /**
     * 获取选中的数量
     * @return
     */
    public int getSelectNumber(){
        int count=0;
        for(AlbumBean bean: mData){
            if(bean.selectState==1){
                count++;
            }
        }
        return count;
    }
}
