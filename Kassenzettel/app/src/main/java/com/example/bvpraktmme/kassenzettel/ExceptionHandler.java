package com.example.bvpraktmme.kassenzettel;

/* to catch all exceptions while running and handle them

*/

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String EXCEPTION_MESSAGE = "Something went wrong! The App started again.";
    private Activity activity;
    public ExceptionHandler(Activity activity) {
        this.activity = activity;

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        handleException(EXCEPTION_MESSAGE, activity);
    }

    /**
     * Method to handle Exceptions globally and to show a Toast popup
     * @param message Exception message to show on Toast popup
     * @param activity the activity, which the exception occurred
     */
    public static void handleException (final String message,final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        try
        {
            Thread.sleep(4000); // Let the Toast display before app will get shutdown
        }
        catch (InterruptedException ex)
        {
            // Ignored.
        }
        Intent mainActivity = new Intent(activity, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        activity.startActivity(mainActivity);
        // make sure we die, otherwise the app will hang ...
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
