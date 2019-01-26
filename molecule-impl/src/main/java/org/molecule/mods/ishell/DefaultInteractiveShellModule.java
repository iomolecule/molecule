package org.molecule.mods.ishell;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import org.molecule.system.Shell;
import org.molecule.system.annotations.Shells;

import javax.inject.Singleton;

public class DefaultInteractiveShellModule extends AbstractModule{

    @Override
    protected void configure() {
        //install(new TextIOInteractiveShellModule());
        bind(TextIOInteractiveShell.class).in(Singleton.class);
        bind(Shell.class).annotatedWith(Names.named("shell://interactive/default")).to(TextIOInteractiveShell.class);
        MapBinder<String, Shell> shellMapBinder = MapBinder.newMapBinder(binder(), String.class, Shell.class, Shells.class);
        shellMapBinder.addBinding("shell://interactive/default").to(TextIOInteractiveShell.class);

    }
}
