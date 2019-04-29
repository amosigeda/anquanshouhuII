/**
 * Copyright (c) 2011-2014 FengWoo Network Co.,Ltd.
 *
 * Prohibition is granted, charge, to any person obtaining a copy of this software and 
 * associated documentation files (the "Software"), limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software
 *
 */
package vip.inteltech.gat.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @ClassName: AnimateFirstDisplayListener
 * @Package cn.fengwoo.im.utils
 * @Description: TODO
 * @Author <a href="mailto:huhao@fengwoo.cn">ZhaoGuoBiao</a>  
 * @Date 2014广12朿9旿 下午6:01:45
 * @Version iwowshow 1.0.0
 */
public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
    static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (loadedImage != null) {
            ImageView imageView = (ImageView) view;
            // 是否第一次显示
            boolean firstDisplay = !displayedImages.contains(imageUri);
            if (firstDisplay) {
                // 图片淡入效果
                FadeInBitmapDisplayer.animate(imageView, 500);
                displayedImages.add(imageUri);
            }
        }
    }
}