package com.yw.gourmet.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogPhotoShowFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;

import java.util.List;

/**
 * Created by LYW on 2017/11/18.
 */

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.MyViewHolder>{
    private Context context;
    private List<ShareListData<List<String>>> listData;
    private OnItemClickListener listener;
    private OnReMarkListener onReMarkListener;
    private OnMoreListener onMoreListener;
    private FragmentManager fragmentManager;

    public ShareListAdapter setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public ShareListAdapter setOnReMarkListener(OnReMarkListener onReMarkListener) {
        this.onReMarkListener = onReMarkListener;
        return this;
    }

    public ShareListAdapter setOnMoreListener(OnMoreListener onMoreListener) {
        this.onMoreListener = onMoreListener;
        return this;
    }

    public ShareListAdapter(Context context, List<ShareListData<List<String>>> listData,FragmentManager fragmentManager){
        this.context = context;
        this.listData = listData;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_share_list,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_content.setText(listData.get(position).getContent());
        holder.tv_time.setText(listData.get(position).getPut_time());
        holder.tv_nickname.setText(listData.get(position).getNickname());
        GlideApp.with(context).load(listData.get(position).getImg_header())
                .error(R.mipmap.load_fail).into(holder.img_header);
        if (listData.get(position).getContent() == null || listData.get(position).getContent().length() == 0){
            holder.tv_content.setVisibility(View.GONE);
        }else {
            holder.tv_content.setVisibility(View.VISIBLE);
        }
        if (listData.get(position).getComment_num() > 0 ){
            holder.tv_comment.setText(listData.get(position).getComment_num()+"");
        }else {
            holder.tv_comment.setText(R.string.comment);
        }
        if (listData.get(position).getGood_num() > 0){
            holder.tv_good.setText(listData.get(position).getGood_num()+"");
        }else {
            holder.tv_good.setText(R.string.good);
        }
        if (listData.get(position).getBad_num() > 0){
            holder.tv_bad.setText(listData.get(position).getBad_num()+"");
        }else {
            holder.tv_bad.setText(R.string.bad);
        }
        if (listData.get(position).getType() == Constant.TypeFlag.SHARE) {//普通分享
            holder.ll_other.setVisibility(View.GONE);
            holder.ll_content.setVisibility(View.VISIBLE);
            if (listData.get(position).getImg() == null
                    || listData.get(position).getImg().size() == 0) {//无图片
                holder.recycler_share.setVisibility(View.GONE);
                holder.ll_img.setVisibility(View.GONE);
            } else if (listData.get(position).getImg().size() == 1) {//单张图片
                holder.ll_img.setVisibility(View.VISIBLE);
                holder.recycler_share.setVisibility(View.GONE);
                GlideApp.with(context).load(listData.get(position).getImg().get(0)).error(R.mipmap.load_fail).into(holder.img_share);
                holder.img_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MyDialogPhotoShowFragment().setImgString(listData.get(holder.getLayoutPosition())
                                .getImg()).show(fragmentManager, "imgShow");
                    }
                });
            } else if (listData.size() > 1) {//多张图片
                ImgAdapter adapter = new ImgAdapter(context, listData.get(position).getImg());
                holder.ll_img.setVisibility(View.GONE);
                holder.recycler_share.setVisibility(View.VISIBLE);
                holder.recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
                        , StaggeredGridLayoutManager.VERTICAL));
                holder.recycler_share.setItemAnimator(new DefaultItemAnimator());
                holder.recycler_share.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void OnClick(View v, int position) {
                        new MyDialogPhotoShowFragment().setImgString(listData.get(holder.getLayoutPosition()).getImg())
                                .setPosition(position).show(fragmentManager, "imgShow");
                    }

                    @Override
                    public boolean OnLongClick(View v, int position) {
                        return false;
                    }
                });
            }
        }else {//非普通类型
            holder.ll_other.setVisibility(View.VISIBLE);
            holder.ll_content.setVisibility(View.GONE);
            holder.tv_title.setText(listData.get(position).getTitle());
            GlideApp.with(context).load(listData.get(position).getCover()).error(R.mipmap.load_fail)
                    .into(holder.img_cover);
        }

        if (listener != null){
            holder.constraint_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(v,holder.getLayoutPosition());
                }
            });
            holder.constraint_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener.OnLongClick(v,holder.getLayoutPosition());
                }
            });
        }
        if (onReMarkListener != null){
            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReMarkListener.OnCommentClick(v,holder.getLayoutPosition());
                }
            });
            holder.ll_good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReMarkListener.OnGoodClick(v,holder.getLayoutPosition());
                }
            });
            holder.ll_bad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReMarkListener.OnBadClick(v,holder.getLayoutPosition());
                }
            });
        }
        if (onMoreListener != null){
            holder.img_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoreListener.OnMoreClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraint_item;
        ImageView img_header,img_share,img_more,img_cover;
        TextView tv_nickname,tv_time,tv_content,tv_comment,tv_good,tv_bad,tv_title;
        LinearLayout ll_img,ll_comment,ll_good,ll_bad,ll_content,ll_other;
        RecyclerView recycler_share;
        MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = (ConstraintLayout)itemView.findViewById(R.id.constraint_item);
            img_header = (ImageView)itemView.findViewById(R.id.img_header);
            img_share = (ImageView)itemView.findViewById(R.id.img_share);
            img_more = (ImageView)itemView.findViewById(R.id.img_more);
            img_cover = (ImageView)itemView.findViewById(R.id.img_cover);
            recycler_share = (RecyclerView)itemView.findViewById(R.id.recycler_share);
            ll_content = (LinearLayout)itemView.findViewById(R.id.ll_content);
            ll_other = (LinearLayout)itemView.findViewById(R.id.ll_other);
            ll_img = (LinearLayout)itemView.findViewById(R.id.ll_img);
            ll_comment = (LinearLayout)itemView.findViewById(R.id.ll_comment);
            ll_good = (LinearLayout) itemView.findViewById(R.id.ll_good);
            ll_bad = (LinearLayout) itemView.findViewById(R.id.ll_bad);
            tv_nickname = (TextView)itemView.findViewById(R.id.tv_nickname);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_comment = (TextView)itemView.findViewById(R.id.tv_comment);
            tv_good = (TextView) itemView.findViewById(R.id.tv_good);
            tv_bad = (TextView)itemView.findViewById(R.id.tv_bad);
            tv_title = (TextView)itemView.findViewById(R.id.tv_title);
        }
    }
}
