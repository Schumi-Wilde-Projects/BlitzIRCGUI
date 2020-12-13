package org.schumiwildeprojects.states;

import javafx.application.Application;
import org.schumiwildeprojects.App;

import java.io.IOException;

public abstract class State {
    protected App appInstance;

    public State() throws IOException {
        appInstance = App.getInstance();
    }

//    public abstract Application getWindow() throws IOException;
    public abstract void onClose() throws IOException;
    public abstract void onSubmit() throws IOException;
    public void onRetry() throws IOException {}
}
