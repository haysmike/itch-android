package io.alpacafarm.itch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ScreenHeightLayout extends FrameLayout {
    public ScreenHeightLayout(Context context) {
        super(context);
    }

    public ScreenHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenHeightLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
