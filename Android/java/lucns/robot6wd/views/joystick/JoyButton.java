package lucns.robot6wd.views.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class JoyButton extends View {

    private final Paint paint, paint2;
    private final int stroke = 22;

    public JoyButton(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(stroke);
        setAlpha(0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = getWidth();
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 1, paint);
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - (stroke / 2f), paint2);
    }
}
