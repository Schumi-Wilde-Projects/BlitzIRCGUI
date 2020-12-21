package org.schumiwildeprojects;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.schumiwildeprojects.states.ConnectionState;
import org.schumiwildeprojects.states.MainWindowState;

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

    private Dialog<ButtonType> dialog;
    private ConnectionState connectionState;

    public void initialize() {
        exitButton.setOnAction(actionEvent -> System.exit(0));

        connectButton.setOnAction(actionEvent -> {
            ConnectToServerTask task = new ConnectToServerTask();
            Thread thread = new Thread(task);
            thread.setDaemon(true);

            dialog = new Dialog<>();
            dialog.initOwner(anchorPane.getScene().getWindow());
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add("style.css");
            dialog.setTitle("Łączenie...");
            dialog.setHeaderText("Proszę czekać...");
            dialog.setContentText("Trwa łączenie z serwerem...");

            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");

            thread.start();
            Optional<ButtonType> result = dialog.showAndWait();
            if (connectionState == ConnectionState.SUCCESSFUL) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.close();
                return;
            }
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                Alert connectionCanceledAlert = new Alert(Alert.AlertType.WARNING);
                DialogPane pane = connectionCanceledAlert.getDialogPane();
                pane.getStylesheets().add("style.css");
                connectionCanceledAlert.setTitle("Uwaga");
                connectionCanceledAlert.setHeaderText("Przerwano połączenie");
                connectionCanceledAlert.setContentText("Przerwano połączenie z serwerem.");
                dialog.close();
                connectionCanceledAlert.show();
            }
        });
    }

    private class ConnectToServerTask extends Task<Void> {
        @Override
        protected Void call() {
            try {
                if (!getServerName().equals(" ") && !getNick().equals(" ") && !getLogin().equals(" ") && !getFullName().equals(" ") &&
                        !getChannelName().equals(" ")) {
                    connect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void connect() throws IOException {
        connectionState = App.getInstance().connectToServer(getServerName(), getNick(), getLogin(), getFullName(), getChannelName(),
                getPassword());
        if (connectionState == ConnectionState.SUCCESSFUL) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            Platform.runLater(() -> {
                App.openWindow(loader, anchorPane.getScene(), "Komunikacja na kanale", 1280, 720);
                try {
                    App.getInstance().changeState(new MainWindowState());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.close();
            });
        } else if (connectionState == ConnectionState.SERVER_TIMEOUT) {
            Platform.runLater(() -> {
                dialog.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("style.css");
                alert.setTitle("Błąd");
                alert.setHeaderText("Błąd");
                alert.setContentText("Nieprawidłowa nazwa serwera. Spróbuj ponownie.");
                alert.show();
            });
        } else if (connectionState == ConnectionState.SERVER_NAME_EMPTY) {
            Platform.runLater(() -> {
                dialog.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("style.css");
                alert.setHeaderText("Błąd");
                alert.setContentText("Brak nazwy serwera. Wypełnij pole serwera.");
                alert.show();
            });
        } else if (connectionState == ConnectionState.FIELDS_NOT_FILLED) {
            Platform.runLater(() -> {
                dialog.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("style.css");
                alert.setHeaderText("Błąd");
                alert.setContentText("Jedno lub więcej pól jest niewypełnione.");
                alert.show();
            });
        }
    }

    public String getServerName() {
        return serverTextField.getText();
    }

    public String getNick() {
        return nickTextField.getText();
    }

    public String getLogin() {
        return loginTextField.getText();
    }

    public String getFullName() {
        return fullNameTextField.getText();
    }

    public String getChannelName() {
        return channelNameTextField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
