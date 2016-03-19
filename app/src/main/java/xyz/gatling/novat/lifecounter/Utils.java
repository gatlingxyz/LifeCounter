package xyz.gatling.novat.lifecounter;

import android.app.Activity;
import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by gimmiepepsi on 3/19/16.
 */
public class Utils {

    public static boolean isScreenInPortrait(Context context){
        return getScreenRotation(context) == Surface.ROTATION_0;
    }

    public static int getScreenRotation(Context context){
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Activity.WINDOW_SERVICE);
        return mWindowManager.getDefaultDisplay().getRotation();
    }

}
