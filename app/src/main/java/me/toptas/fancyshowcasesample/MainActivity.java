package me.toptas.fancyshowcasesample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.toptas.fancyshowcase.AutoPositionViewInflateListener;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusDescriptor;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.OnViewInflateListener;
import me.toptas.fancyshowcase.OnViewInflateListenerV2;
import me.toptas.fancyshowcase.OnViewInflateListenerV3;

public class MainActivity extends BaseActivity {

    FancyShowCaseView mFancyShowCaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        StatusBarFucker fucker = new StatusBarFucker();
//        fucker.setWindowExtend(1);
//        fucker.setStatusBarColor(Color.argb(100, 0, 0, 0));
//        fucker.fuck(getWindow());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    /**
     * Shows a simple FancyShowCaseView
     */
    @OnClick(R.id.btn_simple)
    public void simple() {
//        new FancyShowCaseView.Builder(this)
//                .title("No Focus")
//                .build()
//                .show();

        startActivity(new Intent(this, TestConstraintLayoutActivity.class));
    }

    /**
     * Shows a FancyShowCaseView that focus on a view
     *
     * @param view View to focus
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @OnClick(R.id.btn_focus)
    public void focusView(View view) {

//        new FancyShowCaseView.Builder(this)
//                .focusOn(view)
//                .circlePadding(-30)
//                .title("Focus on View")
//                .build()
//                .show();

        FancyShowCaseView foo = FancyShowCaseView.newInstance(this);

        foo.addFocusDescriptor(new FocusDescriptor().shape(FocusShape.ROUNDED_RECTANGLE)
                .target(this, view)
                .rectRadius(0)
                .rectPaddingDp(this, 8, 8, 8, 8)
                .borderColor(Color.RED)
                .borderSize(3));

        foo.addFocusDescriptor(new FocusDescriptor().shape(FocusShape.ROUNDED_RECTANGLE)
                /*.target(findViewById(R.id.btn_rounded_rect))*/
                .rect(200, 800, 200, 200)
                /*.rectRadius(100)*/
                .borderColor(Color.GREEN)
                .borderSize(8));

        foo.addFocusDescriptor(new FocusDescriptor().shape(FocusShape.CIRCLE)
                .target(this, findViewById(R.id.btn_rounded_rect))
                .circlePadding(-60)
                .borderColor(Color.BLUE)
                .borderSize(1));

        foo.setCustomView(R.layout.layout_my_custom_view_4, new AutoPositionViewInflateListener(new OnViewInflateListener() {
            @Override
            public void onViewInflated(View view) {
                Button button  = (Button) view.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "YEAH!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }));

        foo.show();

    }

    /**
     * Shows a FancyShowCaseView with rounded rect focus shape
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_rounded_rect)
    public void focusRoundedRect(View view) {
//        new FancyShowCaseView.Builder(this)
//                .focusOn(view)
//                .focusShape(FocusShape.ROUNDED_RECTANGLE)
//                .roundRectRadius(90)
//                .title("Focus on View")
//                .build()
//                .show();

        FancyShowCaseView foo = FancyShowCaseView.newInstance(this);
        foo.addFocusDescriptor(new FocusDescriptor().shape(FocusShape.ROUNDED_RECTANGLE).target(this, view).rectPadding(10, 20, 30, 40));
        foo.setCustomView(R.layout.layout_my_custom_view_3, new AutoPositionViewInflateListener());
        foo.show();
    }

    /**
     * Shows FancyShowCaseView with focusCircleRadiusFactor 1.5 and title gravity
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_focus2)
    public void focusWithLargerCircle(View view) {
        new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .focusCircleRadiusFactor(1.5)
                .title("Focus on View with larger circle")
                .focusBorderColor(Color.GREEN)
                .titleStyle(0, Gravity.BOTTOM | Gravity.CENTER)
                .build()
                .show();
    }

    /**
     * Shows FancyShowCaseView at specific position (round rectangle shape)
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_rect_position)
    public void focusRoundRectPosition(View view) {
        new FancyShowCaseView.Builder(this)
                .title("Focus on larger view")
                .focusRectAtPosition(260, 85,  480, 80)
                .roundRectRadius(60)
                .build()
                .show();
    }

    /**
     * Shows a FancyShowCaseView that focuses on a larger view
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_focus_rect_color)
    public void focusRectWithBorderColor(View view) {
        new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .title("Focus on larger view")
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(50)
                .focusBorderSize(5)
                .focusBorderColor(Color.RED)
                .titleStyle(0, Gravity.TOP)
                .build()
                .show();
    }

    /**
     * Shows a FancyShowCaseView with background color and title style
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_background_color)
    public void focusWithBackgroundColor(View view) {
        new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .backgroundColor(Color.parseColor("#AAff0000"))
                .title("Background color and title style can be changed")
                .titleStyle(R.style.MyTitleStyle, Gravity.TOP | Gravity.CENTER)
                .build()
                .show();
    }

    /**
     * Shows a FancyShowCaseView with border color
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_border_color)
    public void focusWithBorderColor(View view) {
        new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .title("Focus border color can be changed")
                .titleStyle(R.style.MyTitleStyle, Gravity.TOP | Gravity.CENTER)
                .focusBorderColor(Color.GREEN)
                .focusBorderSize(10)
                .build()
                .show();
    }

    /**
     * Shows a FancyShowCaseView with custom enter, exit animations
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_anim)
    public void focusWithCustomAnimation(View view) {
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);

        final FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .title("Custom enter and exit animations.")
                .enterAnimation(enterAnimation)
                .exitAnimation(exitAnimation)
                .build();
        fancyShowCaseView.show();
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fancyShowCaseView.removeView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * Shows a FancyShowCaseView view custom view inflation
     *
     * @param view View to focus
     */
    @OnClick(R.id.btn_custom_view)
    public void focusWithCustomView(View view) {
        mFancyShowCaseView = new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .customView(R.layout.layout_my_custom_view, new OnViewInflateListenerV2() {
                    @Override
                    public void onViewInflated(View view, final int focusCenterX, final int focusCenterY) {
                        view.findViewById(R.id.btn_action_1).setOnClickListener(mClickListener);
                        final View image = view.findViewById(R.id.image);

                        view.post(new Runnable() {
                            @Override
                            public void run() {

                                int imageCenterX = image.getRight() / 2;
                                int imageCenterY = image.getBottom() / 2;
                                image.setTranslationX(focusCenterX - imageCenterX);
                                image.setTranslationY(focusCenterY - imageCenterY);

                            }
                        });
                    }
                })
                /*.fitSystemWindows(true)*/
                .closeOnTouch(false)
                .build();
        mFancyShowCaseView.show();
    }

    @OnClick(R.id.btn_no_anim)
    public void noFocusAnimation(View view) {
        mFancyShowCaseView = new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .disableFocusAnimation()
                .build();
        mFancyShowCaseView.show();
    }

    @OnClick(R.id.btn_queue)
    public void queueMultipleInstances() {
        startActivity(new Intent(this, QueueActivity.class));
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mFancyShowCaseView.hide();
        }
    };

    @OnClick(R.id.btn_another_activity)
    public void anotherActivity() {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @OnClick(R.id.btn_recycler_view)
    public void recyclerViewSample(){
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Shows a FancyShowCaseView that focuses to ActionBar items
     *
     * @param item actionbar item to focus
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(item.getItemId()))
                .title("Focus on Actionbar items")
                .build()
                .show();
        return true;
    }
}
