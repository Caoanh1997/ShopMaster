package com.example.caoan.shopmaster.ViewPageTransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            //trang này nằm ngoài màn hình bên trái
            // This page is way off-screen to the left.
            view.setAlpha(0f);

        } else if (position <= 1) { // [-1,1]
            //Sửa đổi chuyển đổi slide mặc định để thu nhỏ trang
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            //Thu nhỏ trang xuống (giữa MIN_SCALE và 1)
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            //Làm mờ trang so với kích thước của nó.
            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            //Trang này nằm ngoài màn hình bên phải.
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }
}
