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

package com.iomolecule.util;

import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.DomainService;

import javax.annotation.Nonnull;
import javax.inject.Named;

public class TestDomainFunctions {

    private DomainService domainService;

    public TestDomainFunctions(DomainService domainService){
        this.domainService = domainService;
    }

    @Id("function://test/greetingFunction")
    public @Named("greeting") @Nonnull String greetingFunction(@Named("name") @Nonnull String name,

                                                               @Named("sex") @Nonnull String sex){
        String formattedGreeting = "Hello %s.%s";

        return sex.equalsIgnoreCase("male") ? String.format(formattedGreeting,"Mr",name)
                : String.format(formattedGreeting,"Ms",name);
    }

    @Id("function://test/greetingFunction2")
    public @Named("greeting") @Nonnull String greetingFunction2(@Named("name") @Nonnull String name,

                                                               @Named("sex") @Nonnull String sex){
        String formattedGreeting = "Hello %s.%s";

        return sex.equalsIgnoreCase("male") ? String.format(formattedGreeting,"Mr",name)
                : String.format(formattedGreeting,"Ms",name);
    }


    @Id("function://test/greetingFunction3")
    public @Named("greeting") @Nonnull String greetingFunction3(@Named("name") @Nonnull String name,

                                                                @Named("sex") @Nonnull String sex){
        String formattedGreeting = "Hello %s.%s";

        return sex.equalsIgnoreCase("male") ? String.format(formattedGreeting,"Mr",name)
                : String.format(formattedGreeting,"Ms",name);
    }



}
