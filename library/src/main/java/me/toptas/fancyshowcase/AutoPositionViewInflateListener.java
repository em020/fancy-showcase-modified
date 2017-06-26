package me.toptas.fancyshowcase;

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yzsh-sym on 2017/6/22.
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class AutoPositionViewInflateListener implements OnViewInflateListenerV3 {

    static final int[] anchorIds = {
            R.id.fscv_anchor1,
            R.id.fscv_anchor2,
            R.id.fscv_anchor3,
            R.id.fscv_anchor4,
            R.id.fscv_anchor5,
            R.id.fscv_anchor6,
            R.id.fscv_anchor7,
            R.id.fscv_anchor8,
            R.id.fscv_anchor9,
            R.id.fscv_anchor10
    };

    static final int[] viewIds = {
            R.id.fscv_view1,
            R.id.fscv_view2,
            R.id.fscv_view3,
            R.id.fscv_view4,
            R.id.fscv_view5,
            R.id.fscv_view6,
            R.id.fscv_view7,
            R.id.fscv_view8,
            R.id.fscv_view9,
            R.id.fscv_view10
    };

    private View[] anchors, views;
    private OnViewInflateListener onViewInflaterListener;

    public AutoPositionViewInflateListener() {
    }

    public AutoPositionViewInflateListener(OnViewInflateListener onViewInflaterListener) {
        this.onViewInflaterListener = onViewInflaterListener;
    }

    @Override
    public void onViewInflated(View view, List<FocusDescriptor> focusDescriptorList) {

        int arraySize = focusDescriptorList.size();

        anchors = new View[arraySize];
        views = new View[arraySize];
        final Rect[] focusRects = new Rect[arraySize];

        for (int i = 0; i < anchors.length; i++) {
            anchors[i] = view.findViewById(anchorIds[i]);
            views[i] = view.findViewById(viewIds[i]);
            focusRects[i] = focusDescriptorList.get(i).getFocusAreaRect();

            if (anchors[i] != null) {
                anchors[i].setVisibility(View.INVISIBLE);
            }
            if (views[i] != null) {
                views[i].setVisibility(View.INVISIBLE);
            }
        }

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 第一个post，设置anchor尺寸至focus area实际尺寸

                View poster = null;
                for (int i = 0; i < anchors.length; i++) {
                    if (anchors[i] != null && !focusRects[i].isEmpty()) {
                        ViewGroup.LayoutParams params = anchors[i].getLayoutParams();
                        params.height = focusRects[i].height();
                        params.width = focusRects[i].width();
                        anchors[i].setLayoutParams(params);

                        if (poster == null) {
                            poster = anchors[i];
                        }
                    }
                }

                if (poster != null) {
                    poster.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 第二个post，移动views

                            for (int i = 0; i < anchors.length; i++) {
                                if (anchors[i] != null && !focusRects[i].isEmpty()) {
                                    float anchorCenterX = anchors[i].getLeft() + anchors[i].getWidth() / 2f;
                                    float anchorCenterY = anchors[i].getTop() + anchors[i].getHeight() / 2f;

                                    anchors[i].setTranslationX(focusRects[i].centerX() - anchorCenterX);
                                    anchors[i].setTranslationY(focusRects[i].centerY() - anchorCenterY);
                                    views[i].setTranslationX(focusRects[i].centerX() - anchorCenterX);
                                    views[i].setTranslationY(focusRects[i].centerY() - anchorCenterY);
                                    views[i].setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, 0);
                }
            }
        }, 0);


        if (onViewInflaterListener != null) {
            onViewInflaterListener.onViewInflated(view);
        }

    }
}
