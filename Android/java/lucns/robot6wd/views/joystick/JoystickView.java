package lucns.robot6wd.views.joystick;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class JoystickView extends RelativeLayout {

    public interface OnPositionChangedListener {
        void onPositionChangedListener(int angle, int power);
    }

    private TouchableField touchableField;
    private JoyButton joyButton;
    private JoyBackSpace joyBackSpace;
    private int joyButtonSize, touchableFieldSize;
    private OnPositionChangedListener onPositionChangedListener;

    private int lastAngle = -1;
    private int lastPower = -1;

    public JoystickView(Context context) {
        super(context);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    protected void moveButton(int x, int y) {
        if (joyButton == null) return;
        int discount = (int) ((joyButtonSize / 2f) - ((getWidth() - touchableFieldSize) / 2f));
        joyButton.setX(x - discount);
        joyButton.setY(y - discount);
        if (onPositionChangedListener != null) {
            int angle = touchableField.getAngle();
            int power = touchableField.getPower();
            if (angle != lastAngle || power != lastPower) {
                onPositionChangedListener.onPositionChangedListener(angle, power);
                lastAngle = angle;
                lastPower = power;
            }
        }
    }

    protected void releaseButton(int x, int y) {
        if (joyButton == null) return;
        int discount = (int) ((joyButtonSize / 2f) - ((getWidth() - touchableFieldSize) / 2f));
        ViewPropertyAnimator anim = joyButton.animate();
        anim.setInterpolator(new DecelerateInterpolator());
        //anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(200);
        anim.translationX(x - discount);
        anim.translationY(y - discount);
        anim.start();
        if (onPositionChangedListener != null) {
            onPositionChangedListener.onPositionChangedListener(0, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (touchableField == null) {
            // setBackgroundColor(Color.RED);
            touchableFieldSize = (int) (getWidth() / 1.5f);
            LayoutParams params = new LayoutParams(touchableFieldSize, touchableFieldSize);
            touchableField = new TouchableField(getContext(), this);
            // touchableField.setBackgroundColor(Color.BLUE);
            addView(touchableField);
            touchableField.setLayoutParams(params);
            touchableField.setX((getWidth() / 2f) - (touchableFieldSize / 2f));
            touchableField.setY((getHeight() / 2f) - (touchableFieldSize / 2f));
        }
        if (joyBackSpace == null) {
            int joyBackSpaceSize = (int) (getWidth() / 1.5f);
            LayoutParams params = new LayoutParams(joyBackSpaceSize, joyBackSpaceSize);
            joyBackSpace = new JoyBackSpace(getContext());
            joyBackSpace.setClickable(false);
            joyBackSpace.setFocusable(false);
            joyBackSpace.setEnabled(false);
            joyBackSpace.setFocusable(false);
            addView(joyBackSpace, params);
            joyBackSpace.setLayoutParams(params);
            joyBackSpace.setX((getWidth() / 2f) - (joyBackSpaceSize / 2f));
            joyBackSpace.setY((getHeight() / 2f) - (joyBackSpaceSize / 2f));
        }
        joyButtonSize = (int) (getWidth() / 3f);
        if (joyButton == null) {
            LayoutParams params = new LayoutParams(joyButtonSize, joyButtonSize);
            joyButton = new JoyButton(getContext());
            joyButton.setClickable(false);
            joyButton.setFocusable(false);
            joyButton.setEnabled(false);
            joyButton.setFocusable(false);
            addView(joyButton, params);
            joyButton.setLayoutParams(params);
            joyButton.setY((getHeight() / 2f) - (joyButtonSize / 2f));
            joyButton.setX((getWidth() / 2f) - (joyButtonSize / 2f));
        }
    }
}
