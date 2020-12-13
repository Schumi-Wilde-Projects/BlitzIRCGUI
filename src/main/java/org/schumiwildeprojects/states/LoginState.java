package org.schumiwildeprojects.states;

import org.schumiwildeprojects.App;
import org.schumiwildeprojects.LoginWindow;

import java.io.IOException;

public class LoginState extends State {
    private LoginWindow window;

    public LoginState() throws IOException {
        super();
    }

    @Override
    public void onClose() throws IOException {
        appInstance.changeState(new ExitState());
    }

    @Override
    public void onSubmit() throws IOException {
        String serverName = window.getServerName();
        String nick = window.getNick();
        String login = window.getLogin();
        String fullName = window.getFullName();
        String channel = window.getChannelName();
        String password = window.getPassword();
        App.currentChannel = channel;
        appInstance.initializeConnectionThread(serverName, nick, login, fullName, channel, password);
        appInstance.changeState(new ConnectingProgressState());
    }
}
