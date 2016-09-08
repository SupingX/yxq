package util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by Administrator on 2016/9/6.
 */
public class TaskExecutor {
    private static Handler uiHandler = new Handler(Looper.getMainLooper());
    private static Handler workHandler = null;
    private static Looper workLooper = null;

    static  {
        HandlerThread workThread = new HandlerThread("leaf");
        workThread.start();
        workLooper = workThread.getLooper();
        workHandler = new Handler(workLooper);
    }

    public static Handler getUiHandler(){
        return uiHandler;
    }

    public static Handler getWorkHandler(){
        return workHandler;
    }



}
