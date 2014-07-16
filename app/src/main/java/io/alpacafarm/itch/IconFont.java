package io.alpacafarm.itch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IconFont {
    private static Typeface iconTypeface;
    private static BitmapDrawable iconDrawable;

    public static Typeface getIconTypeface(Context context) {
        if (iconTypeface == null) {
            iconTypeface = Typeface.createFromAsset(context.getAssets(), "sosa-regular-webfont.ttf");
        }
        return iconTypeface;
    }

    public static Drawable getIconDrawable(Context context, int size) {
        if (iconDrawable == null) {
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setTypeface(IconFont.getIconTypeface(context));
            paint.setColor(Color.WHITE);
            paint.setTextSize(size);
            canvas.drawText("a", 0, size * 0.7f, paint);
            iconDrawable = new BitmapDrawable(context.getResources(), bitmap);
        }
        return iconDrawable;
    }
}
