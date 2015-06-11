package Utility;

import java.util.ArrayList;

public class Watch {

    private static long _start;
    private static ArrayList<Long> laps =  new ArrayList<>();

    public static void start(){
        _start = System.currentTimeMillis();
    }

    public static void lapBegin()
    {
        laps.add(System.currentTimeMillis());
    }

    public static void lapStop(String info){
        long l = laps.remove(laps.size()-1);
        log(info, getElapsedTime(l));
    }

    public static void stop(String info){
        log(info, getElapsedTime(_start));
    }

    private static void log(String msg, double elapsedTime){
        System.out.printf("###### %.3f seconds eplapsed in  %s ######\n", elapsedTime, msg);
    }

    private static double getElapsedTime(long timeStarted)
    {
        return (System.currentTimeMillis() - timeStarted) / (double)1000;
    }
}
