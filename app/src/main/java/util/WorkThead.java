package util;

/**
 * Created by Administrator on 2016/9/6.
 */
public class WorkThead extends Thread {

    @Override
    public void run() {
        try{
            while (! isInterrupted()){
                // 继续工作
            }
        } catch (Exception  e){
                // 退出工作
        }
    }

    public void cancel(){
        interrupt();
    }
}
