package com.example.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.example.home.model.Taskword_ph19997;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {
   private List<Taskword_ph19997> dataList;
    private Context context;

    public Adapter(List<Taskword_ph19997> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item, parent,false);
        return new CustomViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvtitle.setText(dataList.get(position).getTitle_ph19997());
        holder.tvContent.setText(dataList.get(position).getContent_ph19997());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(dataList.get(position));
                }
            }
        });

        Glide.with(context)
                .load(dataList.get(position).getImage_ph19997())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.error)
                .into(holder.imgView);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public View mtView;
        public ImageView imgView;
        public TextView tvtitle,tvContent;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mtView = itemView;
            imgView = mtView.findViewById(R.id.imgView);
            tvtitle = mtView.findViewById(R.id.tvtitle);
            tvContent = mtView.findViewById(R.id.tvContent);
        }
    }
    private OnItemClickListener itemClickListener;

    public Adapter(List<Taskword_ph19997> dataList, Context context, OnItemClickListener listener) {
        this.dataList = dataList;
        this.context = context;
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Taskword_ph19997 task);
    }

}
