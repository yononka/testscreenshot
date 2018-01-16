package com.lakshmi.testscreenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotDetector extends FileObserver  implements OnScreenshotTakenListener {
    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots/";

    public interface OnScreenshotTakenListener {
        void onScreenshotTaken(Uri uri);
        void onScreenshotDeleted(Uri uri);
    }

    private boolean deleteScreenshot = false;
    private OnScreenshotTakenListener mListener;
    private String mLastTakenPath;
    private Handler mainThreadHandler;

    public ScreenshotDetector(Context context, OnScreenshotTakenListener listener) {
        super(PATH, FileObserver.CLOSE_WRITE);
        mainThreadHandler = new Handler(context.getMainLooper());
        mListener = listener;
    }

    @Override
    public void onEvent(int event, final String path) {
        System.out.println("!!!!! Screenshot taken: " + "\t" + path);

        if (path == null || event != FileObserver.CLOSE_WRITE)
            System.out.println("Not important");
        else if (mLastTakenPath != null && path.equalsIgnoreCase(mLastTakenPath))
            Log.d(getClass().getSimpleName(), "This event has been observed before.");
        else {
            mLastTakenPath = path;
            final File file = new File(PATH + path);

            if (mListener != null)
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onScreenshotTaken(Uri.fromFile(file));


                            Bitmap bitmap = createBitmapFromUri(Uri.fromFile(file));


                            Bitmap newBitmap = mark(bitmap, "Hello");

                            try {

                                String root = Environment.getExternalStorageDirectory().toString();
                                File newfile = new File(PATH, path);
                                if (newfile.exists())
                                    newfile.delete();
                                try {
                                    FileOutputStream out = new FileOutputStream(newfile);
                                    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                // FileOutputStream fos = new FileOutputStream(new File(Uri.fromFile(file).getPath()));
                                // newBitmap.compress(Bitmap.CompressFormat.PNG, 20, fos);

                                System.out.println("!!! DONE");
                            } catch (Exception e) {
                            e.printStackTrace();
                        }
                        }
                    });
            }

    }


    // Method to draw watermark
    public static Bitmap mark(Bitmap src, String watermark) {

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        canvas.drawText(watermark, 200, 200, paint);

        return result;
    }

    // Method to create bitmap from uri
   private Bitmap createBitmapFromUri(Uri uri) {
       Context applicationContext = MainActivity.getContextOfApplication();

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
/*
    private String getApplicationName() {
        return getApplicationInfo().loadLabel(getPackageManager()).toString();
    }
*/



    public void start() {
        super.startWatching();
    }

    public void stop() {
        super.stopWatching();
    }

    public void deleteScreenshot(boolean deleteScreenshot) {
        this.deleteScreenshot = deleteScreenshot;
    }


    @Override
    public void onScreenshotTaken(Uri uri) {


    }


}