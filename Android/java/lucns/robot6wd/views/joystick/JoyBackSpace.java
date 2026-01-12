package lucns.robot6wd.views.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class JoyBackSpace extends View {

    private final Paint paint, paintRings;

    public JoyBackSpace(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(Color.GRAY);

        paintRings = new Paint();
        paintRings.setColor(Color.argb(128, 128, 128, 128));
        paintRings.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = getWidth();

        paintRings.setStrokeWidth(10);
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 10, paintRings);
        /*
        paintRings.setStrokeWidth(1);
        canvas.drawCircle(size / 2f, size / 2f, size / 7f, paint);
        canvas.drawCircle(size / 2f, size / 2f, size / 3f, paintRings);
        canvas.drawCircle(size / 2f, size / 2f, size / 6f, paintRings);
        canvas.drawLine(0, getHeight() / 2f, getWidth(), getHeight() / 2f, paint);
        canvas.drawLine(getWidth() / 2f, 0, getWidth() / 2f, getHeight(), paint);
         */
    }
}
