package com.licrafter.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.licrafter.sample.utils.DataRepo;
import com.licrafter.sample.views.TagImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TagImageView mTagImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataRepo.initData();
        mTagImageView = (TagImageView) findViewById(R.id.tagImageView);
        mTagImageView.setImageUrl("http://ci.xiaohongshu.com/0c62c1d9-8183-4410-82cf-80492b88fdad@r_1280w_1280h.jpg");
        mTagImageView.setTagList(DataRepo.tagGroupList);
        mTagImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tagImageView) {
            mTagImageView.excuteTagsAnimation();
        }
    }
}
