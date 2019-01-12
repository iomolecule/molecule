package org.molecule.config;

import lombok.extern.slf4j.Slf4j;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.compose.FallbackConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.strategy.ImmediateReloadStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class ConfigUtil {

    public static class CfgFilesProvider implements ConfigFilesProvider{

        private String[] fileNames;
        private String profile;

        public CfgFilesProvider(String profile,String... files){
            this.profile = profile;
            fileNames = files;
        }

        @Override
        public Iterable<Path> getConfigFiles() {

            List<Path> filePaths = new ArrayList<>();

            if(fileNames != null){
                for(String fileName : fileNames){
                    filePaths.add(Paths.get(String.format("%s/%s",profile,fileName)));
                }
            }

            return filePaths;

        }
    }

    public static ConfigurationSource[] configs(ConfigurationSource... sources){
        return sources;
    }
    public static ConfigurationSource newFileConfigSource(String profile,String... files){
        ConfigurationSource fileConfigSource = new FilesConfigurationSource(new CfgFilesProvider(profile,files));
        return fileConfigSource;
    }

    public static ConfigurationSource newClasspathConfigSource(String profile,String... files){
        CfgFilesProvider cfgFilesProvider = new CfgFilesProvider(profile,files);
        //log.info("CfgFilesProvider {}", cfgFilesProvider);
        ConfigurationSource classpathConfigSource = new ClasspathConfigurationSource(cfgFilesProvider);
        return classpathConfigSource;
    }

    public static ConfigurationSource newFallbackConfigSource(ConfigurationSource... sources){
        ConfigurationSource fallbackConfigSource = new FallbackConfigurationSource(sources);
        return fallbackConfigSource;
    }

    public static ConfigurationSource newMergedConfigSource(ConfigurationSource... sources){
        ConfigurationSource mergedConfigSource = new MergeConfigurationSource(sources);
        return mergedConfigSource;
    }


    public static ConfigurationProvider newConfigurationProvider(String configLocation,
                                                                 ConfigurationSource source){
        ConfigurationProvider configurationProvider = new ConfigurationProviderBuilder()
                .withReloadStrategy(new ImmediateReloadStrategy())
                .withConfigurationSource(source)
                .withEnvironment(new ImmutableEnvironment(configLocation))
                .build();

        return configurationProvider;
    }


}
