package com.licrafter.sample.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * author: shell
 * date 2016/12/30 下午3:45
 **/
public class ImageLoader {

    public static void loadImage(String url, ImageView imageView) {
        Picasso.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
