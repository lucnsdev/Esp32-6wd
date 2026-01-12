package lucns.robot6wd.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import lucns.robot6wd.R;
import lucns.robot6wd.utils.Notify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FrameView extends ImageView {

    public static final int ASPECT_RATIO_4_3 = 0;
    public static final int ASPECT_RATIO_16_9 = 1;
    private int aspectRatio = ASPECT_RATIO_4_3;
    private int width, height;

    public FrameView(Context context) {
        super(context);
    }

    public void resizeHeightAutomatically(int aspectRatio) {
        this.aspectRatio = aspectRatio;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                resizeHeightAutomatically();
            }
        });
    }

    public void resizeHeightAutomatically() {
        if (width > 0 && height > 0) {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = width;
            params.height = height;
            setLayoutParams(params);
            return;
        }
        int width = getWidth();
        int height = getHeight();
        switch (aspectRatio) {
            case ASPECT_RATIO_16_9:
                if (width > 0) {
                    height = (int) (width / (16f / 9f));
                } else {
                    width = (int) (height / (9f / 16f));
                }
                break;
            case ASPECT_RATIO_4_3:
                if (width > 0) {
                    height = (int) (width / (4f / 3f));
                } else {
                    width = (int) (height / (3f / 4f));
                }
                break;
        }
        if (width == 0 || height == 0) {
            return;
        }
        // Log.d("Lucas", width + "x" + height);
        this.width = width;
        this.height = height;

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);

        Bitmap bitmap = getBitmap(R.drawable.videocam_48px);
        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        Paint paint = new Paint();
        paint.setColor(Color.argb(32, 255, 255, 255));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        canvas.drawBitmap(bitmap, (width / 2f) - (bitmap.getWidth() / 2f), (height / 2f) - (bitmap.getHeight() / 2f), paint);
        setImageBitmap(bitmap2);
        bitmap.recycle();
    }

    public void putFrame(Bitmap frame) {
        if (frame == null || width == 0 || height == 0) return;
        setImageBitmap(frame);
    }

    public FrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Bitmap getBitmap(int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId, getContext().getTheme());
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    private Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
