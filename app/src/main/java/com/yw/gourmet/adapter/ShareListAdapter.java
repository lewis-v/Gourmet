package com.yw.gourmet.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.base.BaseViewHolder;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.ui.imgShow.ImgShowActivity;
import com.yw.gourmet.ui.personal.PersonalActivity;
import com.yw.gourmet.utils.ShareTransitionUtil;
import com.yw.gourmet.widget.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LYW on 2017/11/18.
 */

public class ShareListAdapter extends RecyclerView.Adapter<BaseViewHolder<ShareListData<List<String>>>>{
    public static final int DIARY = 0;//日记
    public static final int RAIDERS = 1;//攻略
    public static final int MENU = 2;//食谱
    public static final int SHARE_ONLY = 3;//普通分享,单张图片
    public static final int SHARE_MANY = 4;//普通分享,多张图片
    public static final int END = 5;//没有更多了

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
    public int getItemViewType(int position) {
        if (position == listData.size()){//到底了
            return END;
        }
        int type = listData.get(position).getType();
        if (type < 3) {
            return listData.get(position).getType();
        }else if(type == 3){
            if (listData.get(position).getImg() == null
                    || listData.get(position).getImg().size()<=1){//一张图片或没有图片
                return SHARE_ONLY;
            }else {//多张图片
                return SHARE_MANY;
            }
        }else {
            return -1;//不支持的类型
        }
    }

