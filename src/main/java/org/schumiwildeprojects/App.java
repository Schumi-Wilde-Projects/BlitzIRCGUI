package org.schumiwildeprojects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.schumiwildeprojects.states.ConnectionState;
import org.schumiwildeprojects.states.State;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static App instance;
    public static String currentChannel;
    private static String currentServerName;
    private static String currentNickname;
    private static String currentLogin;
    private static String currentFullName;
    private static String currentPassword;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private State state;
    private ConnectionThread connectionThread;
    private Thread serverThread, resultThread, connection;
    private Socket socket;

    private static Scene scene;

    public static App getInstance() {
        if (instance == null) {
            return new App();
        }
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("loginWindow"), 420, 400);
        stage.setTitle("Logowanie");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    class ConnectionThread implements Runnable {
        private final String serverName;
        private final String nick;
        private final String login;
        private final String fullName;
        private final String channel;
        private final String pass;
        private ConnectionState state;

        public ConnectionThread(String serverName, String nick, String login, String fullName, String channel, String pass) {
            this.serverName = serverName;
            this.nick = nick;
            this.login = login;
            this.fullName = fullName;
            this.channel = channel;
            this.pass = pass;
        }

        @Override
        public void run() {
            if (!serverName.equals("") && !nick.equals("") && !login.equals("") && !fullName.equals("") && !channel.equals("")) {
                try {
                    state = connectToServer(serverName, nick, login, fullName, channel, pass);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                state = ConnectionState.FIELDS_NOT_FILLED;
            }
        }

        public ConnectionState getState() {
            return state;
        }
    }

    public static void openWindow(FXMLLoader loader, Scene scene2, String title, int width, int height) {
        try {
            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((Stage) scene2.getWindow()).close();
    }

    public void changeState(State state) {
        this.state = state;
    }

    public void initializeConnectionThread(String serverName, String nick, String login, String fullName, String channel,
                                           String password) {
        currentChannel = channel;
        connectionThread = new ConnectionThread(serverName, nick, login, fullName, channel, password);
        connection = new Thread(connectionThread);
        connection.start();
    }

    public ConnectionState connectToServer(String serverName, String nick, String login, String fullName, String channel,
                                           String password) throws IOException {
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(serverName, 6667), 10000);
        } catch (SocketTimeoutException e) {
            return ConnectionState.SERVER_TIMEOUT;
        } catch (SocketException e) {
            return ConnectionState.SERVER_NAME_EMPTY;
        }

        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        bufferedWriter.write("NICK " + nick + "\r\n");
        bufferedWriter.write("USER " + login + " 8 * : " + fullName + "\r\n");
        bufferedWriter.write("msg nickserv identify " + nick + " " + password + "\r\n");
        bufferedWriter.flush();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("004")) {
                break;
            } else if (line.contains("432")) {
                return ConnectionState.INVALID_NICKNAME;
            } else if (line.contains("433")) {
                return ConnectionState.USERNAME_EXISTS;
            }
        }

        bufferedWriter.write("JOIN " + channel + "\r\n");
        bufferedWriter.flush();

        currentChannel = channel;
        currentNickname = nick;
        currentFullName = fullName;
        currentLogin = login;
        currentPassword = password;

        return ConnectionState.SUCCESSFUL;
    }

    public void initializeResultThread(Runnable runnable) {
        resultThread = new Thread(runnable);
        resultThread.start();
    }

    public void initializeServerThread(Runnable runnable) {
        serverThread = new Thread(runnable);
        serverThread.start();
    }

    public void closeConnection() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                System.exit(-1);
            }
        }
    }

}