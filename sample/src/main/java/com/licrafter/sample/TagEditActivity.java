package com.licrafter.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.licrafter.sample.model.TagGroupModel;
import com.licrafter.sample.utils.DataRepo;
import com.licrafter.sample.views.TagImageView;
import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.views.ITagView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: shell
 * date 2016/12/30 下午9:08
 **/
public class TagEditActivity extends AppCompatActivity implements View.OnClickListener, TagViewGroup.OnTagGroupClickListener {

    private Button mSaveBtn, mCreateBtn;
    private TagImageView mTagImageView;

    private EditText edit01, edit02, edit03;
    private int num;
    private List<TagGroupModel> mModelList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);
        mTagImageView = (TagImageView) findViewById(R.id.tagImageView);
        edit01 = (EditText) findViewById(R.id.edit1);
        edit02 = (EditText) findViewById(R.id.edit2);
        edit03 = (EditText) findViewById(R.id.edit3);

        mSaveBtn = (Button) findViewById(R.id.saveButton);
        mCreateBtn = (Button) findViewById(R.id.createButton);
        mTagImageView.setEditMode(true);
        mTagImageView.setImageUrl("http://ci.xiaohongshu.com/0c62c1d9-8183-4410-82cf-80492b88fdad@r_1280w_1280h.jpg");
        mSaveBtn.setOnClickListener(this);
        mCreateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                setResult(RESULT_OK);
                DataRepo.tagGroupList = mModelList;
                finish();
                break;
            case R.id.tagImageView:
                break;
            case R.id.createButton:
                TagGroupModel model = createTagGroup();
                mModelList.add(model);
                mTagImageView.addTagGroup(model, this);
                break;

        }
    }

    public TagGroupModel createTagGroup() {
        TagGroupModel model = new TagGroupModel();
        String str01 = edit01.getText().toString();
        String str02 = edit02.getText().toString();
        String str03 = edit03.getText().toString();
        if (!TextUtils.isEmpty(str01)) {
            TagGroupModel.Tag tag = new TagGroupModel.Tag();
            tag.setName(str01);
            tag.setDirection(DIRECTION.RIGHT_TOP.getValue());
            model.getTags().add(tag);
        }
        if (!TextUtils.isEmpty(str02)) {
            TagGroupModel.Tag tag = new TagGroupModel.Tag();
            tag.setName(str02);
            tag.setDirection(DIRECTION.RIGHT_CENTER.getValue());
            model.getTags().add(tag);
        }
        if (!TextUtils.isEmpty(str03)) {
            TagGroupModel.Tag tag = new TagGroupModel.Tag();
            tag.setName(str03);
            tag.setDirection(DIRECTION.RIGHT_BOTTOM.getValue());
            model.getTags().add(tag);
        }
        model.setPercentX(0.5f);
        model.setPercentY(0.5f);
        return model;
    }

    @Override
    public void onCircleClick(TagViewGroup group) {

    }

    @Override
    public void onTagClick(TagViewGroup group, ITagView tag, int index) {
        tag.setDirection(DIRECTION.valueOf((num++ % 10 + 1)));
        mModelList.get(mTagImageView.getTagGroupIndex(group)).getTags().get(index).setDirection(tag.getDirection().getValue());
        group.requestLayout();
    }

    @Override
    public void onScroll(TagViewGroup group, float percentX, float percentY) {
        mModelList.get(mTagImageView.getTagGroupIndex(group)).setPercentX(percentX);
        mModelList.get(mTagImageView.getTagGroupIndex(group)).setPercentY(percentY);
    }

    @Override
    public void onLongPress(final TagViewGroup group) {
        new AlertDialog.Builder(TagEditActivity.this)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTagImageView.removeTagGroup(group);
                        mModelList.remove(mTagImageView.getTagGroupIndex(group));
                        dialog.dismiss();
                    }
                }).setTitle("删除标签组").setMessage("你确定要删除该标签组吗？")
                .create().show();
    }
}
