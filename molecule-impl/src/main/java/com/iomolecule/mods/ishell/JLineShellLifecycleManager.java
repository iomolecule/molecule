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

package com.iomolecule.mods.ishell;

import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.Shell;
import com.iomolecule.system.annotations.MainArgs;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
class JLineShellLifecycleManager implements LifecycleManager{

    private Shell interactiveShell;
    private String[] args;
    private Thread shellThread;

    @Inject
    JLineShellLifecycleManager(@Named("shell://interactive/jline") Shell jLineInteractiveShell,
                               @MainArgs String[] args){
        interactiveShell = jLineInteractiveShell;
        this.args = args;
    }

    @Override
    public void start() throws LifecycleException {
        log.debug("Starting shell...");

        shellThread = new Thread(new Runnable() {
            @Override
            public void run() {
                interactiveShell.start(args);
                System.exit(0);
            }
        });

        shellThread.start();
    }

    @Override
    public void stop() {
        log.debug("Stopping shell...");

        interactiveShell.stop();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
