package com.licrafter.sample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.licrafter.sample.R;
import com.licrafter.sample.model.TagGroupModel;
import com.licrafter.sample.utils.ImageLoader;
import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;
import com.licrafter.tagview.utils.AnimatorUtils;
import com.licrafter.tagview.views.TagTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: shell
 * date 2016/12/30 下午3:13
 **/
public class TagImageView extends FrameLayout {

    private ImageView mImageView;
    private FrameLayout mTagsContent;
    private List<TagGroupModel> mTagGroupList = new ArrayList<>();
    private List<TagViewGroup> mTagGroupViewList = new ArrayList<>();

    public TagImageView(Context context) {
        this(context, null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.layout_tag_imageview, this, true);
        mImageView = (ImageView) rootView.findViewById(R.id.imageview);
        mTagsContent = (FrameLayout) rootView.findViewById(R.id.tagsGroup);
    }

    public void setTagList(List<TagGroupModel> tagGroupList) {
        mTagGroupList.clear();
        mTagsContent.removeAllViews();
        mTagGroupViewList.clear();
        mTagGroupList.addAll(tagGroupList);
        for (TagGroupModel model : mTagGroupList) {
            TagViewGroup tagViewGroup = getTagViewGroup(model);
            tagViewGroup.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(tagViewGroup);
            mTagGroupViewList.add(tagViewGroup);
        }
    }

    public TagViewGroup getTagViewGroup(TagGroupModel model) {
        TagViewGroup tagViewGroup = new TagViewGroup(getContext());
        tagViewGroup.setShowAnimator(AnimatorUtils.getTagShowAnimator(tagViewGroup))
                .setHideAnimator(AnimatorUtils.getTagHideAnimator(tagViewGroup));
        for (TagGroupModel.Tag tag : model.getTags()) {
            TagTextView tagTextView = new TagTextView(getContext());
            tagTextView.setDirection(parseDirection(tag.getDirection()));
            tagTextView.setText(tag.getName());
            tagViewGroup.addTag(tagTextView);
        }
        tagViewGroup.addRipple();
        tagViewGroup.setPercent(model.getPercentX(), model.getPercentY());
        return tagViewGroup;
    }

    public void excuteTagsAnimation() {
        for (TagViewGroup tagViewGroup : mTagGroupViewList) {
            if (tagViewGroup.isHiden()) {
                tagViewGroup.showWithAnimation();
            } else {
                tagViewGroup.hideWithAnimation();
            }
        }
    }

    public void setImageUrl(String url) {
        ImageLoader.loadImage(url, mImageView);
    }

    public DIRECTION parseDirection(int direction) {
        switch (direction) {
            case 0:
                return DIRECTION.RIGHT_TOP;
            case 1:
                return DIRECTION.RIGHT_TOP_TILT;
            case 2:
                return DIRECTION.RIGHT_CENTER;
            case 3:
                return DIRECTION.RIGHT_BOTTOM;
            case 4:
                return DIRECTION.RIGHT_BOTTOM_TILT;
            case 5:
                return DIRECTION.LEFT_BOTTOM;
            case 6:
                return DIRECTION.LEFT_BOTTOM_TILT;
            case 7:
                return DIRECTION.LEFT_CENTER;
            case 8:
                return DIRECTION.LEFT_TOP;
            case 9:
                return DIRECTION.LEFT_TOP_TILT;
            case 10:
                return DIRECTION.CENTER;

        }
        return null;
    }
}
