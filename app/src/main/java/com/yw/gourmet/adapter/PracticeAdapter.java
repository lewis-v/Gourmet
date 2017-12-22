package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.data.MenuPracticeData;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.MyViewHolder>{
    private Context context;
    private List<MenuPracticeData<List<String>>> list;
    private boolean isChange = true;//是否可改变,默认可改变

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_practice,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (isChange) {
            return list.size() + 1;
        }else {
            return list.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item,ll_content;
        EditText et_content;
        ImageView img_add,img_delete;
        RecyclerView recycler_practice;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout)itemView.findViewById(R.id.ll_item);
            ll_content = (LinearLayout)itemView.findViewById(R.id.ll_content);
            et_content = (EditText)itemView.findViewById(R.id.et_content);
            img_add = (ImageView)itemView.findViewById(R.id.img_add);
            img_delete = (ImageView)itemView.findViewById(R.id.img_delete);
            recycler_practice = (RecyclerView)itemView.findViewById(R.id.recycler_practice);

        }
    }
}
