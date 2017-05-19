package com.licrafter.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.licrafter.sample.utils.DataRepo;
import com.licrafter.sample.views.TagImageView;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.views.ITagView;

/**
 * Created by lijx on 2017/5/18.
 */

public class TagListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerview;
    private ImgAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        mRecyclerview = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ImgAdapter();
        mRecyclerview.setAdapter(mAdapter);
    }

    private class ImgAdapter extends RecyclerView.Adapter<ImgHolder> {

        @Override
        public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ImgHolder holder = new ImgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img, parent, false));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageView.excuteTagsAnimation();
                }
            });
            final TagViewGroup.OnTagGroupClickListener listener = new TagViewGroup.OnTagGroupClickListener() {
                @Override
                public void onCircleClick(TagViewGroup container) {
                    Toast.makeText(TagListActivity.this, "点击中心圆", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTagClick(TagViewGroup container, ITagView tag, int position) {
                    Toast.makeText(TagListActivity.this, "点击Tag->" + DataRepo.tagGroupList.get(holder
                            .getAdapterPosition() % DataRepo.tagGroupList.size()).getTags().get(position).getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongPress(final TagViewGroup group) {
                    Toast.makeText(TagListActivity.this, "点击中心圆", Toast.LENGTH_SHORT).show();
                }
            };
            holder.imageView.setTagGroupClickListener(listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(ImgHolder holder, int position) {
            holder.imageView.setImageUrl(DataRepo.imgList.get(position % 3));
            holder.imageView.setTag(DataRepo.tagGroupList.get(position % DataRepo.tagGroupList.size()));
        }

        @Override
        public int getItemCount() {
            return 30;
        }
    }

    class ImgHolder extends RecyclerView.ViewHolder {

        TagImageView imageView;

        public ImgHolder(View itemView) {
            super(itemView);
            imageView = (TagImageView) itemView.findViewById(R.id.tagImageView);
        }
    }
}
