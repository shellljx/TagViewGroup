package com.licrafter.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.views.TagTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TagViewGroup mTagGroup;
    private TagViewGroup mTagGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTagGroup = (TagViewGroup) findViewById(R.id.tagGroup);
        mTagGroup2 = (TagViewGroup) findViewById(R.id.tagGroup2);

        TagTextView v1 = new TagTextView(this);
        v1.setText("我是右上");
        v1.setDirection(DIRECTION.RIGHT_TOP_TILT);
        mTagGroup.addTag(v1);

        TagTextView v2 = new TagTextView(this);
        v2.setText("我是右中啊啊");
        v2.setDirection(DIRECTION.RIGHT_CENTER);
        mTagGroup.addTag(v2);

        TagTextView v3 = new TagTextView(this);
        v3.setText("这里是左下斜线嘿");
        v3.setDirection(DIRECTION.LEFT_BOTTOM_TILE);
        mTagGroup.addTag(v3);

        TagTextView v4 = new TagTextView(this);
        v4.setText("右下");
        v4.setDirection(DIRECTION.RIGHT_BOTTOM);
        mTagGroup.addTag(v4);


        TagTextView v5 = new TagTextView(this);
        v5.setText("这里是左中");
        v5.setDirection(DIRECTION.LEFT_CENTER);
        mTagGroup.addTag(v5);

        TagTextView v6 = new TagTextView(this);
        v6.setText("我是左上哈哈哈哈黑哈嘿嘛咪!");
        v6.setDirection(DIRECTION.LEFT_TOP);
        mTagGroup.addTag(v6);

        TagTextView v7 = new TagTextView(this);
        v7.setText("我是右中，嗯没错");
        v7.setDirection(DIRECTION.RIGHT_CENTER);
        mTagGroup2.addTag(v7);

        v1.setOnClickListener(this);
        v2.setOnClickListener(this);
        v3.setOnClickListener(this);
        v4.setOnClickListener(this);
        v5.setOnClickListener(this);
        v6.setOnClickListener(this);
        findViewById(R.id.activity_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_main) {
            if (mTagGroup.isHiden()) {
                mTagGroup.showWithAnimation();
                mTagGroup2.showWithAnimation();
            } else {
                mTagGroup.hideWithAnimation();
                mTagGroup2.hideWithAnimation();
            }
        } else {
            Toast.makeText(this, ((TagTextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
