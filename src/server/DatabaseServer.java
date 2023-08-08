package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DatabaseServer {

    String dbPath = "src/server/data/db.json";

    private static final int MAX_WORKERS = 4;
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 23456;

    private final DatabaseServerControl control = new DatabaseServerControl(MAX_WORKERS);
    ExecutorService executor = Executors.newCachedThreadPool();

    private final JsonDatabase db = new JsonDatabase(Path.of(dbPath).toAbsolutePath().toString());

    private final List<Future<DatabaseSession>> sessions = new ArrayList<>();

    private ServerSocket server = null;
    Socket currentSocket;


    public void stopServer()  {
        if (server != null) {
            try {
                if (!currentSocket.isClosed()) {
                    currentSocket.close();
                }
                if (!server.isClosed()) {
                    server.close();
                }
            } catch (IOException e) {
                // ignore exception from socket blocked by accept
                System.out.println("MyException " + e);
            }
        }
        control.stopServer = true;
    }

    public boolean isStopped() {
        return control.stopServer;
    }

    public JsonDatabase getDataBase() {
        return db;
    }

    public void deregisterDatabaseTask() {
        control.activeTasks--;
    }

    public void start()  {

        System.out.println("Server started!");

        try {
            server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
            while (!isStopped()) {
                currentSocket = server.accept();
                Future<DatabaseSession> session = executor.submit(new DatabaseSession(currentSocket, this));
                sessions.add(session);
            }

            sessions.forEach(session -> {
                if (!session.isDone()) {
                    try {
                        session.get().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (IOException e) {
            System.err.println(e);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        executor.shutdownNow();

    }

}
