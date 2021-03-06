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

import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.Shell;
import com.iomolecule.system.annotations.MainArgs;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

@Slf4j
class HttpShellLifecycleManager implements LifecycleManager{

    private Shell httpShell;
    private String[] mainArgs;

    @Inject
    HttpShellLifecycleManager(@MainArgs String[] mainArgs, @Named("shell://http/default") Shell httpShell){
        Objects.requireNonNull(httpShell,"http shell");
        this.httpShell = httpShell;
        this.mainArgs = mainArgs;

    }
    @Override
    public void start() throws LifecycleException {
        log.debug("Starting...");

        httpShell.start(mainArgs);
    }

    @Override
    public void stop() {
        log.debug("Stopping...");
        httpShell.stop();
        httpShell = null;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }
}
