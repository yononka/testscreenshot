package com.lakshmi.testscreenshot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    private static final int READ_STORAGE = 0;
    private static final int WRITE_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contextOfApplication = getApplicationContext();

        ScreenshotDetector screenshotDetector =
                new ScreenshotDetector(this, new ScreenshotDetector.OnScreenshotTakenListener() {
                    @Override
                    public void onScreenshotTaken(Uri uri) {
                    }
                    @Override
                    public void onScreenshotDeleted(Uri uri) {
                    }
                });
        screenshotDetector.start();

        setContentView(R.layout.activity_main);

        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_STORAGE);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_STORAGE);

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        }
    }



}
