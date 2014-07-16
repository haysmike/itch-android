package io.alpacafarm.itch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectImageView extends ImageView {
    private static final int ASPECT_WIDTH = 16;
    private static final int ASPECT_HEIGHT = 9;

    public AspectImageView(Context context) {
        super(context);
    }

    public AspectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) * ASPECT_HEIGHT / ASPECT_WIDTH;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY));
    }
}
