package com.licrafter.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.utils.AnimatorUtils;
import com.licrafter.tagview.views.TagTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TagViewGroup mTagGroup;
    private TagViewGroup mTagGroup2;
    private TagViewGroup mTagGroup3;

    private TagTextView v1, v2, v3, v4, v5, v6, v7, v8, v9, v0, v10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTagGroup = (TagViewGroup) findViewById(R.id.tagGroup);
        mTagGroup2 = (TagViewGroup) findViewById(R.id.tagGroup2);
        mTagGroup3 = (TagViewGroup) findViewById(R.id.tagGroup3);
        initTagView();

        mTagGroup.setShowAnimator(AnimatorUtils.getTagShowAnimator(mTagGroup))
                .setHideAnimator(AnimatorUtils.getTagHideAnimator(mTagGroup))
                .addTag(v1).addTag(v2).addTag(v3).addTag(v4).addTag(v5).addTag(v6);
        mTagGroup.setPercent(0.5f, 0.4f);

        mTagGroup2.setShowAnimator(AnimatorUtils.getTagShowAnimator(mTagGroup2))
                .setHideAnimator(AnimatorUtils.getTagHideAnimator(mTagGroup2))
                .addTag(v7);
        mTagGroup2.setPercent(0.5f, 0.2f);

        mTagGroup3.setShowAnimator(AnimatorUtils.getTagShowAnimator(mTagGroup3))
                .setHideAnimator(AnimatorUtils.getTagHideAnimator(mTagGroup3))
                .addTag(v8).addTag(v9).addTag(v0).addTag(v10);
        mTagGroup3.setPercent(0.5f, 0.6f);

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
                mTagGroup3.showWithAnimation();
            } else {
                mTagGroup.hideWithAnimation();
                mTagGroup2.hideWithAnimation();
                mTagGroup3.hideWithAnimation();
            }
        } else {
            Toast.makeText(this, ((TagTextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initTagView() {
        v1 = new TagTextView(this);
        v1.setText("我是右上");
        v1.setDirection(DIRECTION.RIGHT_TOP);

        v2 = new TagTextView(this);
        v2.setText("我是右中啊啊");
        v2.setDirection(DIRECTION.RIGHT_CENTER);

        v3 = new TagTextView(this);
        v3.setText("这里是左下斜线嘿");
        v3.setDirection(DIRECTION.LEFT_BOTTOM);

        v4 = new TagTextView(this);
        v4.setText("右下");
        v4.setDirection(DIRECTION.RIGHT_BOTTOM);


        v5 = new TagTextView(this);
        v5.setText("这里是左中");
        v5.setDirection(DIRECTION.LEFT_CENTER);

        v6 = new TagTextView(this);
        v6.setText("我是左上哈哈哈哈黑哈嘿嘛咪!");
        v6.setDirection(DIRECTION.LEFT_TOP);

        v7 = new TagTextView(this);
        v7.setText("我是右中，嗯没错");
        v7.setDirection(DIRECTION.RIGHT_CENTER);

        v8 = new TagTextView(this);
        v8.setText("三年血赚！");
        v8.setDirection(DIRECTION.RIGHT_TOP_TILT);

        v9 = new TagTextView(this);
        v9.setText("枪毙不亏。");
        v9.setDirection(DIRECTION.RIGHT_BOTTOM_TILT);

        v0 = new TagTextView(this);
        v0.setText("萝莉控");
        v0.setDirection(DIRECTION.LEFT_TOP_TILT);

        v10 = new TagTextView(this);
        v10.setText("蓝白条胖次最好了，嗯");
        v10.setDirection(DIRECTION.LEFT_BOTTOM_TILT);
    }
}
