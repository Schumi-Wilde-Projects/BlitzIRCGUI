package org.schumiwildeprojects.kck2.states;

import java.io.IOException;

public class ConnectingProgressState extends State {

    public ConnectingProgressState() throws IOException {
        super();
    }

    @Override
    public void onClose() throws IOException {
        appInstance.changeState(new LoginState());
    }

    @Override
    public void onSubmit() throws IOException {
        appInstance.changeState(new MainWindowState());
    }
}
