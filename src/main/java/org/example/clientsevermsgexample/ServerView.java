package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private VBox clientvbox_messages; // Messages received from client

    @FXML
    private VBox servervbox_messages; // Messages sent by server

    @FXML
    private Button serverbutton_send;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public void initialize() {
        new Thread(this::startServer).start();

        serverbutton_send.setOnAction(event -> {
            String message = "This is a server message"; // Change as per your need
            sendMessageToClient(message);  // Send message to client
        });
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(6666); // Listen on port 6666
            clientSocket = serverSocket.accept(); // Wait for a client connection

            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());

            // Listen for incoming messages from the client
            new Thread(() -> {
                try {
                    while (true) {
                        String messageFromClient = dis.readUTF();
                        Platform.runLater(() -> {
                            // Display client's message in the server message box
                            clientvbox_messages.getChildren().add(new Label("Client: " + messageFromClient));
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToClient(String message) {
        try {
            dos.writeUTF(message); // Send message to client
            dos.flush();
            Platform.runLater(() -> {
                // Display server's message in server message box
                servervbox_messages.getChildren().add(new Label("Server: " + message));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

