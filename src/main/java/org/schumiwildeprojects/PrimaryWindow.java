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
