package org.molecule.system.services;

import org.molecule.system.Fn;
import org.molecule.system.LifecycleException;

import java.net.URI;
import java.util.function.Consumer;

public interface FnBus extends Fn{

    public void forEach(Consumer<Fn> fnConsumer);

    public boolean hasFnForURI(URI uri);

    public void start() throws LifecycleException;

    public void stop();

}
