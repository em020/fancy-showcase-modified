package me.toptas.fancyshowcase;

import android.view.View;

import java.util.List;

/**
 * Created by yzsh-sym on 2017/6/21.
 */

public interface OnViewInflateListenerV3 {
    void onViewInflated(View view, List<FocusDescriptor> focusDescriptorList);
}
