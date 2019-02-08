/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.molecule.module;

import com.google.common.io.ByteStreams;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import io.datatree.Tree;
import lombok.extern.slf4j.Slf4j;
import org.molecule.config.ConfigurationSource;
import org.molecule.config.InputStreamConfigurationSource;
import org.molecule.config.InputStreamMsgConfigSource;
import org.molecule.config.MsgConfigSource;
import org.molecule.config.annotations.DefaultConfigsSource;
import org.molecule.config.annotations.MsgConfigsSource;
import org.molecule.module.annotations.ModulesInfo;
import org.molecule.system.Operation;
import org.molecule.system.Param;
import org.molecule.system.SimpleOperation;
import org.molecule.system.annotations.DomainOperations;
import org.molecule.system.annotations.Func;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.molecule.util.JSONUtils.OBJECT_MAPPER;

@Slf4j
public abstract class MoleculeModule extends AbstractModule{


    @Override
    protected void configure() {
        super.configure();

        initModule();
    }

    protected void initModule(){
        registerSystemInfoFromDefaultPath();
        registerDomainOperationsFromDefaultPath();
        registerConfigSourcesFromDefaultPath();
        registerMsgConfigSourcesFromDefaultPath();
    }

    protected void registerConfigSourcesFromDefaultPath() {
        String configFile = String.format("/config/%s.json", getClass().getName());
        try(InputStream resourceAsStream = getClass().getResourceAsStream(configFile)){
            if(resourceAsStream != null){
                InputStreamConfigurationSource inputStreamConfigurationSource =
                        new InputStreamConfigurationSource(false,true,resourceAsStream);
                registerConfigSource(inputStreamConfigurationSource);
            }else{
                log.info("Unable to find default config file {} in classpath!",configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("Failed to read {}, so ignoring config file..",configFile);
        }

    }

    private void registerConfigSource(ConfigurationSource configurationSource) {
        Multibinder<ConfigurationSource> configSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                DefaultConfigsSource.class);
        configSources.addBinding().toInstance(configurationSource);

    }

    protected void registerMsgConfigSourcesFromDefaultPath() {
        String msgConfigFile = String.format("/msg-config/%s.json", getClass().getName());
        try(InputStream resourceAsStream = getClass().getResourceAsStream(msgConfigFile)){
            if(resourceAsStream != null){
                InputStreamMsgConfigSource inputStreamMsgConfigSource =
                        new InputStreamMsgConfigSource(false,false,
                                resourceAsStream);
                registerMsgConfigSource(inputStreamMsgConfigSource);
            }else{
                log.info("Unable to find default message config file {} in classpath!",msgConfigFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("Failed to read {}, so ignoring message config file..",msgConfigFile);
        }

    }

    protected void registerMsgConfigSource(MsgConfigSource msgConfigSource) {
        Multibinder<ConfigurationSource> msgConfigSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                MsgConfigsSource.class);
        msgConfigSources.addBinding().toInstance(msgConfigSource);
    }

    protected void registerSystemInfoFromDefaultPath() {
        String moduleInfoFile = String.format("/info/%s.json", getClass().getName());
        try(InputStream resourceAsStream = getClass().getResourceAsStream(moduleInfoFile)){
            if(resourceAsStream != null){
                ModuleInfo moduleInfo = OBJECT_MAPPER.readValue(resourceAsStream, ModuleInfo.class);
                log.info("Registering Module {}",moduleInfo);
                registerSystemInfo(moduleInfo);
            }else{
                log.info("Unable to find default module info file {} in classpath!",moduleInfoFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("Failed to read {}, so ignoring moduleinfo..",moduleInfoFile);
        }
    }

    protected void registerDomainOperationsFromDefaultPath() {
        String domainOperationsFile = String.format("/domains/%s.json", getClass().getName());
        registerDomainOperationsFromClasspath(domainOperationsFile);
    }

    protected void registerSystemInfo(ModuleInfo moduleInfo){
        Multibinder<ModuleInfo> modulesInfo = Multibinder.newSetBinder(binder(),new TypeLiteral<ModuleInfo>(){},
                ModulesInfo.class);

        modulesInfo.addBinding().toInstance(moduleInfo);

    }


    protected void registerDomainOperationsFromClasspath(String fileInClasspath){

        try {
            List<Operation> operations = getOperations(fileInClasspath);

            Multibinder<List<Operation>> domainOperations = Multibinder.newSetBinder(binder(),new TypeLiteral<List<Operation>>(){},
                    DomainOperations.class);

            domainOperations.addBinding().toInstance(operations);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void registerFuncs(Class<? extends Function<Param,Param>>... funcs){
        Multibinder<Function<Param,Param>> funcSet = Multibinder.newSetBinder(binder(),new TypeLiteral<Function<Param,Param>>(){},
                Func.class);
        for (Class<? extends Function<Param, Param>> funcClz : funcs) {
            funcSet.addBinding().to(funcClz).in(Singleton.class);
        }
    }

    protected List<Operation> getOperations(String fileInClasspath) throws Exception {

        try(InputStream inputStream = getClass().getResourceAsStream(fileInClasspath)){
            if(inputStream != null){
                byte[] bytes = ByteStreams.toByteArray(inputStream);
                Tree dataTree = new Tree(bytes);
                return getOperations(dataTree, "");
            }else{
                log.warn(String.format("Failed to read %s, so ignoring domain information..",fileInClasspath));
                return Collections.EMPTY_LIST;
            }
        }
    }

    protected List<Operation> getOperations(Tree data,String currentName){
        String uri = null;
        String doc = null;

        String newCurrentName = currentName.isEmpty() ? data.getName() : currentName + "." + data.getName();



        if(data.get("uri") != null){
            Tree uriTree = data.get("uri");
            uri = uriTree.asString();
        }

        if(data.get("doc") != null){
            Tree docTree = data.get("doc");
            doc = docTree.asString();
        }

        List<Operation> operations = new ArrayList<>();
        if(uri == null && doc == null){
            data.forEach(childTree->{
                List<Operation> childOperations = getOperations(childTree,newCurrentName == null ? "" : newCurrentName);
                operations.addAll(childOperations);
            });
        }else{
            if(uri != null) {
                operations.add(new SimpleOperation(newCurrentName, uri, doc == null ? "Not available" : doc));
            }else{
                String message = String.format("URI not provided for operation %s", newCurrentName == null ? "" : newCurrentName);
                throw new RuntimeException(message);
            }
        }

        return operations;
    }

}
