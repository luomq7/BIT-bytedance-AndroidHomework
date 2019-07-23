package com.example.chanyue;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chanyue.bean.Feed;

import java.util.List;

public class MyfeedClassAdapter extends RecyclerView.Adapter<MyfeedClassAdapter.PictureViewHolder> {

    private List<Feed> picList;
    private Context mContext;

    public MyfeedClassAdapter(Context context, List<Feed> picList) {
        this.picList = picList;
        mContext = context;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载布局 feed_item_list.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfeed_item_list,
                parent, false);
        return new PictureViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PictureViewHolder holder, final int position) {
        if (picList != null) {
            Feed feed = picList.get(position);
            String url = feed.getImageUrl();

            // Glide图片加载
            Glide.with(mContext)
                    .load(url)
                    .into(holder.imageView);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext.getApplicationContext(), "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
                    //从个人页面点击播放视频
                    String videoUrl = picList.get(position).getVideoUrl();
                    mContext.startActivity(new Intent(mContext.getApplicationContext(),PlayVideo.class).putExtra("VideoUrl",videoUrl));
                }
            });

        }

    }
    @Override
    public int getItemCount() {
        if (picList == null || picList.size() < 1) {
            return 0;
        }
        return picList.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView imageView;


        public PictureViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            initView();
        }

        private void initView() {
            imageView = mView.findViewById(R.id.image);

        }

    }
}