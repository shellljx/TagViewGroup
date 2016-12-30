package com.licrafter.sample.model;

import java.util.ArrayList;
import java.util.List;

/**
 * author: shell
 * date 2016/12/30 下午3:37
 **/
public class TagGroupModel {

    private List<Tag> tags = new ArrayList<>();
    private float percentX;
    private float percentY;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public float getPercentX() {
        return percentX;
    }

    public void setPercentX(float percentX) {
        this.percentX = percentX;
    }

    public float getPercentY() {
        return percentY;
    }

    public void setPercentY(float percentY) {
        this.percentY = percentY;
    }

    public static class Tag {
        public String name;
        public String link;
        public int direction;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }
    }
}
