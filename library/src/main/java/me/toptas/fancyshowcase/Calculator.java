package me.toptas.fancyshowcase;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by ftoptas on 23/03/17.
 * Geometric calculations for position, size and radius
 */

class Calculator {

    private final int mBitmapWidth, mBitmapHeight;
    private FocusShape mFocusShape;
    private int mFocusWidth, mFocusHeight, mCircleCenterX, mCircleCenterY, mCircleRadius;
    private boolean mHasFocus;

    private int roundRectPaddingLeft;
    private int roundRectPaddingTop;
    private int roundRectPaddingRight;
    private int roundRectPaddingBottom;
    private int circlePadding;

    public void setRoundRectPadding(int left, int top, int right, int bottom) {
        roundRectPaddingLeft = left;
        roundRectPaddingTop  = top;
        roundRectPaddingRight = right;
        roundRectPaddingBottom = bottom;
    }

    public void setCirclePadding(int circlePadding) {
        this.circlePadding = circlePadding;
    }

    public void setmCircleRadius(int mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }

    Calculator(Activity activity, FocusShape focusShape, View view, double radiusFactor,
               boolean fitSystemWindows) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        mBitmapWidth = deviceWidth;
        mBitmapHeight = deviceHeight - (fitSystemWindows ? 0 : Utils.getStatusBarHeight(activity));
        if (view != null) {
            int adjustHeight = (fitSystemWindows && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    ? 0 : Utils.getStatusBarHeight(activity));
            int[] viewPoint = new int[2];
            view.getLocationInWindow(viewPoint);
            mFocusWidth = view.getWidth();
            mFocusHeight = view.getHeight();
            mFocusShape = focusShape;
            mCircleCenterX = viewPoint[0] + mFocusWidth / 2;
            mCircleCenterY = viewPoint[1] + mFocusHeight / 2 - adjustHeight;
            mCircleRadius = (int) ((int) (Math.hypot(view.getWidth(), view.getHeight()) / 2) * radiusFactor);
            mHasFocus = true;
        } else {
            mHasFocus = false;
        }
    }

    /**
     *  Setting round rectangle focus at specific position
     *
     * @param positionX       focus at specific position Y coordinate
     * @param positionY       focus at specific position circle radius
     * @param rectWidth   focus at specific position rectangle width
     * @param rectHeight  focus at specific position rectangle height
     */

    public void setRectPosition(int positionX, int positionY, int rectWidth, int rectHeight) {
        mCircleCenterX = positionX;
        mCircleCenterY = positionY;
        mFocusWidth = rectWidth;
        mFocusHeight = rectHeight;
        mFocusShape = FocusShape.ROUNDED_RECTANGLE;
        mHasFocus = true;
    }

    /**
     *  Setting circle focus at specific position
     *
     * @param positionX       focus at specific position Y coordinate
     * @param positionY       focus at specific position circle radius
     * @param radius          focus at specific position circle radius
     */

    public void setCirclePosition(int positionX, int positionY, int radius) {
        mCircleCenterX = positionX;
        mCircleRadius = radius;
        mCircleCenterY = positionY;
        mFocusShape = FocusShape.CIRCLE;
        mHasFocus = true;
    }


    /**
     * @return Shape of focus
     */
    FocusShape getFocusShape() {
        return mFocusShape;
    }

    /**
     * @return Focus width
     */
    int getFocusWidth() {
        return mFocusWidth;
    }

    /**
     * @return Focus height
     */
    int getFocusHeight() {
        return mFocusHeight;
    }

    /**
     * @return X coordinate of focus circle
     */
    int getCircleCenterX() {
        return mCircleCenterX;
    }

    /**
     * @return Y coordinate of focus circle
     */
    int getCircleCenterY() {
        return mCircleCenterY;
    }

    /**
     * @return Radius of focus circle
     */
    int getViewRadius() {
        return mCircleRadius;
    }

    /**
     * @return True if there is a view to focus
     */
    boolean hasFocus() {
        return mHasFocus;
    }

    /**
     * @return Width of background bitmap
     */
    int getBitmapWidth() {
        return mBitmapWidth;
    }

    /**
     * @return Height of background bitmap
     */
    int getBitmapHeight() {
        return mBitmapHeight;
    }

    /**
     * @param animCounter    Counter for circle animation
     * @param animMoveFactor Move factor for circle animation (Bigger value makes bigger animation)
     * @return Radius of animating circle
     */
    float circleRadius(int animCounter, double animMoveFactor) {
        return (float) (mCircleRadius + circlePadding + animCounter * animMoveFactor);
    }


    /**
     * @param animCounter    Counter for round rect animation
     * @param animMoveFactor Move factor for round rect animation (Bigger value makes bigger animation)
     * @return Bottom position of round rect
     */
    float roundRectLeft(int animCounter, double animMoveFactor) {
        /*return (float) (mCircleCenterX - mFocusWidth / 2 - animCounter * animMoveFactor );*/
        return (float) (mCircleCenterX - mFocusWidth / 2d - roundRectPaddingLeft - animCounter * animMoveFactor );
    }

    /**
     * @param animCounter    Counter for round rect animation
     * @param animMoveFactor Move factor for round rect animation (Bigger value makes bigger animation)
     * @return Top position of focus round rect
     */
    float roundRectTop(int animCounter, double animMoveFactor) {
        /*return (float) (mCircleCenterY - mFocusHeight / 2 - animCounter * animMoveFactor);*/
        return (float) (mCircleCenterY - mFocusHeight / 2d - roundRectPaddingTop - animCounter * animMoveFactor);
    }

    /**
     * @param animCounter    Counter for round rect animation
     * @param animMoveFactor Move factor for round rect animation (Bigger value makes bigger animation)
     * @return Bottom position of round rect
     */
    float roundRectRight(int animCounter, double animMoveFactor) {
        /*return (float) (mCircleCenterX + mFocusWidth / 2 + animCounter * animMoveFactor);*/
        return (float) (mCircleCenterX + mFocusWidth / 2d + roundRectPaddingRight + animCounter * animMoveFactor);
    }

    /**
     * @param animCounter    Counter for round rect animation
     * @param animMoveFactor Move factor for round rect animation (Bigger value makes bigger animation)
     * @return Bottom position of round rect
     */
    float roundRectBottom(int animCounter, double animMoveFactor) {
        /*return (float) (mCircleCenterY + mFocusHeight / 2 + animCounter * animMoveFactor);*/
        return (float) (mCircleCenterY + mFocusHeight / 2d + roundRectPaddingBottom + animCounter * animMoveFactor);
    }

    /**
     * @param animCounter    Counter for round rect animation
     * @param animMoveFactor Move factor for round rect animation (Bigger value makes bigger animation)
     * @return Radius of focus round rect
     */
    float roundRectLeftCircleRadius(int animCounter, double animMoveFactor) {
        return (float) (mFocusHeight / 2 + animCounter * animMoveFactor);
    }
}
