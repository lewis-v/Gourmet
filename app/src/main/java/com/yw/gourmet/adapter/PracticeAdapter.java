package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.utils.ToastUtils;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.MyViewHolder>{
    private Context context;
    private List<MenuPracticeData<List<String>>> list;
    private FragmentManager fragmentManager;
    private boolean isChange = true;//是否可改变,默认可改变
    private OnAddListener onAddListener;
    private OnDeleteListener onDeleteListener;
    private OnItemClickListener onItemClickListener;
    private int imgMaxNum = 5;//每个步骤的图片最大数量,默认5

    public PracticeAdapter(Context context, List<MenuPracticeData<List<String>>> list, FragmentManager fragmentManager) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
    }

    public PracticeAdapter(Context context, List<MenuPracticeData<List<String>>> list, FragmentManager fragmentManager, boolean isChange) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.isChange = isChange;
    }

    public PracticeAdapter(Context context, List<MenuPracticeData<List<String>>> list, FragmentManager fragmentManager, int imgMaxNum) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.imgMaxNum = imgMaxNum;
    }

    public PracticeAdapter(Context context, List<MenuPracticeData<List<String>>> list, FragmentManager fragmentManager, boolean isChange, int imgMaxNum) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.isChange = isChange;
        this.imgMaxNum = imgMaxNum;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_practice,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (isChange ){
            if (position >= list.size()) {
                holder.img_delete.setVisibility(View.GONE);
                holder.ll_content.setVisibility(View.GONE);
                holder.img_add.setVisibility(View.VISIBLE);
                if (onAddListener != null) {
                    holder.ll_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAddListener.OnAdd(v, holder.getLayoutPosition());
                        }
                    });
                }
            }else {
                holder.ll_content.setVisibility(View.VISIBLE);
                holder.img_delete.setVisibility(View.VISIBLE);
                holder.img_add.setVisibility(View.GONE);
                holder.tv_num.setText("步骤"+(position + 1));
                holder.et_content.setText(list.get(position).getContent());
                holder.et_content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        list.get(holder.getLayoutPosition()).setContent(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                holder.recycler_practice.setItemAnimator(new DefaultItemAnimator());
                holder.recycler_practice.setLayoutManager(
                        new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                final ImgAddAdapter adapter = new ImgAddAdapter(list.get(position).getImg_practiceData(),context);
                holder.recycler_practice.setAdapter(adapter);
                adapter.setOnAddListener(new OnAddListener() {
                    @Override
                    public void OnAdd(View view, int position) {
                        if (imgMaxNum > list.get(holder.getLayoutPosition()).getImg_practiceData().size()) {
                            new MyDialogPhotoChooseFragment().setCrop(true)
                                    .setOnCropListener(new MyDialogPhotoChooseFragment.OnCropListener() {
                                        @Override
                                        public void OnCrop(String path, String tag) {
                                            adapter.addImg(path);
                                        }
                                    }).show(fragmentManager,"crop");
                        }else {
                            ToastUtils.showLongToast("最多添加"+imgMaxNum+"张图片");
                        }
                    }
                });
                adapter.setOnDeleteListener(new OnDeleteListener() {
                    @Override
                    public void OnDelete(View v, int position) {

                    }
                });
                if (onDeleteListener != null){
                    holder.img_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDeleteListener.OnDelete(v,holder.getLayoutPosition());
                            list.remove(holder.getLayoutPosition());
                            notifyItemRemoved(holder.getLayoutPosition());
                            notifyItemRangeChanged(holder.getLayoutPosition(),list.size() - holder.getLayoutPosition());
                            Log.i("---list---",list.toString());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }
        }else {
            holder.img_delete.setVisibility(View.GONE);
            holder.ll_content.setVisibility(View.VISIBLE);
            holder.img_add.setVisibility(View.GONE);
            holder.tv_num.setText("步骤"+(position + 1));
            holder.et_content.setText(list.get(position).getContent());
            holder.et_content.setEnabled(false);
            holder.et_content.setFocusable(false);
            holder.recycler_practice.setLayoutManager(
                    new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            ImgAddAdapter adapter = new ImgAddAdapter(list.get(position).getImg_practiceData(),context,imgMaxNum,false);
            holder.recycler_practice.setAdapter(adapter);
            if (onItemClickListener != null) {
                holder.ll_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.OnClick(v,holder.getLayoutPosition());
                    }
                });
                holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onItemClickListener.OnLongClick(v,holder.getLayoutPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isChange) {
            return list.size() + 1;
        }else {
            return list.size();
        }
    }

    public PracticeAdapter setChange(boolean change) {
        isChange = change;
        return this;
    }

    public PracticeAdapter setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
        return this;
    }

    public PracticeAdapter setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public PracticeAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public PracticeAdapter setImgMaxNum(int imgMaxNum) {
        this.imgMaxNum = imgMaxNum;
        return this;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item,ll_content;
        EditText et_content;
        ImageView img_add,img_delete;
        RecyclerView recycler_practice;
        TextView tv_num;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_num = itemView.findViewById(R.id.tv_num);
            ll_item = itemView.findViewById(R.id.ll_item);
            ll_content = itemView.findViewById(R.id.ll_content);
            et_content = itemView.findViewById(R.id.et_content);
            img_add = itemView.findViewById(R.id.img_add);
            img_delete = itemView.findViewById(R.id.img_delete);
            recycler_practice = itemView.findViewById(R.id.recycler_practice);

        }
    }
}
