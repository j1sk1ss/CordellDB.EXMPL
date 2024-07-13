package org.cordell.com.cordelldb.manager;

import java.util.List;


public class Cluster {
    public Cluster(List<Manager> managers) {
        this.managers = managers;
    }

    private final List<Manager> managers;

    public void link2Cluster(Manager manager) {
        managers.add(manager);
    }

    public void unlinkFromCluster(Manager manager) {
        managers.remove(manager);
    }
}
