package com.licrafter.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.licrafter.sample.model.TagGroupModel;
import com.licrafter.sample.utils.DataRepo;
import com.licrafter.sample.views.TagEditDialog;
import com.licrafter.sample.views.TagImageView;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.views.ITagView;


/**
 * author: shell
 * date 2016/12/30 下午9:08
 **/
public class TagEditActivity extends AppCompatActivity implements View.OnClickListener
        , TagEditDialog.OnTagEditDialogClickListener {

    private Button mSaveBtn, mCreateBtn;
    private TagImageView mTagImageView;
    private TagEditDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);
        mTagImageView = (TagImageView) findViewById(R.id.tagImageView);
        mDialog = new TagEditDialog(this, this);
        mCreateBtn = (Button) findViewById(R.id.createBtn);

        mSaveBtn = (Button) findViewById(R.id.saveButton);
        mTagImageView.setEditMode(true);
        mTagImageView.setTagGroupClickListener(mTagGroupClickListener);
        mTagImageView.setTagGroupScrollListener(mTagGroupDragListener);
        mTagImageView.setImageUrl("http://ci.xiaohongshu.com/0c62c1d9-8183-4410-82cf-80492b88fdad@r_1280w_1280h.jpg");
        mSaveBtn.setOnClickListener(this);
        mCreateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                setResult(RESULT_OK);
                DataRepo.tagGroupList = mTagImageView.getTagGroupModels();
                finish();
                break;
            case R.id.tagImageView:
                break;
            case R.id.createBtn:
                mDialog.show();
                break;
        }
    }

    private TagViewGroup.OnTagGroupClickListener mTagGroupClickListener = new TagViewGroup.OnTagGroupClickListener() {
        @Override
        public void onCircleClick(TagViewGroup container) {
            Toast.makeText(TagEditActivity.this, "点击中心圆", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTagClick(TagViewGroup container, ITagView tag, int position) {
            mTagImageView.onTagClicked(container, tag, position);
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
                            dialog.dismiss();
                        }
                    }).setTitle("删除标签组").setMessage("你确定要删除该标签组吗？")
                    .create().show();
        }
    };

    private TagViewGroup.OnTagGroupDragListener mTagGroupDragListener = new TagViewGroup.OnTagGroupDragListener() {
        @Override
        public void onDrag(TagViewGroup container, float percentX, float percentY) {
            mTagImageView.onDrag(container, percentX, percentY);
        }
    };

    @Override
    public void onCancel() {
        mDialog.dismiss();
    }

    @Override
    public void onTagGroupCreated(TagGroupModel group) {
        mTagImageView.addTagGroup(group);
        mDialog.dismiss();
    }
}
