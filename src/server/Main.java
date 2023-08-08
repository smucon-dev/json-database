package server;
public class Main {

    private static final int MAX_WORKERS = 4;

    public static void main(String[] args) throws InterruptedException {

        DatabaseServer server = new DatabaseServer();
        server.start();

    }

}

