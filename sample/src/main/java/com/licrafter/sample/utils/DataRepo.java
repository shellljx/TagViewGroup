package com.licrafter.sample.utils;

import com.licrafter.sample.model.TagGroupModel;

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

        TagGroupModel.Tag tag0 = new TagGroupModel.Tag();
        tag0.setDirection(0);
        tag0.setLink("http://www.baidu.com");
        tag0.setName("这是左上的TAG");

        TagGroupModel.Tag tag1 = new TagGroupModel.Tag();
        tag1.setDirection(2);
        tag1.setLink("http://blog.licrafter.com");
        tag1.setName("这是左中的TAG,嘿嘿嘿哈哈哈哈。");

        TagGroupModel.Tag tag2 = new TagGroupModel.Tag();
        tag2.setDirection(3);
        tag2.setLink("http://mc.licrafter.com");
        tag2.setName("这是左下的TAG，凑字数!");

        model.getTags().add(tag0);
        model.getTags().add(tag1);
        model.getTags().add(tag2);
        model.setPercentX(0.3f);
        model.setPercentY(0.5f);
        tagGroupList.add(model);
    }
}
