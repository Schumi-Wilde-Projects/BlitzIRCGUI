package org.schumiwildeprojects.states;

import java.io.IOException;

public class MainWindowState extends State {
    public MainWindowState() throws IOException {
        super();
    }

    @Override
    public void onClose() throws IOException {
        appInstance.changeState(new ExitState());
    }

    @Override
    public void onSubmit() throws IOException {
        appInstance.changeState(new LoginState());
    }
}
