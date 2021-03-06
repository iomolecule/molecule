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

import com.iomolecule.shell.http.HttpShellConfig;
import com.iomolecule.system.annotations.Id;

import javax.inject.Named;

public class HttpShellFnProvider {

    private HttpShellConfig shellConfig;

    HttpShellFnProvider(HttpShellConfig config){
        shellConfig = config;
    }

    @Id("function://httpshell/GetConfigFunction")
    public @Named("config") HttpShellConfig getHttpShellConfig(){
        return shellConfig;
    }


}
