package org.molecule.config;

import java.util.Set;

public class CompositeConfigurationSource implements ConfigurationSource{

    private Set<ConfigurationSource> configurationSources;

    public CompositeConfigurationSource(Set<ConfigurationSource> configurationSources){
        this.configurationSources = configurationSources;
    }

    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                return configurationSource.get(path,clz);
            }
        }

        throw new ConfigurationNotFoundException(
                String.format("Could not find the configuration for path % in any of the configured configuration sources %s",path,configurationSources));
    }

    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        try {
            return get(path,clz);
        } catch (ConfigurationException e) {
            return defaultVal;
        }

    }

    @Override
    public boolean isValid(String path) {
        boolean retVal = false;

        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                retVal = true;
                break;
            }
        }

        return retVal;
    }
}
