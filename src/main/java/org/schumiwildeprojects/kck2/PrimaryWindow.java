package org.schumiwildeprojects.kck2;

import java.io.IOException;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class PrimaryWindow {
    @FXML
    private Button sendMessageButton;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private TextField messageTextField;
    @FXML
    private ListView<String> userListView;
    @FXML
    private Button changeChannelButton;
    @FXML
    private Button exitButton;

    @FXML
    private BorderPane borderPane;

    private final StringBuilder messagesStringBuilder = new StringBuilder();
    private final ObservableList<String> userList = FXCollections.observableArrayList();

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
                    } else if (line.split(" ")[1].equalsIgnoreCase("join")) {
                        messagesStringBuilder.append("Użytkownik ").append(line.substring(1, line.indexOf("!~"))).append(" dołączył do czatu.\n");
                        messageTextArea.setText(messagesStringBuilder.toString());
                        scrollMessagesToBottom();
                        App.getBufferedWriter().write("NAMES " + App.currentChannel + "\r\n");
                        App.getBufferedWriter().flush();
                    } else if (line.split(" ")[1].equalsIgnoreCase("353")) {
                        String[] splits = line.split(" ");
                        Platform.runLater(() -> {
                            userListView.getItems().clear();
                            for (int i = 5; i < splits.length; i++) {
                                int j = 0;
                                while (splits[i].substring(j, j + 1).matches(".*[:@+].*")) {
                                    j++;
                                }
                                userList.add(splits[i].substring(j));
                            }
                            userListView.setItems(userList);
                        });
                    } else if (line.split(" ")[1].equalsIgnoreCase("privmsg")) {
                        String[] splitLine = line.split(" ");
                        String username = line.substring(1, line.indexOf("!~"));
                        StringBuilder lineToAdd = new StringBuilder(splitLine[3].substring(1));
                        for (int i = 4; i < splitLine.length; i++) {
                            lineToAdd.append(" ").append(splitLine[i]);
                        }
                        messagesStringBuilder.append(username).append(": ").append(lineToAdd.toString()).append("\n");
                        messageTextArea.setText(messagesStringBuilder.toString());
                        scrollMessagesToBottom();

                    } else if (line.split(" ")[1].equalsIgnoreCase("quit")) {
                        String username = line.substring(1, line.indexOf("!~"));
                        messagesStringBuilder.append("Użytkownik ").append(username).append(" opuścił czat.\n");
                        messageTextArea.setText(messagesStringBuilder.toString());
                        scrollMessagesToBottom();
                        App.getBufferedWriter().write("NAMES " + App.currentChannel + "\r\n");
                        App.getBufferedWriter().flush();
                        if (username.equals(App.getCurrentNickname())) {
//                            App.getInstance().getResultThread().join();
//                            App.getInstance().getConnection().join();
//                            App.getInstance().closeConnection();

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Uwaga");
                            alert.setHeaderText("Utracono połączenie");
                            alert.setContentText("Utracono połączenie z kanałem " + App.getCurrentChannel() + "\n" +
                                    "Czy chcesz nawiązać kolejne połączenie?");
                            ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setText("Tak");
                            ((Button) alert.getDialogPane().lookupButton(ButtonType.NO)).setText("Nie");
                            ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.YES) {
                                // coś tam, nie wiem
                            } else if (result.isPresent() && result.get() == ButtonType.NO) {
                                System.exit(0);
                            }
                        }
                    }
                    messageTextArea.setText(messagesStringBuilder.toString());
                    scrollMessagesToBottom();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        messageTextArea.setEditable(false);
        messagesStringBuilder.append("[BlitzIRC] Witaj na kanale ").append(App.currentChannel)
                .append(". Komenda \"/priv <nick_uzytkownika> <wiadomosc>\"\n");
        messagesStringBuilder.append("[BlitzIRC] pozwala wysłać wiadomość tylko do konkretnego użytkownika. Inne wiadomości zaczynające się od ukośnika\n")
                .append("[BlitzIRC] to komendy serwera IRC. Używaj ich tylko wtedy, kiedy wiesz co robisz!\n");
        messageTextArea.setText(messagesStringBuilder.toString());
        App.getInstance().initializeServerThread(new Server());
        messageTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String messageText = messageTextField.getText();
                if (messageText.equals("")) {
                    return;
                }
                if (messageText.startsWith("/priv")) {
                    List<String> commandTokens = Arrays.asList(messageText.trim().replaceAll(" +", " ").split(" "));
                    if (commandTokens.size() < 3 || commandTokens.get(1).startsWith("#")) {
                        messagesStringBuilder.append("[BlitzIRC] Składnia komendy /priv: ")
                                .append("[BlitzIRC] /priv <nick_uzytkownika> <wiadomosc>");
                        messageTextArea.setText(messagesStringBuilder.toString());
                        scrollMessagesToBottom();
                    } else {
                        StringBuilder sbMessage = new StringBuilder(commandTokens.get(2));
                        for (int i = 3; i < commandTokens.size(); i++) {
                            sbMessage.append(" ").append(commandTokens.get(i));
                        }
                        try {
                            App.getBufferedWriter().write("PRIVMSG " + commandTokens.get(1) + " :" + sbMessage.toString() + "\r\n");
                            App.getBufferedWriter().flush();
                            messagesStringBuilder.append(App.getCurrentNickname()).append(" -> ").append(commandTokens.get(1))
                                    .append(": ").append(sbMessage.toString());
                            messageTextArea.setText(messagesStringBuilder.toString());
                            scrollMessagesToBottom();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (messageText.charAt(0) == '/') {
                    try {
                        App.getBufferedWriter().write(messageText.substring(1) + "\r\n");
                        App.getBufferedWriter().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        App.getBufferedWriter().write("PRIVMSG " + App.getCurrentChannel() + " :" + messageText + "\r\n");
                        App.getBufferedWriter().flush();
                        messagesStringBuilder.append(App.getCurrentNickname()).append(": ").append(messageText).append("\n");
                        messageTextArea.setText(messagesStringBuilder.toString());
                        scrollMessagesToBottom();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                messageTextField.setText("");
            }
        });
        userListView.setEditable(false);
        userListView.setItems(userList);

        changeChannelButton.setOnAction(actionEvent -> Platform.runLater(() -> {
            try {
//                App.getInstance().getServerThread().join();
//                App.getInstance().getResultThread().join();
//                App.getInstance().getConnection().join();
                leaveChannel();
                App.getInstance().closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("loginWindow.fxml"));
                App.openWindow(loader, borderPane.getScene(), "Logowanie", 420, 400);
        }));

        exitButton.setOnAction(actionEvent -> {
            try {
//                App.getInstance().getServerThread().join();
////                App.getInstance().getResultThread().join();
////                App.getInstance().getConnection().join();
                leaveChannel();
                App.getInstance().closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }

    public void scrollMessagesToBottom() {
        int caretPos = messageTextArea.caretPositionProperty().get();
        messageTextArea.positionCaret(caretPos);
        messageTextArea.setScrollTop(Double.MAX_VALUE);
    }

    public void leaveChannel() throws IOException {
        App.getBufferedWriter().write("QUIT :Left the channel\r\n");
        App.getBufferedWriter().flush();
    }
}
