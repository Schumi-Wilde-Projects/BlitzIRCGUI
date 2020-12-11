package org.schumiwildeprojects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginWindow {
    @FXML
    private TextField serverTextField;
    @FXML
    private TextField nickTextField;
    @FXML
    private TextField loginTextField;
    @FXML
    private TextField fullNameTextField;
    @FXML
    private TextField channelNameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;
    @FXML
    private Button exitButton;

    @FXML
    private AnchorPane anchorPane;

    public void initialize() {
        connectButton.setOnAction(actionEvent -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(anchorPane.getScene().getWindow());
            dialog.setTitle("Łączenie...");
            dialog.setHeaderText("Proszę czekać...");
            dialog.setContentText("Trwa łączenie z serwerem...");

            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                Alert connectionCanceledAlert = new Alert(Alert.AlertType.WARNING);
                connectionCanceledAlert.setTitle("Uwaga");
                connectionCanceledAlert.setHeaderText("Przerwano połączenie");
                connectionCanceledAlert.setContentText("Przerwano połączenie z serwerem.");

                Optional<ButtonType> okResult = connectionCanceledAlert.showAndWait();
                if (okResult.isPresent() && okResult.get() == ButtonType.OK) {
                    connectionCanceledAlert.close();
                    dialog.close();
                }
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            App.openWindow(loader, anchorPane.getScene(), "Komunikacja na kanale", 1280, 720);
        });

        exitButton.setOnAction(actionEvent -> System.exit(0));
    }
}
