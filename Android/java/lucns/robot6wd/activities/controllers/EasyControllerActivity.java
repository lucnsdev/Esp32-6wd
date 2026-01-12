package lucns.robot6wd.activities.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import lucns.robot6wd.R;
import lucns.robot6wd.activities.BaseActivity;
import lucns.robot6wd.services.MainService;
import lucns.robot6wd.services.ServiceController;
import lucns.robot6wd.services.Transceiver;
import lucns.robot6wd.utils.Notify;
import lucns.robot6wd.utils.Prefs;
import lucns.robot6wd.utils.Utils;
import lucns.robot6wd.views.FrameView;
import lucns.robot6wd.views.TriangleView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EasyControllerActivity extends BaseActivity {

    private PopupMenu popupMenu;
    private MainService mainService;
    private TextView textRssi, textBattery, textFps, textNetworkVelocity, textTime, textData;
    private ImageButton buttonConnection;
    private TextView textTx, textRx;
    private int runSpeed, bendSpeed, sideProportion;
    private final int rotationPwm = 1023;
    private FrameView frameView;
    private boolean resized;

    @Override
    public boolean onCreated() {
        setContentView(R.layout.activity_easy_controller);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        textRssi = findViewById(R.id.textRssiValue);
        textBattery = findViewById(R.id.textBatteryValue);
        textData = findViewById(R.id.textData);
        textFps = findViewById(R.id.textFpsValue);
        textNetworkVelocity = findViewById(R.id.textNetworkVelocityValue);
        frameView = findViewById(R.id.imageView);
        frameView.resizeHeightAutomatically(FrameView.ASPECT_RATIO_4_3);
        textTx = findViewById(R.id.textTx);
        textRx = findViewById(R.id.textRx);

        runSpeed = Prefs.getInt("run_speed");
        if (runSpeed < 0) runSpeed = 1023;
        sideProportion = Prefs.getInt("side_proportion");
        if (sideProportion < 0) sideProportion = 90;
        if (sideProportion == 0) bendSpeed = 0;
        else bendSpeed = (int) (runSpeed * (sideProportion) / 100.0d);

        findViewById(R.id.buttonBuzzer).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Utils.vibrate(40);
                        mainService.put(new Transceiver.Command("buzzer", "bz1", true));
                        break;
                    case MotionEvent.ACTION_UP:
                        Utils.vibrate(25);
                        mainService.put(new Transceiver.Command("buzzer", "bz0"));
                        break;
                }
                return false;
            }
        });

        TriangleView buttonUp = findViewById(R.id.buttonUp);
        TriangleView buttonDown = findViewById(R.id.buttonDown);
        TriangleView buttonLeft = findViewById(R.id.buttonLeft);
        TriangleView buttonRight = findViewById(R.id.buttonRight);
        buttonUp.setPosition(TriangleView.Positions.TOP);
        buttonDown.setPosition(TriangleView.Positions.BOTTOM);
        buttonLeft.setPosition(TriangleView.Positions.LEFT);
        buttonRight.setPosition(TriangleView.Positions.RIGHT);

        TriangleView.TouchCallback touchCallback = new TriangleView.TouchCallback() {

            @Override
            public void onTouch(View view, boolean touched) {
                if (touched) Utils.vibrate(40);
                else Utils.vibrate(25);
                String leftCommand, rightCommand;
                boolean persistent = true;
                if (view.getId() == R.id.buttonUp) {
                    if (buttonDown.isTouched()) return;
                    if (touched) {
                        if (buttonLeft.isTouched()) {
                            leftCommand = "lb" + bendSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonRight.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rb" + bendSpeed;
                        } else {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rf" + runSpeed;
                        }
                    } else {
                        if (buttonLeft.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonRight.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rb" + runSpeed;
                        } else {
                            leftCommand = "ld";
                            rightCommand = "rd";
                            persistent = false;
                        }
                    }
                } else if (view.getId() == R.id.buttonDown) {
                    if (buttonUp.isTouched()) return;
                    if (touched) {
                        if (buttonLeft.isTouched()) {
                            leftCommand = "lf" + bendSpeed;
                            rightCommand = "rb" + runSpeed;
                        } else if (buttonRight.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rf" + bendSpeed;
                        } else {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rb" + runSpeed;
                        }
                    } else {
                        if (buttonLeft.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonRight.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rb" + runSpeed;
                        } else {
                            leftCommand = "ld";
                            rightCommand = "rd";
                            persistent = false;
                        }
                    }
                } else if (view.getId() == R.id.buttonLeft) {
                    if (buttonRight.isTouched()) return;
                    if (touched) {
                        if (buttonUp.isTouched()) {
                            leftCommand = "lb" + bendSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonDown.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rf" + bendSpeed;
                        } else {
                            leftCommand = "lb" + rotationPwm;
                            rightCommand = "rf" + rotationPwm;
                        }
                    } else {
                        if (buttonUp.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonDown.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rb" + runSpeed;
                        } else {
                            leftCommand = "ld";
                            rightCommand = "rd";
                            persistent = false;
                        }
                    }
                } else if (view.getId() == R.id.buttonRight) {
                    if (buttonLeft.isTouched()) return;
                    if (touched) {
                        if (buttonUp.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rb" + bendSpeed;
                        } else if (buttonDown.isTouched()) {
                            leftCommand = "lb" + bendSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else {
                            leftCommand = "lf" + rotationPwm;
                            rightCommand = "rb" + rotationPwm;
                        }
                    } else {
                        if (buttonUp.isTouched()) {
                            leftCommand = "lf" + runSpeed;
                            rightCommand = "rf" + runSpeed;
                        } else if (buttonDown.isTouched()) {
                            leftCommand = "lb" + runSpeed;
                            rightCommand = "rb" + runSpeed;
                        } else {
                            leftCommand = "ld";
                            rightCommand = "rd";
                            persistent = false;
                        }
                    }
                } else {
                    return;
                }
                //Log.d("lucas", "command: " + leftCommand + " " + rightCommand);
                mainService.put(new Transceiver.Command("motors", leftCommand + " " + rightCommand, persistent));
            }
        };

        buttonUp.setTouchCallback(touchCallback);
        buttonDown.setTouchCallback(touchCallback);
        buttonLeft.setTouchCallback(touchCallback);
        buttonRight.setTouchCallback(touchCallback);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonMenu) {
                    //MenuItem item = popupMenu.getMenu().findItem(R.id.menu_record);
                    //item.setTitle(frameView.isRecording() ? R.string.stop_record : R.string.start_record);
                    //item.setEnabled(mainService.isConnectedUdp() && Prefs.getBoolean("camera_state"));
                    //item = popupMenu.getMenu().findItem(R.id.menu_change_camera_state);
                    //item.setTitle(Prefs.getBoolean("camera_state") ? R.string.disable_camera : R.string.enable_camera);
                    //item.setEnabled(mainService.isConnectedUdp());
                    //item = popupMenu.getMenu().findItem(R.id.menu_take_picture);
                    //item.setEnabled(frameView.hasFrame());
                    popupMenu.show();
                } else if (v.getId() == R.id.buttonConnection) {
                    if (mainService == null) return;
                    if (mainService.isConnectedOnEspWifi()) {
                        if (mainService.isConnectedUdp()) {
                            mainService.put(new Transceiver.Command("control", "z"));
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mainService.close();
                                }
                            }, 100);
                        } else {
                            mainService.connect();
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                } else if (v.getId() == R.id.buttonWifi) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }
            }
        };
        findViewById(R.id.buttonWifi).setOnClickListener(onClickListener);
        textTime = findViewById(R.id.textTime);
        buttonConnection = findViewById(R.id.buttonConnection);
        buttonConnection.setOnClickListener(onClickListener);
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(onClickListener);

        popupMenu = new PopupMenu(this, buttonMenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_speeds) {
                    showDialogSpeed();
                } else if (item.getItemId() == R.id.menu_lights) {
                    showDialogLights();
                } else if (item.getItemId() == R.id.menu_change_camera_state) {
                    resized = false;
                    if (!mainService.isConnectedUdp()) return true;
                    boolean cameraEnabled = !Prefs.getBoolean("camera_state");
                    Prefs.setBoolean("camera_state", cameraEnabled);
                    Notify.showToast(cameraEnabled ? R.string.enabled : R.string.disabled);
                    if (cameraEnabled) {
                        mainService.enableReceiver();
                        frameView.setVisibility(View.VISIBLE);
                    } else {
                        frameView.setVisibility(View.INVISIBLE);
                        mainService.disableReceiver();
                    }
                    mainService.put(new Transceiver.Command("camera_state", cameraEnabled ? "c1" : "c0"));
                    textFps.setText(R.string.zero);
                    textNetworkVelocity.setText(R.string.zero);
                } else if (item.getItemId() == R.id.menu_controller) {
                    startActivity(new Intent(EasyControllerActivity.this, VirtualControllerActivity.class));
                    finish();
                }
                return true;
            }
        });
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popupMenu.getMenu());

        ServiceController.getInstance(this, new ServiceController.OnServiceAvailableListener() {
            @Override
            public void onAvailable(MainService mainService) {
                EasyControllerActivity.this.mainService = mainService;
                mainService.setCallback(callback);
                buttonConnection.setImageResource(mainService.isConnectedUdp() ? R.drawable.icon_close : R.drawable.icon_reconnect);
                if (mainService.isConnectedUdp()) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    sendSettings();
                } else if (mainService.isConnectedOnEspWifi()) {
                    mainService.connect();
                }
            }
        });
        return true;
    }

    private void showDialogSpeed() {
        Dialog dialog = generateDialog(R.layout.dialog_speed, true);
        dialog.findViewById(R.id.buttonPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView textView = dialog.findViewById(R.id.textPercent);
        TextView textView2 = dialog.findViewById(R.id.textPercent2);
        SeekBar seekBar = dialog.findViewById(R.id.seekbar);
        SeekBar seekBar2 = dialog.findViewById(R.id.seekbar2);

        int percent = (int) (100 * ((double) runSpeed / 1023.0d));
        textView.setText(percent + "%");
        seekBar.setProgress(runSpeed);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runSpeed = progress;
                int percent = (int) (100 * ((double) progress / 1023.0d));
                textView.setText(percent + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Prefs.setInt("run_speed", seekBar.getProgress());
            }
        });
        textView2.setText(sideProportion + "%");
        seekBar2.setProgress(sideProportion);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sideProportion = progress;
                if (sideProportion == 0) bendSpeed = 0;
                else bendSpeed = (int) (runSpeed * (sideProportion) / 100.0d);
                textView2.setText(sideProportion + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Prefs.setInt("side_proportion", seekBar.getProgress());
            }
        });

        dialog.show();
    }

    private void showDialogLights() {
        Dialog dialog = generateDialog(R.layout.dialog_lights, true);
        dialog.findViewById(R.id.buttonPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView textView = dialog.findViewById(R.id.textPercent);
        SeekBar seekBar = dialog.findViewById(R.id.seekbar);
        int ledFront = Prefs.getInt("led_front");
        if (ledFront < 0) ledFront = 1023;
        int percent = (int) (100 * ((double) ledFront / 1023.0d));
        textView.setText(percent + "%");
        seekBar.setProgress(ledFront);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int percent = (int) (100 * ((double) progress / 1023.0d));
                textView.setText(percent + "%");
                if (mainService == null || !mainService.isConnectedUdp()) return;
                mainService.put(new Transceiver.Command("led_front", "g" + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Prefs.setInt("led_front", seekBar.getProgress());
            }
        });

        dialog.show();
    }

    @Override
    public void onResumed() {
        super.onResumed();
        if (mainService != null) {
            buttonConnection.setImageResource(mainService.isConnectedUdp() ? R.drawable.icon_close : R.drawable.icon_reconnect);
            if (mainService.isConnectedOnEspWifi() && !mainService.isConnectedUdp()) {
                mainService.connect();
            }
        }
    }

    @Override
    public void onPaused() {
        super.onPaused();
        popupMenu.dismiss();
        if (mainService != null && mainService.isConnectedUdp()) {
            mainService.clear();
            mainService.put(new Transceiver.Command("motors", "ld rd", false));
        }
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private final MainService.Callback callback = new MainService.Callback() {

        long startTime = System.currentTimeMillis();
        int fps, kbps;
        int minPercentage = 100;

        final Handler hT = new Handler(Looper.getMainLooper());
        final Runnable rT = new Runnable() {
            @Override
            public void run() {
                textTx.setTextColor(getColor(R.color.white_2));
            }
        };

        final Handler hR = new Handler(Looper.getMainLooper());
        final Runnable rR = new Runnable() {
            @Override
            public void run() {
                textRx.setTextColor(getColor(R.color.white_2));
            }
        };

        @Override
        public void onSent() {
            hT.removeCallbacks(rT);
            hT.postDelayed(rT, 100);
            textTx.setTextColor(getColor(R.color.main));
        }

        @Override
        public void onReceive(byte[] data, int length) {
            hR.removeCallbacks(rR);
            hR.postDelayed(rR, 250);
            textRx.setTextColor(getColor(R.color.main));
            if (data != null) {
                if (length < 12) {
                    String value = new String(data, 0, length);
                    int raw = Integer.parseInt(value.substring(value.indexOf(" ") + 1)) + 227;
                    // Log.d("lucas", "raw: " + raw);
                    int percentage;
                    int max = 2606; // (4.26v = 2643) (4.2volts = 2606)
                    int min = 2234; // (3.6v = 2234) (3.7volts = 581)
                    if (raw <= min) {
                        percentage = 0;
                    } else if (raw >= max) {
                        percentage = 100;
                    } else {
                        percentage = (int) ((((float) (raw - min)) / ((float) (max - min))) * 100);
                    }
                    if (percentage < minPercentage) minPercentage = percentage;
                    textBattery.setText(minPercentage + "%");
                    if (percentage >= 50) {
                        textBattery.setTextColor(getColor(R.color.green));
                    } else if (percentage > 24) {
                        textBattery.setTextColor(getColor(R.color.orange));
                    } else {
                        textBattery.setTextColor(getColor(R.color.red));
                    }
                    return;
                }
                fps++;
                kbps += data.length;
                long time = System.currentTimeMillis();
                if (time - startTime >= 1000) {
                    textFps.setText(String.valueOf(fps));
                    if (kbps > 0) {
                        //if (fps > 0) textData.setText(String.valueOf((kbps / 1024) / fps));
                        textNetworkVelocity.setText(String.valueOf(kbps / 1024));
                    }
                    startTime = time;
                    fps = 0;
                    kbps = 0;
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, length);
                if (bitmap == null) {
                    Log.d("lucas", "Bitmap is null!");
                    return;
                }
                textData.setText(bitmap.getWidth() + "x" + bitmap.getHeight());
                if (bitmap.getWidth() == 640) textData.setTextColor(getColor(R.color.red));
                else textData.setTextColor(getColor(R.color.white));

                if (!resized && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
                    resized = true;
                    frameView.resizeHeightAutomatically();
                }
                frameView.putFrame(bitmap);
            }
        }

        private double resizeNumber(double number) {
            int i = (int) (number * 100);
            return (double) i / 100;
        }

        @Override
        public void onRssiChanged(int value) {
            textRssi.setText(String.valueOf(value));
            if (value <= -90) {
                textRssi.setTextColor(getColor(R.color.red));
            } else if (value <= -80) {
                textRssi.setTextColor(getColor(R.color.orange));
            } else {
                textRssi.setTextColor(getColor(R.color.green));
            }
        }

        @Override
        public void onSocketStateChanged(boolean connected) {
            resized = false;
            Utils.vibrate();
            Notify.showToast(connected ? R.string.connected : R.string.disconnected);
            buttonConnection.setImageResource(connected ? R.drawable.icon_close : R.drawable.icon_reconnect);
            textNetworkVelocity.setText(R.string.zero);
            textFps.setText(R.string.zero);
            //textRssi.setText(R.string.zero_db);
            textRssi.setTextColor(getColor(R.color.red));

            if (connected) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                sendSettings();
            }
        }
    };

    private void sendSettings() {
        int ledFront = Prefs.getInt("led_front");
        if (ledFront < 0) ledFront = 1023;
        /*
        boolean cameraEnabled = Prefs.getBoolean("camera_state");
        if (cameraEnabled) {
            resized = false;
            mainService.enableReceiver();
            frameView.setVisibility(View.VISIBLE);
        } else {
            mainService.disableReceiver();
        }
         */
        mainService.put(new Transceiver.Command("led_front", "g" + ledFront));
        //mainService.put(new Transceiver.Command("camera_state", cameraEnabled ? "c1" : "c0"));
        mainService.put(new Transceiver.Command("camera_state", "c1"));
    }
}