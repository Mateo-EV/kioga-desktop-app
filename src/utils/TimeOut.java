/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author intel
 */
public class TimeOut {
    static public interface Method {
        void start();
    }
    
    static public void set(Method method, int ms) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                method.start();
            }
        };
        
        timer.schedule(task, ms);
    }
}
