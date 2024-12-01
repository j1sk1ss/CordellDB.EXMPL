package org.cordell.com.cordelldb.threads;

import java.io.IOException;

import org.cordell.com.cordelldb.manager.Manager;


public class SaveThread extends Thread {
    public SaveThread(Manager manager, int delay) {
        this.manager = manager;
        this.delay = delay;
    }

    private final Manager manager;
    private final int delay;
    private Boolean isAlive = true;

    public void run() {
        try {
            manager.load();
            while (isAlive) {
                manager.save();
                Thread.sleep(delay);

                synchronized (manager) {
                    manager.load();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        isAlive = false;
    }
} 
