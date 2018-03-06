package com.yw.gourmet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.yw.gourmet.ui.personal.PersonalActivity;
import com.yw.gourmet.widget.GlideCircleTransform;

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
    private boolean isEnd = false;//是否到底了

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


    public ShareListAdapter setEnd(boolean end) {
        isEnd = end;
        return this;
    }

    /**
     * 通过类型及id获取位置,不存在时返回-1
     * @param type
     * @param id
     * @return
     */
    public int getPosition(int type,String id){
        for (int len = listData.size(),num = 0;num < len;num++){
            if (type == listData.get(num).getType() && id.equals(listData.get(num).getId())){
                return num;
            }
        }
        return -1;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public ShareListAdapter(Context context, List<ShareListData<List<String>>> listData, FragmentManager fragmentManager){
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
        if (position == listData.size()){
            holder.constraint_item.setVisibility(View.GONE);
            holder.tv_bottom.setVisibility(View.VISIBLE);
            if (isEnd && listData.size() > 0) {
                holder.tv_bottom.setText(R.string.is_end);
            }else{
                holder.tv_bottom.setText("");
            }
        }else {
            holder.constraint_item.setVisibility(View.VISIBLE);
            holder.tv_bottom.setVisibility(View.GONE);
            holder.tv_content.setText(listData.get(position).getContent());
            holder.tv_time.setText(listData.get(position).getPut_time());
            holder.tv_nickname.setText(listData.get(position).getNickname());
            GlideApp.with(context).load(listData.get(position).getImg_header()).placeholder(R.mipmap.loading)
                    .transform(new GlideCircleTransform(context)).placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_fail).into(holder.img_header);
            if (listData.get(position).getContent() == null || listData.get(position).getContent().length() == 0) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
            }
            if (listData.get(position).getComment_num() > 0) {
                holder.tv_comment.setText(listData.get(position).getComment_num() + "");
            } else {
                holder.tv_comment.setText(R.string.comment);
            }
            if (listData.get(position).getGood_num() > 0) {
                holder.tv_good.setText(listData.get(position).getGood_num() + "");
            } else {
                holder.tv_good.setText(R.string.good);
            }
            if (listData.get(position).getBad_num() > 0) {
                holder.tv_bad.setText(listData.get(position).getBad_num() + "");
            } else {
                holder.tv_bad.setText(R.string.bad);
            }
            String goodAct = listData.get(position).getGood_act();
            if (goodAct != null) {
                if (goodAct.equals("0")) {//踩了
                    holder.img_bad.setImageResource(R.drawable.bad_ic);
                    holder.img_good.setImageResource(R.drawable.good);
                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                } else if (goodAct.equals("1")) {//点赞了
                    holder.img_bad.setImageResource(R.drawable.bad);
                    holder.img_good.setImageResource(R.drawable.good_ic);
                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
                } else {
                    holder.img_bad.setImageResource(R.drawable.bad);
                    holder.img_good.setImageResource(R.drawable.good);
                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
                }
            } else {
                holder.img_bad.setImageResource(R.drawable.bad);
                holder.img_good.setImageResource(R.drawable.good);
                holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
            }
            String is_comment = listData.get(position).getIs_comment();
            if (is_comment != null && is_comment.length() > 0) {//评论了
                holder.img_comment.setImageResource(R.drawable.comment_ic);
                holder.tv_comment.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                holder.img_comment.setImageResource(R.drawable.comment);
                holder.tv_comment.setTextColor(ContextCompat.getColor(context, R.color.close));
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
                    GlideApp.with(context).load(listData.get(position).getImg().get(0))
                            .placeholder(R.mipmap.loading)
                            .error(R.mipmap.load_fail).into(holder.img_share);
                    holder.img_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MyDialogPhotoShowFragment().setImgString(listData.get(holder.getLayoutPosition())
                                    .getImg()).show(fragmentManager, "imgShow");
                        }
                    });
                } else if (listData.get(position).getImg().size() > 1) {//多张图片
                    ImgAdapter adapter = new ImgAdapter(context, listData.get(position).getImg());
                    Log.e("share",listData.get(position).getImg().toString());
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
            } else {//非普通类型
                holder.ll_other.setVisibility(View.VISIBLE);
                holder.ll_content.setVisibility(View.GONE);
                holder.tv_title.setText(listData.get(position).getTitle());
                GlideApp.with(context).load(listData.get(position).getCover())
                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                        .into(holder.img_cover);
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.DIARY://日记
                        holder.tv_title.setBackgroundResource(R.drawable.diary_back);
                        break;
                    case Constant.TypeFlag.MENU://食谱
                        holder.tv_title.setBackgroundResource(R.drawable.menu_back);
                        break;
                    case Constant.TypeFlag.RAIDERS://攻略
                        holder.tv_title.setBackgroundResource(R.drawable.raiders_back);
                        break;
                }
                if (listData.get(position).getType() >= Constant.TypeFlag.OTHER){
                    holder.tv_title.setBackgroundResource(R.color.transparent);
                    holder.tv_title.setText("此版本不支持此类型,请更新APP");
                }
            }

            if (listener != null) {
                holder.constraint_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.OnClick(v, holder.getLayoutPosition());
                    }
                });
                holder.constraint_item.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return listener.OnLongClick(v, holder.getLayoutPosition());
                    }
                });
            }
            if (onReMarkListener != null) {
                holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnCommentClick(v, holder.getLayoutPosition());
                    }
                });
                holder.ll_good.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnGoodClick(v, holder.getLayoutPosition());
                    }
                });
                holder.ll_bad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnBadClick(v, holder.getLayoutPosition());
                    }
                });
            }
            if (onMoreListener != null) {
                holder.img_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMoreListener.OnMoreClick(v, holder.getLayoutPosition());
                    }
                });
            }

            holder.img_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("id", listData.get(holder.getLayoutPosition()).getUser_id());
                    context.startActivity(intent);
                }
            });
            holder.tv_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("id", listData.get(holder.getLayoutPosition()).getUser_id());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size()+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraint_item;
        ImageView img_header,img_share,img_more,img_cover,img_comment,img_good,img_bad;
        TextView tv_nickname,tv_time,tv_content,tv_comment,tv_good,tv_bad,tv_title,tv_bottom;
        LinearLayout ll_img,ll_comment,ll_good,ll_bad,ll_content,ll_other;
        RecyclerView recycler_share;
        MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = (ConstraintLayout)itemView.findViewById(R.id.constraint_item);
            img_header = (ImageView)itemView.findViewById(R.id.img_header);
            img_share = (ImageView)itemView.findViewById(R.id.img_share);
            img_more = (ImageView)itemView.findViewById(R.id.img_more);
            img_cover = (ImageView)itemView.findViewById(R.id.img_cover);
            img_comment = itemView.findViewById(R.id.img_comment);
            img_good = itemView.findViewById(R.id.img_good);
            img_bad = itemView.findViewById(R.id.img_bad);
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
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
        }
    }
}
