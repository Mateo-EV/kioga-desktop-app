/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javax.swing.SwingWorker;

/**
 *
 * @author intel
 */
public class ServiceWorker extends SwingWorker<Void, Void> {

    public InterfaceVoid cb;

    public interface InterfaceVoid {

        void execute();
    }

    public ServiceWorker(InterfaceVoid cb) {
        this.cb = cb;
    }

    @Override
    protected Void doInBackground() throws Exception {
        cb.execute();
        return null;
    }

    public static void execute(InterfaceVoid cb) {
        new ServiceWorker(cb).execute();
    }

}
