package org.cordell.com.cordelldb.threads;

import java.io.IOException;

import org.cordell.com.cordelldb.manager.Manager;

public class SaveThread extends Thread {
    public SaveThread(Manager manager, int delay) {
        this.manager = manager;
        this.delay = delay;
    }

    private Manager manager;
    private int delay;

    public void run() {
        try {
            while (true) {
                manager.save();
                Thread.sleep(delay);

                synchronized (manager) {
                    manager.load();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
