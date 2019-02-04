package org.molecule.config;


import java.util.Set;

public class CompositeMsgConfigSource extends CompositeConfigurationSource implements MsgConfigSource{

    public CompositeMsgConfigSource(Set<ConfigurationSource> msgConfigSources) {
        super(msgConfigSources);
    }

}
