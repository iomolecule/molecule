package org.molecule.config;

public interface ConfigurationSource {

    public <T> T get(String path,Class<T> clz) throws ConfigurationException;

    public <T> T get(String path,Class<T> clz,T defaultVal);

    public boolean isValid(String path);

}
