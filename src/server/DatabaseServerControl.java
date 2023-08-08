package server;

public class DatabaseServerControl {

    public volatile boolean stopServer = false;
    public volatile int activeTasks;

    public DatabaseServerControl(int maxTasks) {
        activeTasks = maxTasks;
    }

}
