package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import shared.DatabaseRequest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        String testDataPath = "src/client/data";

        Gson gson = new Gson();

        System.out.println("Client started!");
        CommandLineArgs clArgs = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(clArgs)
                .build()
                .parse(args);

        String address = "127.0.0.1";
        int port = 23456;

        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            String request;
            if (clArgs.fileInput != null) {
                byte[] fileInput = Files.readAllBytes(Path.of(testDataPath, clArgs.fileInput));
                request = new String(fileInput);
            } else {
                switch (clArgs.type) {
                    case "exit":
                        request = gson.toJson(new DatabaseRequest(clArgs.type));
                        break;
                    case "set":
                        request = gson.toJson(new DatabaseRequest(clArgs.type, clArgs.key, clArgs.value));
                        break;
                    case "get", "delete":
                        request = gson.toJson(new DatabaseRequest(clArgs.type, clArgs.key));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown command: " + clArgs.type);
                }
            }

            if (request != null) {
                output.writeUTF(request);
                System.out.println("Sent: " + request);
            }

            String msgReceived = input.readUTF();
            System.out.println("Received: " + msgReceived);

        } catch (EOFException e ) {

        } catch (IOException e) {
            System.err.println("Error! Server is offline. " + e);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

}
