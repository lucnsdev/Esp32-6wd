package lucns.robot6wd.views.joystick;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchableField extends RelativeLayout {

    private int xPosition;
    private int yPosition;
    private int centerPosition;
    private int lastAngle;
    private int joystickRadius;
    private final JoystickView joystickView;
    private final double reductionFactor = 0.5d;

    public TouchableField(Context context, JoystickView joystickView) {
        super(context);
        this.joystickView = joystickView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerPosition = (int) (getWidth() / 2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPosition = (int) event.getX();
        yPosition = (int) event.getY();
        double abs = Math.sqrt(((xPosition - centerPosition) * (xPosition - centerPosition) / reductionFactor) + ((yPosition - centerPosition) * (yPosition - centerPosition) / reductionFactor));
        if (abs > joystickRadius) {
            xPosition = (int) ((xPosition - centerPosition) * joystickRadius / abs + centerPosition);
            yPosition = (int) ((yPosition - centerPosition) * joystickRadius / abs + centerPosition);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            xPosition = centerPosition;
            yPosition = centerPosition;
            joystickView.releaseButton(xPosition, yPosition);
            return true;
        }

        joystickView.moveButton(xPosition, yPosition);
        return true;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        xPosition = getWidth() / 2;
        yPosition = getWidth() / 2;
        joystickRadius = getWidth() / 2;
    }

    protected int getAngle() {
        double RAD = 57.2957795;

        if (xPosition > centerPosition) {
            return lastAngle = (int) (Math.atan((double) (yPosition - centerPosition) / (xPosition - centerPosition)) * RAD + 90);
        } else if (xPosition < centerPosition) {
            return lastAngle = (int) (360 + (Math.atan((double) (yPosition - centerPosition) / (xPosition - centerPosition)) * RAD - 90));
        } else {
            if (yPosition <= centerPosition) {
                return lastAngle = 0;
            } else {
                if (lastAngle < 0) return lastAngle = -180;
                else return lastAngle = 180;
            }
        }
    }

    protected int getPower() {
        double baseValue = 1024.0d;
        double value = baseValue * Math.sqrt(((xPosition - centerPosition) * (xPosition - centerPosition) / reductionFactor) + ((yPosition - centerPosition) * (yPosition - centerPosition)) / reductionFactor) / joystickRadius;
        return (int) Math.min(value, baseValue);
    }
}
