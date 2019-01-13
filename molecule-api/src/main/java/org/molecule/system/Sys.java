package org.molecule.system;

public interface Sys {

    void start() throws LifecycleException;

    boolean isStarted();

    void stop();
}
