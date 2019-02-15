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

package com.iomolecule.system;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Default implementation of {@link Operation}
 */
public class SimpleOperation implements Operation{

    private String name;
    private URI fnURI;
    private String documentation;
    private String simpleName;

    public SimpleOperation(String name){
        this(name,(URI)null,"Not available at present");
    }

    public SimpleOperation(String name,URI fnURI,String doc){
        checkArgument(name != null && !name.isEmpty(),"Operation name cannot be null or empty!");
        this.name = name;
        this.fnURI = fnURI;
        this.documentation = doc;
        String[] splitNames = name.split("\\.");
        if(splitNames != null && splitNames.length > 0){
            simpleName = splitNames[splitNames.length-1];
        }else{
            simpleName = name;
        }
    }

    public SimpleOperation(String name,String uriString,String doc){
        this.name = name;
        this.documentation = doc;
        try {
            this.fnURI = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String[] splitNames = name.split("\\.");
        if(splitNames != null && splitNames.length > 0){
            simpleName = splitNames[splitNames.length-1];
        }else{
            simpleName = name;
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getFunctionURI() {
        return fnURI;
    }

    @Override
    public String getDoc() {
        return documentation;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String toString() {
        return "SimpleOperation{" +
                "name='" + name + '\'' +
                ", fnURI=" + fnURI +
                ", documentation='" + documentation + '\'' +
                ", simpleName='" + simpleName + '\'' +
                '}';
    }
}
