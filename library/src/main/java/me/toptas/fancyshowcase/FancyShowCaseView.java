package me.toptas.fancyshowcase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by faruktoptas on 05/03/17.
 * FancyShowCaseView class
 */

public class FancyShowCaseView extends FrameLayout {

    FancyShowCaseView(@NonNull Context context) {
        super(context);
    }

    FancyShowCaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    FancyShowCaseView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    FancyShowCaseView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Tag for container view
    private static final String CONTAINER_TAG = "ShowCaseViewTag";
    // SharedPreferences name
    private static final String PREF_NAME = "PrefShowCaseView";

    /**
     * Resets the show once flag
     *
     * @param context context that should be used to create the shared preference instance
     * @param id      id of the show once flag that should be reset
     */
    public static void resetShowOnce(Context context, String id) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.edit().remove(id).commit();
    }

    /**
     * Resets all show once flags
     *
     * @param context context that should be used to create the shared preference instance
     */
    public static void resetAllShowOnce(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().commit();
    }

    /**
     * Builder parameters
     */
    private Activity mActivity;
    private String mTitle;
    private Spanned mSpannedTitle;
    private String mId;
    private double mFocusCircleRadiusFactor;
    private View mView;
    private int mBackgroundColor;
    private int mFocusBorderColor;
    private int mTitleGravity;
    private int mTitleStyle;
    private int mTitleSize;
    private int mTitleSizeUnit;
    private int mCustomViewRes;
    private int mFocusBorderSize;
    private int mRoundRectRadius;
    private OnViewInflateListener mViewInflateListener;
    private Animation mEnterAnimation, mExitAnimation;
    private boolean mCloseOnTouch;
    private boolean mFitSystemWindows;
    private FocusShape mFocusShape;
    private DismissListener mDismissListener;


    private int mAnimationDuration = 400;
    private int mCenterX, mCenterY, mRadius;
    private ViewGroup mRoot;
    private SharedPreferences mSharedPreferences;
    private Calculator mCalculator;

    private int mFocusPositionX, mFocusPositionY, mFocusCircleRadius, mFocusRectangleWidth, mFocusRectangleHeight;

    private boolean mFocusAnimationEnabled;

    private int roundRectPaddingLeft;
    private int roundRectPaddingTop;
    private int roundRectPaddingRight;
    private int roundRectPaddingBottom;

    private int circlePadding;

    private OnViewInflateListenerV2 mViewInflateListenerV2;

    /**
     * Constructor for FancyShowCaseView
     *
     * @param activity                Activity to show FancyShowCaseView in
     * @param view                    view to focus
     * @param id                      unique identifier for FancyShowCaseView
     * @param title                   title text
     * @param spannedTitle            title text if spanned text should be used
     * @param titleGravity            title gravity
     * @param titleStyle              title text style
     * @param titleSize               title text size
     * @param titleSizeUnit           title text size unit
     * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
     * @param backgroundColor         background color of FancyShowCaseView
     * @param focusBorderColor        focus border color of FancyShowCaseView
     * @param focusBorderSize         focus border size of FancyShowCaseView
     * @param customViewRes           custom view layout resource
     * @param viewInflateListener     inflate listener for custom view
     * @param enterAnimation          enter animation for FancyShowCaseView
     * @param exitAnimation           exit animation for FancyShowCaseView
     * @param closeOnTouch            closes on touch if enabled
     * @param fitSystemWindows        should be the same value of root view's fitSystemWindows value
     * @param focusShape              shape of focus, can be circle or rounded rectangle
     * @param dismissListener         listener that gets notified when showcase is dismissed
     * @param roundRectRadius         round rectangle radius
     * @param focusPositionX          focus at specific position X coordinate
     * @param focusPositionY          focus at specific position Y coordinate
     * @param focusCircleRadius       focus at specific position circle radius
     * @param focusRectangleWidth     focus at specific position rectangle width
     * @param focusRectangleHeight    focus at specific position rectangle height
     * @param animationEnabled        flag to enable/disable animation
     */
    private FancyShowCaseView(Activity activity, View view, String id, String title, Spanned spannedTitle,
                              int titleGravity, int titleStyle, int titleSize, int titleSizeUnit, double focusCircleRadiusFactor,
                              int backgroundColor, int focusBorderColor, int focusBorderSize, int customViewRes,
                              OnViewInflateListener viewInflateListener, Animation enterAnimation,
                              Animation exitAnimation, boolean closeOnTouch, boolean fitSystemWindows,
                              FocusShape focusShape, DismissListener dismissListener, int roundRectRadius,
                              int focusPositionX, int focusPositionY, int focusCircleRadius, int focusRectangleWidth, int focusRectangleHeight,
                              final boolean animationEnabled) {
        super(activity);
        mId = id;
        mActivity = activity;
        mView = view;
        mTitle = title;
        mSpannedTitle = spannedTitle;
        mFocusCircleRadiusFactor = focusCircleRadiusFactor;
        mBackgroundColor = backgroundColor;
        mFocusBorderColor = focusBorderColor;
        mFocusBorderSize = focusBorderSize;
        mTitleGravity = titleGravity;
        mTitleStyle = titleStyle;
        mTitleSize = titleSize;
        mTitleSizeUnit = titleSizeUnit;
        mRoundRectRadius = roundRectRadius;
        mCustomViewRes = customViewRes;
        mViewInflateListener = viewInflateListener;
        mEnterAnimation = enterAnimation;
        mExitAnimation = exitAnimation;
        mCloseOnTouch = closeOnTouch;
        mFitSystemWindows = fitSystemWindows;
        mFocusShape = focusShape;
        mDismissListener = dismissListener;
        mFocusPositionX = focusPositionX;
        mFocusPositionY = focusPositionY;
        mFocusCircleRadius = focusCircleRadius;
        mFocusRectangleWidth = focusRectangleWidth;
        mFocusRectangleHeight = focusRectangleHeight;
        mFocusAnimationEnabled = animationEnabled;

        initializeParameters();
    }

    /**
     * Calculates and set initial parameters
     */
    private void initializeParameters() {
        mBackgroundColor = mBackgroundColor != 0 ? mBackgroundColor :
                mActivity.getResources().getColor(R.color.fancy_showcase_view_default_background_color);
        mTitleGravity = mTitleGravity >= 0 ? mTitleGravity : Gravity.CENTER;
        mTitleStyle = mTitleStyle != 0 ? mTitleStyle : R.style.FancyShowCaseDefaultTitleStyle;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        mCenterX = deviceWidth / 2;
        mCenterY = deviceHeight / 2;
        mSharedPreferences = mActivity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setRoundRectPadding(int left, int top, int right, int bottom) {
        roundRectPaddingLeft = left;
        roundRectPaddingTop  = top;
        roundRectPaddingRight = right;
        roundRectPaddingBottom = bottom;
    }

    public void setCirclePadding(int circlePadding) {
        this.circlePadding = circlePadding;
    }

    public void setViewInflateListenerV2(OnViewInflateListenerV2 viewInflateListenerV2) {
        this.mViewInflateListenerV2 = viewInflateListenerV2;
    }

    /**
     * Shows FancyShowCaseView
     */
    public void show() {
        if (mActivity == null || (mId != null && isShownBefore())) {
            if (mDismissListener != null) {
                mDismissListener.onSkipped(mId);
            }
            return;
        }

        mCalculator = new Calculator(mActivity, mFocusShape, mView, mFocusCircleRadiusFactor,
                mFitSystemWindows);
        /*Bitmap bitmap = Bitmap.createBitmap(mCalculator.getBitmapWidth(), mCalculator.getBitmapHeight(),
                Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(mBackgroundColor);*/

        ViewGroup androidContent = (ViewGroup) mActivity.findViewById(android.R.id.content);
        mRoot = (ViewGroup) androidContent.getParent().getParent();
        FancyShowCaseView visibleView = (FancyShowCaseView) mRoot.findViewWithTag(CONTAINER_TAG);
        setClickable(true);
        if (visibleView == null) {
            //mContainer = new FrameLayout(mActivity);
            setTag(CONTAINER_TAG);
            if (mCloseOnTouch) {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hide();
                    }
                });
            }
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mRoot.addView(this);


            FancyImageView imageView = new FancyImageView(mActivity);
            if (mCalculator.hasFocus()) {
                //Utils.drawFocusCircle(bitmap, focusPoint, focusPoint[2]);
                mCenterX = mCalculator.getCircleCenterX();
                mCenterY = mCalculator.getCircleCenterY();
                mRadius = mCalculator.getViewRadius();
            }


            imageView.setParameters(mBackgroundColor, mCalculator);
            if (mFocusRectangleWidth > 0 && mFocusRectangleHeight > 0) {
                mCalculator.setRectPosition(mFocusPositionX, mFocusPositionY, mFocusRectangleWidth, mFocusRectangleHeight);
            }
            if (mFocusCircleRadius > 0) {
                mCalculator.setCirclePosition(mFocusPositionX, mFocusPositionY, mFocusCircleRadius);
            }
            mCalculator.setRoundRectPadding(roundRectPaddingLeft, roundRectPaddingTop, roundRectPaddingRight, roundRectPaddingBottom);
            mCalculator.setCirclePadding(circlePadding);

            imageView.setAnimationEnabled(mFocusAnimationEnabled);
            imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (mFocusBorderColor != 0 && mFocusBorderSize > 0) {
                imageView.setBorderParameters(mFocusBorderColor, mFocusBorderSize);
            }
            if (mRoundRectRadius >= 0) {
                imageView.setRoundRectRadius(mRoundRectRadius);
            }
            //imageView.setImageBitmap(bitmap);
            addView(imageView);

            if (mCustomViewRes == 0) {
                inflateTitleView();
            } else {
                inflateCustomView(mCustomViewRes, mViewInflateListener);
            }

            startEnterAnimation();
            writeShown();
        }
    }

    /**
     * Check is FancyShowCaseView visible
     *
     * @param activity should be used to find FancyShowCaseView inside it
     */
    public static Boolean isVisible(Activity activity) {
        ViewGroup androidContent = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup mRoot = (ViewGroup) androidContent.getParent().getParent();
        FancyShowCaseView mContainer = (FancyShowCaseView) mRoot.findViewWithTag(CONTAINER_TAG);
        return mContainer != null;
    }

    /**
     * Hide  FancyShowCaseView
     *
     * @param activity should be used to hide FancyShowCaseView inside it
     */
    public static void hideCurrent(Activity activity) {
        ViewGroup androidContent = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup mRoot = (ViewGroup) androidContent.getParent().getParent();
        FancyShowCaseView mContainer = (FancyShowCaseView) mRoot.findViewWithTag(CONTAINER_TAG);
        mContainer.hide();
    }

    /**
     * Starts enter animation of FancyShowCaseView
     */
    private void startEnterAnimation() {
        if (mEnterAnimation != null) {
            startAnimation(mEnterAnimation);
        } else if (false && Utils.shouldShowCircularAnimation()) {
            doCircularEnterAnimation();
        } else {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_in);
            fadeInAnimation.setFillAfter(true);
            startAnimation(fadeInAnimation);
        }
    }

    /**
     * Hides FancyShowCaseView with animation
     */
    public void hide() {
        if (mExitAnimation != null) {
            startAnimation(mExitAnimation);
        } else if (false && Utils.shouldShowCircularAnimation()) {
            doCircularExitAnimation();
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeView();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fadeOut.setFillAfter(true);
            startAnimation(fadeOut);
        }
    }

    /**
     * Inflates custom view
     *
     * @param layout              layout for custom view
     * @param viewInflateListener inflate listener for custom view
     */
    private void inflateCustomView(@LayoutRes int layout, OnViewInflateListener viewInflateListener) {
        View view = mActivity.getLayoutInflater().inflate(layout, this, false);
        this.addView(view);
        if (viewInflateListener != null) {
            viewInflateListener.onViewInflated(view);
        }
        if (mViewInflateListenerV2 != null) {
            mViewInflateListenerV2.onViewInflated(view, mCenterX, mCenterY);
        }
    }

    /**
     * Inflates title view layout
     */
    private void inflateTitleView() {
        inflateCustomView(R.layout.fancy_showcase_view_layout_title, new OnViewInflateListener() {
            @Override
            public void onViewInflated(View view) {
                TextView textView = (TextView) view.findViewById(R.id.fscv_title);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(mTitleStyle);
                } else {
                    textView.setTextAppearance(mActivity, mTitleStyle);
                }
                if (mTitleSize != -1) {
                    textView.setTextSize(mTitleSizeUnit, mTitleSize);
                }
                textView.setGravity(mTitleGravity);
                if (mSpannedTitle != null) {
                    textView.setText(mSpannedTitle);
                } else {
                    textView.setText(mTitle);
                }
            }
        });

    }

    /**
     * Circular reveal enter animation
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void doCircularEnterAnimation() {
        getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(this);

                        final int revealRadius = (int) Math.hypot(
                                getWidth(), getHeight());
                        int startRadius = 0;
                        if (mView != null) {
                            startRadius = mView.getWidth() / 2;
                        } else if (mFocusCircleRadius > 0 || mFocusRectangleWidth > 0 || mFocusRectangleHeight > 0) {
                            mCenterX = mFocusPositionX;
                            mCenterY = mFocusPositionY;
                        }
                        Animator enterAnimator = ViewAnimationUtils.createCircularReveal(FancyShowCaseView.this,
                                mCenterX, mCenterY, startRadius, revealRadius);
                        enterAnimator.setDuration(mAnimationDuration);
                        enterAnimator.setInterpolator(AnimationUtils.loadInterpolator(mActivity,
                                android.R.interpolator.accelerate_cubic));
                        enterAnimator.start();
                        return false;
                    }
                });

    }

    /**
     * Circular reveal exit animation
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doCircularExitAnimation() {
        final int revealRadius = (int) Math.hypot(getWidth(), getHeight());
        Animator exitAnimator = ViewAnimationUtils.createCircularReveal(this,
                mCenterX, mCenterY, revealRadius, 0f);
        exitAnimator.setDuration(mAnimationDuration);
        exitAnimator.setInterpolator(AnimationUtils.loadInterpolator(mActivity,
                android.R.interpolator.decelerate_cubic));
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView();

            }
        });
        exitAnimator.start();


    }

    /**
     * Saves the FancyShowCaseView id to SharedPreferences that is shown once
     */
    private void writeShown() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mId, true);
        editor.apply();
    }

    /**
     * Returns if FancyShowCaseView with given id is shown before
     *
     * @return true if show before
     */
    public boolean isShownBefore() {
        return mSharedPreferences.getBoolean(mId, false);
    }

    /**
     * Removes FancyShowCaseView view from activity root view
     */
    public void removeView() {
        mRoot.removeView(this);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(mId);
        }
    }

    protected DismissListener getDismissListener() {
        return mDismissListener;
    }

    protected void setDismissListener(DismissListener dismissListener) {
        mDismissListener = dismissListener;
    }


    /**
     * Builder class for {@link FancyShowCaseView}
     */
    public static class Builder {

        private Activity mActivity;
        private View mView;
        private String mId;
        private String mTitle;
        private Spanned mSpannedTitle;
        private double mFocusCircleRadiusFactor = 1;
        private int mBackgroundColor;
        private int mFocusBorderColor;
        private int mTitleGravity = -1;
        private int mTitleSize = -1;
        private int mTitleSizeUnit = -1;
        private int mTitleStyle;
        private int mCustomViewRes;
        private int mRoundRectRadius = -1;
        private OnViewInflateListener mViewInflateListener;
        private OnViewInflateListenerV2 mViewInflateListenerV2;
        private Animation mEnterAnimation, mExitAnimation;
        private boolean mCloseOnTouch = true;
        private boolean mFitSystemWindows;
        private FocusShape mFocusShape = FocusShape.CIRCLE;
        private DismissListener mDismissListener = null;
        private int mFocusBorderSize;
        private int mFocusPositionX, mFocusPositionY, mFocusCircleRadius, mFocusRectangleWidth, mFocusRectangleHeight;
        private boolean mFocusAnimationEnabled = true;

        private int roundRectPaddingLeft;
        private int roundRectPaddingTop;
        private int roundRectPaddingRight;
        private int roundRectPaddingBottom;

        private int circlePadding;

        /**
         * Constructor for Builder class
         *
         * @param activity Activity to show FancyShowCaseView in
         */
        public Builder(Activity activity) {
            mActivity = activity;
        }


        /**
         * @param title title text
         * @return Builder
         */
        public Builder title(String title) {
            mTitle = title;
            mSpannedTitle = null;
            return this;
        }

        /**
         * @param title title text
         * @return Builder
         */
        public Builder title(Spanned title) {
            mSpannedTitle = title;
            mTitle = null;
            return this;
        }

        /**
         * @param style        title text style
         * @param titleGravity title gravity
         * @return Builder
         */
        public Builder titleStyle(@StyleRes int style, int titleGravity) {
            mTitleGravity = titleGravity;
            mTitleStyle = style;
            return this;
        }

        /**
         * @param focusBorderColor Border color for focus shape
         * @return Builder
         */
        public Builder focusBorderColor(int focusBorderColor) {
            mFocusBorderColor = focusBorderColor;
            return this;
        }

        /**
         * @param focusBorderSize Border size for focus shape
         * @return Builder
         */
        public Builder focusBorderSize(int focusBorderSize) {
            mFocusBorderSize = focusBorderSize;
            return this;
        }

        /**
         * @param titleGravity title gravity
         * @return Builder
         */
        public Builder titleGravity(int titleGravity) {
            mTitleGravity = titleGravity;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param titleSize title size
         * @param unit      title text unit
         * @return Builder
         */
        public Builder titleSize(int titleSize, int unit) {
            mTitleSize = titleSize;
            mTitleSizeUnit = unit;
            return this;
        }

        /**
         * @param id unique identifier for FancyShowCaseView
         * @return Builder
         */
        public Builder showOnce(String id) {
            mId = id;
            return this;
        }

        /**
         * @param view view to focus
         * @return Builder
         */
        public Builder focusOn(View view) {
            mView = view;
            return this;
        }

        /**
         * @param backgroundColor background color of FancyShowCaseView
         * @return Builder
         */
        public Builder backgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        /**
         * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
         * @return Builder
         */
        public Builder focusCircleRadiusFactor(double focusCircleRadiusFactor) {
            mFocusCircleRadiusFactor = focusCircleRadiusFactor;
            return this;
        }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        public Builder customView(@LayoutRes int layoutResource, @Nullable OnViewInflateListener listener) {
            mCustomViewRes = layoutResource;
            mViewInflateListener = listener;
            return this;
        }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        public Builder customView(@LayoutRes int layoutResource, @Nullable OnViewInflateListenerV2 listener) {
            mCustomViewRes = layoutResource;
            mViewInflateListenerV2 = listener;
            return this;
        }

        /**
         * @param enterAnimation enter animation for FancyShowCaseView
         * @return Builder
         */
        public Builder enterAnimation(Animation enterAnimation) {
            mEnterAnimation = enterAnimation;
            return this;
        }

        /**
         * @param exitAnimation exit animation for FancyShowCaseView
         * @return Builder
         */
        public Builder exitAnimation(Animation exitAnimation) {
            mExitAnimation = exitAnimation;
            return this;
        }

        /**
         * @param closeOnTouch closes on touch if enabled
         * @return Builder
         */
        public Builder closeOnTouch(boolean closeOnTouch) {
            mCloseOnTouch = closeOnTouch;
            return this;
        }

        /**
         * This should be the same as root view's fitSystemWindows value
         *
         * @param fitSystemWindows fitSystemWindows value
         * @return Builder
         */
        public Builder fitSystemWindows(boolean fitSystemWindows) {
            mFitSystemWindows = fitSystemWindows;
            return this;
        }

        public Builder focusShape(FocusShape focusShape) {
            mFocusShape = focusShape;
            return this;
        }

        /**
         * Focus round rectangle at specific position
         *
         * @param positionX      focus at specific position Y coordinate
         * @param positionY      focus at specific position circle radius
         * @param positionWidth  focus at specific position rectangle width
         * @param positionHeight focus at specific position rectangle height
         * @return Builder
         */

        public Builder focusRectAtPosition(int positionX, int positionY, int positionWidth, int positionHeight) {
            mFocusPositionX = positionX;
            mFocusPositionY = positionY;
            mFocusRectangleWidth = positionWidth;
            mFocusRectangleHeight = positionHeight;
            return this;
        }

        /**
         * Focus circle at specific position
         *
         * @param positionX focus at specific position Y coordinate
         * @param positionY focus at specific position circle radius
         * @param radius    focus at specific position circle radius
         * @return Builder
         */

        public Builder focusCircleAtPosition(int positionX, int positionY, int radius) {
            mFocusPositionX = positionX;
            mFocusPositionY = positionY;
            mFocusCircleRadius = radius;
            return this;
        }

        /**
         * @param dismissListener the dismiss listener
         * @return Builder
         */
        public Builder dismissListener(DismissListener dismissListener) {
            mDismissListener = dismissListener;
            return this;
        }

        public Builder roundRectRadius(int roundRectRadius) {
            mRoundRectRadius = roundRectRadius;
            return this;
        }

        /**
         * disable Focus Animation
         *
         * @return Builder
         */
        public Builder disableFocusAnimation() {
            mFocusAnimationEnabled = false;
            return this;
        }

        /**
         * 当设置形状为圆角矩形时，对focus区域周围增加padding，正padding相当于加大focus区域，负padding相当于减小focus区域
         */
        public Builder roundRectPadding(int left, int top, int right, int bottom) {
            roundRectPaddingLeft = left;
            roundRectPaddingTop = top;
            roundRectPaddingRight = right;
            roundRectPaddingBottom = bottom;
            return this;
        }

        /**
         * 当设置形状为圆形时，对focus区域周围增加padding，正padding相当于加大focus区域，负padding相当于减小focus区域
         */
        public Builder circlePadding(int padding) {
            circlePadding= padding;
            return this;
        }

        /**
         * builds the builder
         *
         * @return {@link FancyShowCaseView} with given parameters
         */
        public FancyShowCaseView build() {

            FancyShowCaseView fff = new FancyShowCaseView(mActivity, mView, mId, mTitle, mSpannedTitle, mTitleGravity, mTitleStyle, mTitleSize, mTitleSizeUnit,
                    mFocusCircleRadiusFactor, mBackgroundColor, mFocusBorderColor, mFocusBorderSize, mCustomViewRes, mViewInflateListener,
                    mEnterAnimation, mExitAnimation, mCloseOnTouch, mFitSystemWindows, mFocusShape, mDismissListener, mRoundRectRadius,
                    mFocusPositionX, mFocusPositionY, mFocusCircleRadius, mFocusRectangleWidth, mFocusRectangleHeight, mFocusAnimationEnabled);

            fff.setViewInflateListenerV2(mViewInflateListenerV2);

            fff.setRoundRectPadding(roundRectPaddingLeft, roundRectPaddingTop, roundRectPaddingRight, roundRectPaddingBottom);

            fff.setCirclePadding(circlePadding);

            return fff;
        }
    }
}
