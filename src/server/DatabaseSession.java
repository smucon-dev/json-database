package server;

import com.google.gson.Gson;
import shared.DatabaseRequest;
import shared.DatabaseResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class DatabaseSession implements Callable<DatabaseSession> {

    private final Socket socket;
    private final JsonDatabase db;
    private final DatabaseServer server;

    public DatabaseSession(Socket socket, DatabaseServer server) {
        this.socket = socket;
        this.db = server.getDataBase();
        this.server = server;
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public DatabaseSession call() {

        Gson gson = new Gson();

        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            String clientRequest = input.readUTF();
            System.out.println("Received: " + clientRequest);
            DatabaseRequest dbRequest = gson.fromJson(clientRequest, DatabaseRequest.class);

            String response;
            var stopServer = false;
            switch (dbRequest.getType()) {
                case "exit":
                    response = gson.toJson(new DatabaseResponse("OK"));
                    stopServer = true;
                    break;
                case "set":
                    response = gson.toJson(new SetOperation(db, dbRequest).execute());
                    break;
                case "get":
                    response = gson.toJson(new GetOperation(db, dbRequest).execute());
                    break;
                case "delete":
                    response = gson.toJson(new DeleteOperation(db, dbRequest).execute());
                    break;
                default:
                    response = gson.toJson(new DatabaseResponse("ERROR", null, "Unknown action: " + dbRequest.getType()));
            }
            output.writeUTF(response);
            System.out.println("Sent: " + response);
            if (stopServer) {
                socket.close();
                server.stopServer();
            }

        } catch (IOException e) {
            System.out.println("Client error!" + e);
        }

        return this;
    }

}
