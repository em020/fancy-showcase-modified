package me.toptas.fancyshowcasesample;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by yzsh-sym on 2017/2/10.
 */

public class StatusBarFucker {

    /**0-none; 1-up; 2-both*/
    private int windowExtend = -1;

    /**0-hide none; 1-hide status bar; 2-hide both*/
    private int hideSysBars = -1;


    private Boolean useDarkNotiIcon = null;


    private Integer statusBarColor = null;


    private Integer navBarColor = null;


    public void setWindowExtend(int windowExtend) {
        this.windowExtend = windowExtend;
    }


    /**
     * 注意，如果需要隐藏状态栏，除了create时设置外，还需要在onWindowFocusChange中，hasFocus true时设置，以使从子activity返回时再次隐藏。
     * 0-hide none;
     * 1-hide status bar;
     * 2-hide both
     * */
    public void setHideSysBars(int hideSysBars) {
        this.hideSysBars = hideSysBars;
    }

    public void setUseDarkNotiIcon(boolean useDarkNotiIcon) {
        this.useDarkNotiIcon = useDarkNotiIcon;
    }

    public void setStatusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public void setNavBarColor(int navBarColor) {
        this.navBarColor = navBarColor;
    }


    public void fuck(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fuckImpl(window);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fuckImpl(final Window window) {

        int origSystemUiVisibility = window.getDecorView().getSystemUiVisibility();

        int systemUiVisibility = origSystemUiVisibility;

        if (windowExtend != -1) {

            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            switch (windowExtend) {

                case 0:
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    break;
                case 1:
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    break;
                case 2:
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    break;
            }
        }


        if (hideSysBars != -1) {

            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            switch (hideSysBars) {

                case 0:
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    break;
                case 1:
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    break;
                case 2:
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    break;
            }
        }


        if (useDarkNotiIcon != null) {

            if (ableToSetDarkStatusBarIcon()) {

                //执行深浅icon切换
                String manu = Build.MANUFACTURER;
                if ("Xiaomi".equals(manu)) {
                    /*setMIUIStatusBarDarkIcon(window, useDarkNotiIcon);// =window.setExtraFlag*/
                    postSetMiuiDarkStatusBarIcon = true;
                } else {
                    if (useDarkNotiIcon) {
                        systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                }

            } else {

                if (useDarkNotiIcon && statusBarColor != null) {
                    statusBarColor = adjustColorForLightStatusBarIcon(statusBarColor);
                }
            }

        }



        if (systemUiVisibility != origSystemUiVisibility) {
            // changed
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }


        if (postSetMiuiDarkStatusBarIcon) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    setMIUIStatusBarDarkIcon(window, useDarkNotiIcon);// =window.setExtraFlag
                }
            });

//            setMIUIStatusBarDarkIcon(window, useDarkNotiIcon);// =window.setExtraFlag
        }


        if (statusBarColor != null || navBarColor != null) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    if (statusBarColor != null) {
                        window.setStatusBarColor(statusBarColor);
                    }
                    if (navBarColor != null) {
                        window.setNavigationBarColor(navBarColor);
                    }
                }
            });

//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            if (statusBarColor != null) {
//                window.setStatusBarColor(statusBarColor);
//            }
//            if (navBarColor != null) {
//                window.setNavigationBarColor(navBarColor);
//            }

        }
        /**
         * 关于post的解释：
         *
         * A=view.setSystemUiVisibility  B=window.setFlags  C=window.setExtraFlags(MIUI)
         * 对于原生系统，通过A来设置状态栏深色图标，A与B连续调用会有冲突
         * 对于MIUI，是通过C来设置状态栏深色图标，C与A连续调用会有冲突
         *
         * 大概是这二者内部都会导致向UI线程任务队列添加某个（共同的？）message。
         * 为避免这个问题，这里用post使这二个方法断开。还需要注意的是，经测试发现，
         * 把window.setFlags放在post中时 效果是完美的，如果把view.setSystemUiVisibility放在post中，
         * 则状态栏窗口会有偶现的、可见的状态变化（颜色 显隐）
         *
         */


    }
    private boolean postSetMiuiDarkStatusBarIcon;



    private static String getSystemProperty(String key) {

        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return value;
    }

    private static boolean ableToSetDarkStatusBarIcon() {
        String manufacturer = Build.MANUFACTURER;
        if ("Xiaomi".equals(manufacturer)) {
            String miuiVersion = getSystemProperty("ro.miui.ui.version.name");
            return isMIUIVersionGreaterThanV6(miuiVersion);
        } else {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        }
    }

    private static boolean isMIUIVersionGreaterThanV6(String version) {
        if (TextUtils.isEmpty(version)) return false;

        if (Pattern.compile("[vV]\\d+").matcher(version).matches()) {
            // V8  V12的格式
            int ver = Integer.parseInt(version.substring(1));
            return ver >= 6;
        } else {
            // 未知格式，那么一定是来自未来的MIUI，所以认为它是大于V6的
            return true;
        }
    }

    public static void main(String[] args) {

        Pattern pattern = Pattern.compile("[vV]\\d+");

        System.out.println(""+pattern.matcher("abc").matches());
        System.out.println(""+pattern.matcher("10").matches());
        System.out.println(""+pattern.matcher("v5a2").matches());
        System.out.println(""+pattern.matcher("v").matches());
        System.out.println(""+pattern.matcher("V").matches());

        System.out.println(""+pattern.matcher("v5").matches());
        System.out.println(""+pattern.matcher("V5").matches());
        System.out.println(""+pattern.matcher("v7").matches());
        System.out.println(""+pattern.matcher("V12").matches());
        System.out.println(""+pattern.matcher("v0").matches());
    }

    private static int adjustColorForLightStatusBarIcon(int input) {

        int ret = input;

        int alpha = Color.alpha(input);

        if (alpha == 0x00) {
            // 全透明
            ret = Color.parseColor("#40000000");

        }

        if (alpha == 0xff) {
            // solid color
            int r = Color.red(input);
            int g = Color.green(input);
            int b = Color.blue(input);

            int adjustment = 0x40;
            int adjustedR = r - adjustment > 0 ? r - adjustment : 0;
            int adjustedG = g - adjustment > 0 ? g - adjustment : 0;
            int adjustedB = b - adjustment > 0 ? b - adjustment : 0;

            ret = Color.rgb(adjustedR, adjustedG, adjustedB);

        }

        return ret;
    }

    private static void setMIUIStatusBarDarkIcon(Window window, boolean useDarkNotiIcon) {

        Class windowClass = window.getClass();

        try {
//            int transparentFlag = 0;
//            Field fieldTransParentStatusBar = layoutParamsClass.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
//            transparentFlag = fieldTransParentStatusBar.getInt(layoutParamsClass);
//            //只需要状态栏透明
//            setExtraFlags.invoke(window, transparentFlag, transparentFlag);
//            //或  状态栏透明且黑色字体
//            setExtraFlags.invoke(window, transparentFlag | EXTRA_FLAG_STATUS_BAR_DARK_MODE, transparentFlag | EXTRA_FLAG_STATUS_BAR_DARK_MODE);


            Class layoutParamsClass = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Field field = layoutParamsClass.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");

            int EXTRA_FLAG_STATUS_BAR_DARK_MODE = field.getInt(layoutParamsClass);

            Method setExtraFlags = windowClass.getMethod("setExtraFlags", int.class, int.class);

            setExtraFlags.invoke(window, useDarkNotiIcon ? EXTRA_FLAG_STATUS_BAR_DARK_MODE : 0, EXTRA_FLAG_STATUS_BAR_DARK_MODE);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
