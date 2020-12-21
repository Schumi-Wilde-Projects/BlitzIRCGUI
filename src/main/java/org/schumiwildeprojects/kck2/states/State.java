package org.schumiwildeprojects.kck2.states;

import org.schumiwildeprojects.kck2.App;

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
