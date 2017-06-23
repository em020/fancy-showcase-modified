package me.toptas.fancyshowcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by yzsh-sym on 2017/6/20.
 */

public class FocusDescriptor {

    FocusShape focusShape;

    int centerX;
    int centerY;

    int rectWidth;
    int rectHeight;
    int roundRectRadius = 20;
    int roundRectPaddingLeft;
    int roundRectPaddingTop;
    int roundRectPaddingRight;
    int roundRectPaddingBottom;

    int circleRadius;
    int circlePadding;

    int focusBorderColor = Color.TRANSPARENT;
    int focusBorderSize;

    boolean noHole;


    public FocusDescriptor shape(FocusShape shape) {
        focusShape = shape;
        return this;
    }

    public FocusDescriptor target(Activity activity, View view) {

        // 我们想获得的focus area的coords是相对于activity的“内容区域” (content view, 也就是根布局) 的，为什么呢，
        // 因为我们的FancyShowcaseView 是一个铺满根布局的frame layout，其中添加了一个铺满容器的FancyImageView，
        // 打孔是发生在FancyImageView中的，所以打孔的coords是相对于FancyImageView的，所以也就是相对于根布局的

        // 但是getLocationInWindow这个拿到的是相对于整个屏幕的（已验证过），所以为了方便的拿到相对于根布局的，
        // 我们拿一下content view的coords，求出相对值

        int[] contentViewLocation = new int[2];
        activity.findViewById(android.R.id.content).getLocationInWindow(contentViewLocation);

        int[] viewPoint = new int[2];
        view.getLocationInWindow(viewPoint);

        viewPoint[0] = viewPoint[0] - contentViewLocation[0];
        viewPoint[1] = viewPoint[1] - contentViewLocation[1];

        rectWidth = view.getWidth();
        rectHeight = view.getHeight();
        centerX = viewPoint[0] + rectWidth / 2;
        centerY = viewPoint[1] + rectHeight / 2;
        circleRadius = (int) (Math.hypot(view.getWidth(), view.getHeight()) / 2d);

        return this;
    }

    /**
     * 注意，当通过参数指明区域时，参数的坐标必须是相对于根布局区域的，具体请参考target方法内的注释
     */
    public FocusDescriptor rect(int centerX, int centerY, int width, int height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.rectWidth = width;
        this.rectHeight = height;
        return this;
    }

    public FocusDescriptor rectRadius(int rectRadius) {
        this.roundRectRadius = rectRadius;
        return this;
    }

    public FocusDescriptor rectRadiusDp(Context context, int roundRectRadiusInDp) {
        int px = Utils.dp2pxV2(context, roundRectRadiusInDp);
        return rectRadius(px);
    }

    public FocusDescriptor rectPadding(int left, int top, int right, int bottom) {
        roundRectPaddingLeft = left;
        roundRectPaddingTop = top;
        roundRectPaddingRight = right;
        roundRectPaddingBottom = bottom;
        return this;
    }

    public FocusDescriptor rectPaddingDp(Context context, int left, int top, int right, int bottom) {
        int l = Utils.dp2pxV2(context, left);
        int t = Utils.dp2pxV2(context, top);
        int r = Utils.dp2pxV2(context, right);
        int b = Utils.dp2pxV2(context, bottom);
        return rectPadding(l, t, r ,b);
    }


    /**
     * 注意，当通过参数指明区域时，参数的坐标必须是相对于根布局区域的，具体请参考target方法内的注释
     */
    public FocusDescriptor circle(int centerX, int centerY, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.circleRadius = radius;
        return this;
    }

    public FocusDescriptor circlePadding(int padding) {
        circlePadding = padding;
        return this;
    }

    public FocusDescriptor circlePaddingDp(Context context, int paddingDp) {
        int px = Utils.dp2pxV2(context, paddingDp);
        return circlePadding(px);
    }

    public FocusDescriptor borderColor(int color) {
        this.focusBorderColor = color;
        return this;
    }

    public FocusDescriptor borderSize(int borderSize) {
        focusBorderSize = borderSize;
        return this;
    }

    public FocusDescriptor borderSizeDp(Context context, int borderSizeDp) {
        int px = Utils.dp2pxV2(context, borderSizeDp);
        return borderSize(px);
    }

    public FocusDescriptor noHole(boolean value) {
        this.noHole = value;
        return this;
    }


    @SuppressWarnings("WeakerAccess")
    public Rect getFocusAreaRect() {
        Rect rect = null;

        if (FocusShape.CIRCLE.equals(focusShape)) {

            int realRadius = circleRadius + circlePadding;
            rect = new Rect(centerX - realRadius, centerY - realRadius, centerX + realRadius, centerY + realRadius);


        } else if (FocusShape.ROUNDED_RECTANGLE.equals(focusShape)) {

            rect = new Rect(
                    centerX - rectWidth / 2 - roundRectPaddingLeft,
                    centerY - rectHeight / 2 - roundRectPaddingTop,
                    centerX + rectWidth / 2 + roundRectPaddingRight,
                    centerY + rectHeight / 2 + roundRectPaddingBottom);

        } else {
            throw new IllegalArgumentException();
        }

        return rect;
    }
}
