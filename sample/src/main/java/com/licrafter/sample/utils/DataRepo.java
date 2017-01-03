package com.licrafter.sample.utils;

import com.licrafter.sample.model.TagGroupModel;
import com.licrafter.tagview.DIRECTION;

import java.util.ArrayList;
import java.util.List;

/**
 * author: shell
 * date 2016/12/30 下午4:46
 **/
//没有网络请求，tags数据存在内存中
public class DataRepo {
    public static List<TagGroupModel> tagGroupList = new ArrayList<>();

    public static void initData() {
        TagGroupModel model = new TagGroupModel();
        TagGroupModel model2 = new TagGroupModel();

        TagGroupModel.Tag tag0 = new TagGroupModel.Tag();
        tag0.setDirection(DIRECTION.RIGHT_TOP.getValue());
        tag0.setLink("http://www.baidu.com");
        tag0.setName("头层牛皮");

        TagGroupModel.Tag tag1 = new TagGroupModel.Tag();
        tag1.setDirection(DIRECTION.RIGHT_CENTER.getValue());
        tag1.setLink("http://blog.licrafter.com");
        tag1.setName("英伦加绒青年棕色保暖鞋子。");

        TagGroupModel.Tag tag2 = new TagGroupModel.Tag();
        tag2.setDirection(DIRECTION.RIGHT_BOTTOM.getValue());
        tag2.setLink("http://mc.licrafter.com");
        tag2.setName("PLAYBOY/花花公子");

        TagGroupModel.Tag tag3 = new TagGroupModel.Tag();
        tag3.setDirection(DIRECTION.RIGHT_CENTER.getValue());
        tag3.setLink("http://mc.licrafter.com");
        tag3.setName("很不错的鞋子");

        model.getTags().add(tag0);
        model.getTags().add(tag1);
        model.getTags().add(tag2);
        model.setPercentX(0.4f);
        model.setPercentY(0.5f);

        model2.getTags().add(tag3);
        model2.setPercentY(0.2f);
        model2.setPercentX(0.3f);

        tagGroupList.add(model);
        tagGroupList.add(model2);
    }
}