    @Override
    public BaseViewHolder<ShareListData<List<String>>> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case DIARY:
            case RAIDERS:
            case MENU:
                return new OtherMessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_other,parent,false));
            case SHARE_ONLY:
                return new CommonOnlyMessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_common_only,parent,false));
            case SHARE_MANY:
                return new CommonManyMessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_common_many,parent,false));
            case END:
                return new EndMessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_end,parent,false));
        }
        return new BaseMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_share_list,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder<ShareListData<List<String>>> holder, final int position) {
        if (position == listData.size()){
            holder.onBind(null);
        }else {
            holder.onBind(listData.get(position));
        }
//        if (position == listData.size()){
//            holder.constraint_item.setVisibility(View.GONE);
//            holder.tv_bottom.setVisibility(View.VISIBLE);
//            if (isEnd && listData.size() > 0) {
//                holder.tv_bottom.setText(R.string.is_end);
//            }else{
//                holder.tv_bottom.setText("");
//            }
//        }else {
//            holder.constraint_item.setVisibility(View.VISIBLE);
//            holder.tv_bottom.setVisibility(View.GONE);
//            holder.tv_content.setText(listData.get(position).getContent());
//            holder.tv_time.setText(listData.get(position).getPut_time());
//            holder.tv_nickname.setText(listData.get(position).getNickname());
//            GlideApp.with(context).load(listData.get(position).getImg_header()).placeholder(R.mipmap.loading)
//                    .transform(new GlideCircleTransform(context)).placeholder(R.mipmap.loading)
//                    .error(R.mipmap.load_fail).into(holder.img_header);
//            if (listData.get(position).getContent() == null || listData.get(position).getContent().length() == 0) {
//                holder.tv_content.setVisibility(View.GONE);
//            } else {
//                holder.tv_content.setVisibility(View.VISIBLE);
//            }
//            if (listData.get(position).getComment_num() > 0) {
//                holder.tv_comment.setText(String.valueOf(listData.get(position).getComment_num()));
//            } else {
//                holder.tv_comment.setText(R.string.comment);
//            }
//            if (listData.get(position).getGood_num() > 0) {
//                holder.tv_good.setText(String.valueOf(listData.get(position).getGood_num()));
//            } else {
//                holder.tv_good.setText(R.string.good);
//            }
//            if (listData.get(position).getBad_num() > 0) {
//                holder.tv_bad.setText(String.valueOf(listData.get(position).getBad_num()));
//            } else {
//                holder.tv_bad.setText(R.string.bad);
//            }
//            String goodAct = listData.get(position).getGood_act();
//            if (goodAct != null) {
//                if (goodAct.equals("0")) {//踩了
//                    holder.img_bad.setImageResource(R.drawable.bad_ic);
//                    holder.img_good.setImageResource(R.drawable.good);
//                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
//                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//                } else if (goodAct.equals("1")) {//点赞了
//                    holder.img_bad.setImageResource(R.drawable.bad);
//                    holder.img_good.setImageResource(R.drawable.good_ic);
//                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
//                } else {
//                    holder.img_bad.setImageResource(R.drawable.bad);
//                    holder.img_good.setImageResource(R.drawable.good);
//                    holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
//                    holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
//                }
//            } else {
//                holder.img_bad.setImageResource(R.drawable.bad);
//                holder.img_good.setImageResource(R.drawable.good);
//                holder.tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
//                holder.tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
//            }
//            String is_comment = listData.get(position).getIs_comment();
//            if (is_comment != null && is_comment.length() > 0) {//评论了
//                holder.img_comment.setImageResource(R.drawable.comment_ic);
//                holder.tv_comment.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//            } else {
//                holder.img_comment.setImageResource(R.drawable.comment);
//                holder.tv_comment.setTextColor(ContextCompat.getColor(context, R.color.close));
//            }
//
//            if (listData.get(position).getType() == Constant.TypeFlag.SHARE) {//普通分享
//                holder.ll_other.setVisibility(View.GONE);
//                holder.ll_content.setVisibility(View.VISIBLE);
//                if (listData.get(position).getImg() == null
//                        || listData.get(position).getImg().size() == 0) {//无图片
//                    holder.recycler_share.setVisibility(View.GONE);
//                    holder.ll_img.setVisibility(View.GONE);
//                } else if (listData.get(position).getImg().size() == 1) {//单张图片
//                    holder.ll_img.setVisibility(View.VISIBLE);
//                    holder.recycler_share.setVisibility(View.GONE);
//                    GlideApp.with(context).load(listData.get(position).getImg().get(0))
//                            .placeholder(R.mipmap.loading)
//                            .error(R.mipmap.load_fail).into(holder.img_share);
//                    holder.img_share.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            final String shareFlag = "tran"+(int)(Math.random()*100);
//                            Intent intent = new Intent(context, ImgShowActivity.class);
//                            intent.putStringArrayListExtra("img", (ArrayList<String>) listData.get(holder.getLayoutPosition()).getImg());
//                            intent.putExtra("position",0);
//                            intent.putExtra("shareFlag",shareFlag);
//                            ShareTransitionUtil.position = -1;
//                            if (android.os.Build.VERSION.SDK_INT > 20) {
//                                ((BaseActivity)context).setExitSharedElementCallback(new SharedElementCallback() {
//                                    @Override
//                                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                                        super.onMapSharedElements(names, sharedElements);
//                                    }
//                                });
//                                holder.ll_img.setTransitionName(shareFlag);
//                                context.startActivity(intent
//                                        , ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.ll_img, shareFlag).toBundle());
//                            } else {
//                                context.startActivity(intent);
//                            }
//                        }
//                    });
//                } else if (listData.get(position).getImg().size() > 1) {//多张图片
//                    final ImgAdapter adapter = new ImgAdapter(context, listData.get(position).getImg());
//                    Log.e("share",listData.get(position).getImg().toString());
//                    holder.ll_img.setVisibility(View.GONE);
//                    holder.recycler_share.setVisibility(View.VISIBLE);
//                    holder.recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
//                            , StaggeredGridLayoutManager.VERTICAL));
//                    holder.recycler_share.setItemAnimator(new DefaultItemAnimator());
//                    holder.recycler_share.setAdapter(adapter);
//                    adapter.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void OnClick(View v, int position) {
//
//                            final String shareFlag = "tran"+(int)(Math.random()*1000);
//                            Intent intent = new Intent(context, ImgShowActivity.class);
//                            intent.putStringArrayListExtra("img", (ArrayList<String>) listData.get(holder.getLayoutPosition()).getImg());
//                            intent.putExtra("position",position);
//                            intent.putExtra("shareFlag",shareFlag);
//
//                            if (android.os.Build.VERSION.SDK_INT > 20) {
//                                ((BaseActivity)context).setExitSharedElementCallback(new SharedElementCallback() {
//                                    @Override
//                                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                                        super.onMapSharedElements(names, sharedElements);
//                                        if (ShareTransitionUtil.position != -1) {
//                                            sharedElements.put(shareFlag, adapter.getSparseArray().get(ShareTransitionUtil.position));
//                                        }
//                                    }
//                                });
//                                ShareTransitionUtil.position = position;
//                                v.setTransitionName(shareFlag);
//                                context.startActivity(intent
//                                        , ActivityOptions.makeSceneTransitionAnimation((Activity) context, v, shareFlag).toBundle());
//                            } else {
//                                context.startActivity(intent);
//                            }
//                        }
//
//                        @Override
//                        public boolean OnLongClick(View v, int position) {
//                            return false;
//                        }
//                    });
//                }
//            } else {//非普通类型
//                holder.ll_other.setVisibility(View.VISIBLE);
//                holder.ll_content.setVisibility(View.GONE);
//                holder.tv_title.setText(listData.get(position).getTitle());
//                GlideApp.with(context).load(listData.get(position).getCover())
//                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
//                        .into(holder.img_cover);
//                switch (listData.get(position).getType()) {
//                    case Constant.TypeFlag.DIARY://日记
//                        holder.tv_title.setBackgroundResource(R.drawable.diary_back);
//                        break;
//                    case Constant.TypeFlag.MENU://食谱
//                        holder.tv_title.setBackgroundResource(R.drawable.menu_back);
//                        break;
//                    case Constant.TypeFlag.RAIDERS://攻略
//                        holder.tv_title.setBackgroundResource(R.drawable.raiders_back);
//                        break;
//                }
//                if (listData.get(position).getType() >= Constant.TypeFlag.OTHER){
//                    holder.tv_title.setBackgroundResource(R.color.transparent);
//                    holder.tv_title.setText("此版本不支持此类型,请更新APP");
//                }
//            }
//
//            if (listener != null) {
//                holder.constraint_item.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.OnClick(v, holder.getLayoutPosition());
//                    }
//                });
//                holder.constraint_item.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        return listener.OnLongClick(v, holder.getLayoutPosition());
//                    }
//                });
//            }
//            if (onReMarkListener != null) {
//                holder.ll_comment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onReMarkListener.OnCommentClick(v, holder.getLayoutPosition());
//                    }
//                });
//                holder.ll_good.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onReMarkListener.OnGoodClick(v, holder.getLayoutPosition());
//                    }
//                });
//                holder.ll_bad.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onReMarkListener.OnBadClick(v, holder.getLayoutPosition());
//                    }
//                });
//            }
//            if (onMoreListener != null) {
//                holder.img_more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onMoreListener.OnMoreClick(v, holder.getLayoutPosition());
//                    }
//                });
//            }
//
//            holder.img_header.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, PersonalActivity.class);
//                    intent.putExtra("id", listData.get(holder.getLayoutPosition()).getUser_id());
//                    context.startActivity(intent);
//                }
//            });
//            holder.tv_nickname.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, PersonalActivity.class);
//                    intent.putExtra("id", listData.get(holder.getLayoutPosition()).getUser_id());
//                    context.startActivity(intent);
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return listData.size()+1;
    }

    class BaseMessageViewHolder extends BaseViewHolder<ShareListData<List<String>>>{
        ImageView img_header,img_comment,img_good,img_bad,img_more;
        LinearLayout ll_comment,ll_good,ll_bad;
        TextView tv_nickname,tv_time,tv_comment,tv_good,tv_bad;
        CardView card_item;

        BaseMessageViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        public void onBind(ShareListData<List<String>> data) {
            if (onMoreListener != null){
                img_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMoreListener.OnMoreClick(v,getLayoutPosition());
                    }
                });
            }
            if (onReMarkListener != null){
                ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnCommentClick(v,getLayoutPosition());
                    }
                });
                ll_good.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnGoodClick(v,getLayoutPosition());
                    }
                });
                ll_bad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReMarkListener.OnBadClick(v,getLayoutPosition());
                    }
                });
                //设置点赞信息
                if (data.getComment_num() > 0) {
                    tv_comment.setText(String.valueOf(data.getComment_num()));
                } else {
                    tv_comment.setText(R.string.comment);
                }
                if (data.getGood_num() > 0) {
                    tv_good.setText(String.valueOf(data.getGood_num()));
                } else {
                    tv_good.setText(R.string.good);
                }
                if (data.getBad_num() > 0) {
                    tv_bad.setText(String.valueOf(data.getBad_num()));
                } else {
                    tv_bad.setText(R.string.bad);
                }
                String goodAct = data.getGood_act();
                if (goodAct != null) {
                    if (goodAct.equals("0")) {//踩了
                        img_bad.setImageResource(R.drawable.bad_ic);
                        img_good.setImageResource(R.drawable.good);
                        tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                        tv_bad.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    } else if (goodAct.equals("1")) {//点赞了
                        img_bad.setImageResource(R.drawable.bad);
                        img_good.setImageResource(R.drawable.good_ic);
                        tv_good.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
                    } else {
                        img_bad.setImageResource(R.drawable.bad);
                        img_good.setImageResource(R.drawable.good);
                        tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                        tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
                    }
                } else {
                    img_bad.setImageResource(R.drawable.bad);
                    img_good.setImageResource(R.drawable.good);
                    tv_good.setTextColor(ContextCompat.getColor(context, R.color.close));
                    tv_bad.setTextColor(ContextCompat.getColor(context, R.color.close));
                }
                String is_comment = data.getIs_comment();
                if (is_comment != null && is_comment.length() > 0) {//评论了
                    img_comment.setImageResource(R.drawable.comment_ic);
                    tv_comment.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                } else {
                    img_comment.setImageResource(R.drawable.comment);
                    tv_comment.setTextColor(ContextCompat.getColor(context, R.color.close));
                }

                //设置分享者信息
                tv_time.setText(data.getPut_time());
                tv_nickname.setText(data.getNickname());
                GlideApp.with(context).load(data.getImg_header()).placeholder(R.mipmap.loading)
                        .transform(new GlideCircleTransform(context))
                        .error(R.mipmap.load_fail).into(img_header);
                img_header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("id", listData.get(getLayoutPosition()).getUser_id());
                        context.startActivity(intent);
                    }
                });
                tv_nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("id", listData.get(getLayoutPosition()).getUser_id());
                        context.startActivity(intent);
                    }
                });

                //查看详情
                if (listener != null) {
                    card_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.OnClick(v, getLayoutPosition());
                        }
                    });
                    card_item.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return listener.OnLongClick(v, getLayoutPosition());
                        }
                    });
                }
            }

        }

        public void init(View itemView){
            card_item = itemView.findViewById(R.id.card_item);
            //分享者信息
            img_header = itemView.findViewById(R.id.img_header);
            img_more = itemView.findViewById(R.id.img_more);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_time = itemView.findViewById(R.id.tv_time);
            //评论情况
            img_comment = itemView.findViewById(R.id.img_comment);
            img_good = itemView.findViewById(R.id.img_good);
            img_bad = itemView.findViewById(R.id.img_bad);
            ll_comment = itemView.findViewById(R.id.ll_comment);
            ll_good =  itemView.findViewById(R.id.ll_good);
            ll_bad =  itemView.findViewById(R.id.ll_bad);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_good =  itemView.findViewById(R.id.tv_good);
            tv_bad = itemView.findViewById(R.id.tv_bad);
        }
    }

    class CommonOnlyMessageViewHolder extends BaseMessageViewHolder{
        TextView tv_content;
        ImageView img_share;
        LinearLayout ll_img;

        CommonOnlyMessageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(ShareListData<List<String>> data) {
            super.onBind(data);
            if (data.getContent() == null || data.getContent().length() == 0){
                tv_content.setVisibility(View.GONE);
            }else {
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(data.getContent());
            }
            if (data.getImg() == null || data.getImg().size() == 0){
                ll_img.setVisibility(View.GONE);
            }else {
                ll_img.setVisibility(View.VISIBLE);
                GlideApp.with(context).load(data.getImg().get(0))
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.load_fail).into(img_share);
                img_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String shareFlag = "tran"+(int)(Math.random()*100);
                        Intent intent = new Intent(context, ImgShowActivity.class);
                        intent.putStringArrayListExtra("img", (ArrayList<String>) listData.get(getLayoutPosition()).getImg());
                        intent.putExtra("position",0);
                        intent.putExtra("shareFlag",shareFlag);
                        ShareTransitionUtil.position = -1;
                        if (android.os.Build.VERSION.SDK_INT > 20) {
                            img_share.setTransitionName(shareFlag);
                            context.startActivity(intent
                                    , ActivityOptions.makeSceneTransitionAnimation((Activity) context, img_share, shareFlag).toBundle());
                        } else {
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            img_share = itemView.findViewById(R.id.img_share);
            ll_img = itemView.findViewById(R.id.ll_img);
        }
    }

    class CommonManyMessageViewHolder extends BaseMessageViewHolder{
        TextView tv_content;
        RecyclerView recycler_share;

        CommonManyMessageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(ShareListData<List<String>> data) {
            super.onBind(data);
            if (data.getContent() == null || data.getContent().length() == 0){
                tv_content.setVisibility(View.GONE);
            }else {
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(data.getContent());
            }
            final ImgAdapter adapter = new ImgAdapter(context, data.getImg());
            recycler_share.setVisibility(View.VISIBLE);
            recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
                    , StaggeredGridLayoutManager.VERTICAL));
            recycler_share.setItemAnimator(new DefaultItemAnimator());
            recycler_share.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnClick(View v, int position) {
                    final String shareFlag = "tran"+(int)(Math.random()*1000);
                    Intent intent = new Intent(context, ImgShowActivity.class);
                    intent.putStringArrayListExtra("img", (ArrayList<String>) listData.get(getLayoutPosition()).getImg());
                    intent.putExtra("position",position);
                    intent.putExtra("shareFlag",shareFlag);

                    if (android.os.Build.VERSION.SDK_INT > 20) {
                        ((BaseActivity)context).setExitSharedElementCallback(new SharedElementCallback() {
                            @Override
                            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                                super.onMapSharedElements(names, sharedElements);
                                if (ShareTransitionUtil.position != -1) {
                                    sharedElements.put(shareFlag, adapter.getSparseArray().get(ShareTransitionUtil.position));
                                }
                            }
                        });
                        ShareTransitionUtil.position = position;
                        v.setTransitionName(shareFlag);
                        context.startActivity(intent
                                , ActivityOptions.makeSceneTransitionAnimation((Activity) context, v, shareFlag).toBundle());
                    } else {
                        context.startActivity(intent);
                    }
                }

                @Override
                public boolean OnLongClick(View v, int position) {
                    return false;
                }
            });
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            recycler_share = itemView.findViewById(R.id.recycler_share);
        }
    }

    class OtherMessageViewHolder extends BaseMessageViewHolder{
        ImageView img_cover;
        TextView tv_title;

        OtherMessageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(ShareListData<List<String>> data) {
            super.onBind(data);
            tv_title.setText(data.getTitle());
            GlideApp.with(context).load(data.getCover())
                    .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                    .into(img_cover);
            switch (data.getType()) {
                case Constant.TypeFlag.DIARY://日记
                    tv_title.setBackgroundResource(R.drawable.diary_back);
                    break;
                case Constant.TypeFlag.MENU://食谱
                    tv_title.setBackgroundResource(R.drawable.menu_back);
                    break;
                case Constant.TypeFlag.RAIDERS://攻略
                    tv_title.setBackgroundResource(R.drawable.raiders_back);
                    break;
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            img_cover = itemView.findViewById(R.id.img_cover);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    class EndMessageViewHolder extends BaseViewHolder<ShareListData<List<String>>>{
        TextView tv_more;
        EndMessageViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        @Override
        public void onBind(ShareListData<List<String>> data) {
            if (listData.size() <= 1){//列表无数据
                tv_more.setText("");
            }else {
                if (isEnd()) {//到末尾
                    tv_more.setText(R.string.is_end);
                } else {//还能加载更多
                    tv_more.setText("");
                }
            }
        }

        public void init(View itemView) {
            tv_more = itemView.findViewById(R.id.tv_more);
        }
    }
}
