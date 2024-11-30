package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientView {

    @FXML
    private TextField clienttf_message;  // TextField for client message input

    @FXML
    private Button clientbutton_send;  // Button to send the message

    @FXML
    private VBox clientvbox_messages; // Messages sent by client

    @FXML
    private VBox servervbox_messages; // Messages received from server

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public void initialize() {
        connectToServer();

        clientbutton_send.setOnAction(event -> {
            String message = clienttf_message.getText();  // Get the message from the TextField
            if (!message.isEmpty()) {
                sendMessageToServer(message);  // Send message to server
                clienttf_message.clear();  // Clear the message input field
            }
        });
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 6666); // Connect to server on localhost
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Listen for incoming messages from the server
            new Thread(() -> {
                try {
                    while (true) {
                        String messageFromServer = dis.readUTF();
                        Platform.runLater(() -> {
                            // Display server message in server's message box
                            servervbox_messages.getChildren().add(new Label("Server: " + messageFromServer));
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

    private void sendMessageToServer(String message) {
        try {
            dos.writeUTF(message); // Send message to server
            dos.flush();
            Platform.runLater(() -> {
                // Display client's message in the client message box
                clientvbox_messages.getChildren().add(new Label("Client: " + message));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
