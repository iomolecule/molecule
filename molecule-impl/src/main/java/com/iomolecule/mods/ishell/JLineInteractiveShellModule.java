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

import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.iomolecule.ishell.annotations.DomainStack;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.Shell;
import com.iomolecule.system.annotations.Shells;

import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.Stack;

import static com.iomolecule.util.CollectionUtils.tuple;

public class JLineInteractiveShellModule extends MoleculeModule{


    @Override
    protected void configure() {
        initModule();
        bind(JLineInteractiveShell.class).in(Singleton.class);
        bind(Shell.class).annotatedWith(Names.named("shell://interactive/jline")).to(JLineInteractiveShell.class);
        MapBinder<String, Shell> shellMapBinder = MapBinder.newMapBinder(binder(), String.class, Shell.class, Shells.class);
        shellMapBinder.addBinding("shell://interactive/jline").to(JLineInteractiveShell.class);
        registerFuncs();
    }

    private void registerFuncs() {

        registerFuncs(ListHelpFunction.class,
                ExitSystemFunction.class,
                ListCurrentDomainsFunction.class,
                ListOperationsFunction.class,
                GetPresentWorkingDomainFunction.class,
                ChangeDomainFunction.class,
                ListModulesFunction.class,
                AboutModuleFunction.class);

    }

    @Provides
    @DomainStack
    @Singleton
    public Stack<String> provideJLineShellStack(){
        return new Stack<String>();
    }


    @Override
    protected AbstractMap.SimpleEntry<String, Class<? extends LifecycleManager>> getLifecycleManager() {
        return tuple("jline-interactive-shell",JLineShellLifecycleManager.class);
    }
}
