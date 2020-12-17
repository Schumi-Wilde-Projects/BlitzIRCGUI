package org.schumiwildeprojects;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PrimaryWindow {
    @FXML
    private TextArea messageTextArea;
    @FXML
    private TextField messageTextField;
    @FXML
    private ListView userListView;
    @FXML
    private Button changeChannelButton;
    @FXML
    private Button exitButton;

    @FXML
    private BorderPane borderPane;

    private class Server implements Runnable {
        String line;

        public String readLines() throws Exception {
            return (App.getBufferedReader().readLine());
        }

        @Override
        public void run() {
            try {
                while ((line = readLines()) != null) {
                    if (line.toLowerCase().startsWith("ping ")) {
                        try {
                            App.getBufferedWriter().write("PONG " + line.substring(5) + "\r\n");
                            App.getBufferedWriter().write("PRIVMSG" + App.currentChannel + ": I got pinged!");
                            App.getBufferedWriter().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (line.split(" ")[1].toLowerCase().equals("join")) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        messageTextArea.setEditable(false);
        changeChannelButton.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginWindow.fxml"));
            App.openWindow(loader, borderPane.getScene(), "Logowanie", 420, 400);
        });

        exitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });
    }
}
