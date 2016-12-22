package com.licrafter.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.views.TagView;

public class MainActivity extends AppCompatActivity {

    private TagViewGroup mTagGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTagGroup = (TagViewGroup) findViewById(R.id.tagGroup);

        TagView view1 = new TagView(this);
        view1.setText("我是右上");
        view1.setDirection(DIRECTION.RIGHT_TOP_TILT);
        mTagGroup.addTag(view1);

        TagView v2 = new TagView(this);
        v2.setText("我是右中啊啊");
        v2.setDirection(DIRECTION.RIGHT_CENTER);
        mTagGroup.addTag(v2);

        TagView v3 = new TagView(this);
        v3.setText("这里是左下斜线嘿");
        v3.setDirection(DIRECTION.LEFT_BOTTOM_TILE);
        mTagGroup.addTag(v3);

        TagView v4 = new TagView(this);
        v4.setText("右下");
        v4.setDirection(DIRECTION.RIGHT_BOTTOM);
        mTagGroup.addTag(v4);


        TagView v5 = new TagView(this);
        v5.setText("这里是左中");
        v5.setDirection(DIRECTION.LEFT_CENTER);
        mTagGroup.addTag(v5);

        TagView v6 = new TagView(this);
        v6.setText("我是左上哈哈哈哈黑哈嘿嘛咪!");
        v6.setDirection(DIRECTION.LEFT_TOP);
        mTagGroup.addTag(v6);

        mTagGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTagGroup.isHiden()) {
                    mTagGroup.showWithAnimation();
                } else {
                    mTagGroup.hideWithAnimation();
                }
            }
        });
    }
}
