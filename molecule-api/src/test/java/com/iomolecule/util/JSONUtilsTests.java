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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iomolecule.system.Operation;
import com.iomolecule.system.SimpleOperation;
import com.google.common.io.ByteStreams;
import io.datatree.Tree;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONUtilsTests {

    @Test
    public void testMergeNodes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode baseJsonNode = mapper.readTree(JSONUtilsTests.class.getResource("/base.json"));
        JsonNode overridingJsonNode = mapper.readTree(JSONUtilsTests.class.getResource("/overriding.json"));

        JSONUtils.merge(overridingJsonNode,baseJsonNode,false);

        System.out.println(baseJsonNode);
    }

    @Test
    public void testDataTree() throws Exception {

        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/test.json"));

        Tree dataTree = new Tree(bytes);


        List<Operation> operations = getOperations(dataTree, "");

        System.out.println(operations);

    }

    /*private List<Operation> getOperations(Tree data,List<Operation> ops){

        data.forEach(child->{
            if(child.get("uri") != null){
                Operation newOperation = new Sim
            }

        });
    }*/

    private List<Operation> getOperations(String fileInClasspath) throws Exception {

        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream(fileInClasspath));

        Tree dataTree = new Tree(bytes);


        List<Operation> operations = getOperations(dataTree, "");

        return operations;
    }

    private List<Operation> getOperations(Tree data,String currentName){
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
