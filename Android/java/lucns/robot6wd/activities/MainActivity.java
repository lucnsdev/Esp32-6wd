package lucns.robot6wd.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import lucns.robot6wd.R;
import lucns.robot6wd.activities.controllers.EasyControllerActivity;
import lucns.robot6wd.utils.Notify;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final String[] PERMISSIONS_RUNTIME = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    private void requestPermissions() {
        String[] deniedPermissions = getDeniedPermissions();
        if (deniedPermissions.length > 0) {
            requestPermissions(deniedPermissions, 1234);
            return;
        }
        finish();
        //startActivity(new Intent(this, JoystickControllerActivity.class));
        startActivity(new Intent(this, EasyControllerActivity.class));
        //startActivity(new Intent(this, VirtualControllerActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String[] getDeniedPermissions() {
        List<String> permissions = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        for (String permission : PERMISSIONS_RUNTIME) {
            if (packageManager.checkPermission(permission, packageName) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        for (String permission : PERMISSIONS_RUNTIME) {
            if (packageManager.checkPermission(permission, packageName) != PackageManager.PERMISSION_GRANTED) {
                Notify.showToast(getString(R.string.permission_denied) + "\n" + permission);
                finish();
            }
        }
    }
}