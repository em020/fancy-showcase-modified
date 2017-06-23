package me.toptas.fancyshowcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.List;


public class FancyImageViewV2 extends AppCompatImageView {

    private static final int ANIM_COUNTER_MAX = 20;

    private Bitmap mBitmap;// rename to backgroundBitmap
    private Paint mBackgroundPaint;
    private Paint mErasePaint;
    private Paint mBorderPaint;
    private Path mPath;

    private int mAnimCounter = 0;
    private int mStep = 1;

    private int mBackgroundColor = Color.TRANSPARENT;   // per FancyImageView
    private boolean mAnimationEnabled;                  // per FancyImageView
    private double mAnimMoveFactor = 1;                 // per FancyImageView

    private List<FocusDescriptor> focusDescriptors;


    public void setMaskLayerColor(int color) {
        mBackgroundColor = color;
    }

    public void setAnimationEnable(boolean enable) {
        mAnimationEnabled = enable;
    }

    public void setFocusDescriptors(List<FocusDescriptor> focusDescriptors) {
        this.focusDescriptors = focusDescriptors;
    }


    public FancyImageViewV2(Context context) {
        super(context);
        init();
    }

    public FancyImageViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FancyImageViewV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }

        setWillNotDraw(false);

        setBackgroundColor(Color.TRANSPARENT);

        mPath = new Path();

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAlpha(0xFF);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setAlpha(0xFF);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(mBackgroundColor);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBackgroundPaint);


        if (focusDescriptors != null) {
            for (FocusDescriptor fd : focusDescriptors) {
                if (fd.focusShape.equals(FocusShape.CIRCLE)) {
                    drawCircle(canvas, fd);
                } else {
                    drawRoundedRectangle(canvas, fd);
                }
            }
        }


        if (mAnimationEnabled) {
            if (mAnimCounter == ANIM_COUNTER_MAX) {
                mStep = -1;
            } else if (mAnimCounter == 0) {
                mStep = 1;
            }
            mAnimCounter = mAnimCounter + mStep;
            postInvalidate();
        }
    }


    private void drawCircle(Canvas canvas, FocusDescriptor fd) {

        float radius = (float) (fd.circleRadius + fd.circlePadding + mAnimCounter * mAnimMoveFactor);

        if (fd.noHole) {
            // if no hole, dont make hole
        } else {
            canvas.drawCircle(fd.centerX, fd.centerY, radius, mErasePaint);
        }

        if (fd.focusBorderSize > 0) {
            mBorderPaint.setColor(fd.focusBorderColor);
            mBorderPaint.setStrokeWidth(fd.focusBorderSize);
            mPath.reset();
            mPath.moveTo(fd.centerX, fd.centerY);
            mPath.addCircle(fd.centerX, fd.centerY, radius, Path.Direction.CW);
            canvas.drawPath(mPath, mBorderPaint);
        }

    }


    private void drawRoundedRectangle(Canvas canvas, FocusDescriptor fd) {

        float left = (float) (fd.centerX - fd.rectWidth / 2d - fd.roundRectPaddingLeft - mAnimCounter * mAnimMoveFactor);
        float right = (float) (fd.centerX + fd.rectWidth / 2d + fd.roundRectPaddingRight + mAnimCounter * mAnimMoveFactor);
        float top = (float) (fd.centerY - fd.rectHeight / 2d - fd.roundRectPaddingTop - mAnimCounter * mAnimMoveFactor);
        float bottom = (float) (fd.centerY + fd.rectHeight / 2d + fd.roundRectPaddingBottom + mAnimCounter * mAnimMoveFactor);

        RectF rectF = new RectF();
        rectF.set(left, top, right, bottom);

        if (fd.noHole) {
            // if no hole, dont make hole
        } else {
            canvas.drawRoundRect(rectF, fd.roundRectRadius, fd.roundRectRadius, mErasePaint);
        }

        if (fd.focusBorderSize > 0) {
            mBorderPaint.setColor(fd.focusBorderColor);
            mBorderPaint.setStrokeWidth(fd.focusBorderSize);
            mPath.reset();
            mPath.moveTo(fd.centerX, fd.centerY);
            mPath.addRoundRect(rectF, fd.roundRectRadius, fd.roundRectRadius, Path.Direction.CW);
            canvas.drawPath(mPath, mBorderPaint);
        }
    }
}
