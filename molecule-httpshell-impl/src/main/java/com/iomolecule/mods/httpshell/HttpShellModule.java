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

package com.iomolecule.mods.httpshell;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import com.iomolecule.config.ConfigurationException;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.shell.http.HttpShellConfig;
import com.iomolecule.shell.http.MediaType;
import com.iomolecule.shell.http.annotations.SupportedMediaTypes;
import com.iomolecule.system.LifecycleManager;

import javax.inject.Singleton;
import java.util.AbstractMap;

import static com.iomolecule.util.CollectionUtils.LIST;
import static com.iomolecule.util.CollectionUtils.tuple;

public class HttpShellModule extends MoleculeModule {

    @Override
    protected AbstractMap.SimpleEntry<String, Class<? extends LifecycleManager>> getLifecycleManager() {
        return tuple("httpshell-lifecyclemanager",HttpShellLifecycleManager.class);
    }

    @Override
    protected void configure() {
        super.configure();
        registerShell("shell://http/default",HttpShellImpl.class);

        binder().bind(io.undertow.server.HttpHandler.class)
                .annotatedWith(Names.named("main-http-shell-handler"))
                .to(HttpHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public HttpShellConfig provideHttpShellConfig(ConfigurationSource configurationSource){
        HttpShellConfig httpShellConfig = null;

        if(configurationSource.isValid("/http-shell-config")){
            try {
                httpShellConfig = configurationSource.get("/http-shell-config",HttpShellConfig.class);
            } catch (ConfigurationException e) {
                httpShellConfig = new HttpShellConfig(8080,"localhost");
            }
        }else{
            httpShellConfig = new HttpShellConfig(8080,"localhost");
        }

        return httpShellConfig;

    }


    @ProvidesIntoSet
    @SupportedMediaTypes
    @Singleton
    public String provideSupportedMediaTypeJson(){
        return MediaType.APPLICATION_JSON;
    }

}
