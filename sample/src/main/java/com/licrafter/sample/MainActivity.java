package com.licrafter.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.licrafter.sample.utils.DataRepo;
import com.licrafter.sample.views.TagImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CREATE_TAG = 0x001;

    private TagImageView mTagImageView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataRepo.initData();
        mTagImageView = (TagImageView) findViewById(R.id.tagImageView);
        mButton = (Button) findViewById(R.id.transButton);
        mTagImageView.setImageUrl("http://ci.xiaohongshu.com/0c62c1d9-8183-4410-82cf-80492b88fdad@r_1280w_1280h.jpg");
        mTagImageView.setTagList(DataRepo.tagGroupList);
        mTagImageView.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tagImageView:
                mTagImageView.excuteTagsAnimation();
                break;
            case R.id.transButton:
                startActivityForResult(new Intent(MainActivity.this, TagEditActivity.class), CREATE_TAG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CREATE_TAG) {
            mTagImageView.setTagList(DataRepo.tagGroupList);
        }
    }
}
