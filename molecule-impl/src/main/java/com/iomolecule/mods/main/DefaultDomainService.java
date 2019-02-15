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

package com.iomolecule.mods.main;

import io.datatree.Tree;
import lombok.extern.slf4j.Slf4j;
import com.iomolecule.system.Operation;
import com.iomolecule.system.SimpleOperation;
import com.iomolecule.system.services.DomainService;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
class DefaultDomainService implements DomainService {

    private Set<List<Operation>> operationsSet;
    private Tree domainTree;
    private boolean started;
    private Operation noOp = new SimpleOperation("__no_op__");

    DefaultDomainService(Set<List<Operation>> operationsSet){
        this.operationsSet = operationsSet;
    }

    DefaultDomainService(List<Operation> operations){
        Set<List<Operation>> ops = new HashSet<>();
        ops.add(operations);
        this.operationsSet = ops;
    }

    @Override
    public void start() {
        if(!started) {
            domainTree = new Tree();

            if (operationsSet != null && operationsSet.size() > 0) {
                for (List<Operation> operations : operationsSet) {
                    for (Operation operation : operations) {
                        if (domainTree.getObject(operation.getName(), null) == null) {
                            log.debug("Adding operation {}",operation);

                            domainTree.putObject(operation.getName(), operation);
                        } else {
                            throw new RuntimeException(String.format("Operation %s has a conflict within the domain tree", operation.getName()));
                        }
                    }

                }
            }
            started = true;
        }

        log.debug("Registered Domains \n {}",domainTree);
    }

    @Override
    public boolean isValidOperation(String path) {
        doStartedCheck();
        Operation op = domainTree.getObject(path,noOp);
        return op != noOp ? true : false;

    }

    private void doStartedCheck() {
        checkArgument(started,"Domain service in an invalid state. It does not seem to be stared!");
    }

    //@Override
    //public void forEach(Consumer<Operation> operationConsumer) {
    //    doStartedCheck();
    //}

    @Override
    public Operation getOperation(String path) {
        doStartedCheck();

        /*Tree tree = domainTree.get(path);
        if(tree != null){
            Object asObject = tree.asObject();
            Object newObj = domainTree.getObject(path,noOp);
            log.debug("Operation @ {} is {} , new Obj {}",path,asObject,newObj);
        }*/
        return domainTree.getObject(path,noOp);
    }

    @Override
    public List<String> getDomainNamesAt(String path) {

        List<String> domainNames = new ArrayList<>();
        if(domainTree.isExists(path)){

            Tree tree = domainTree.get(path);

            tree.forEach(subtree->{
                Object obj = subtree.asObject();

                if(!(obj instanceof Operation)){
                    domainNames.add(subtree.getName());
                }
            });
        }
        return domainNames;
    }

    @Override
    public List<String> getOperationsAt(String path) {
        List<String> operationNames = new ArrayList<>();
        if(domainTree.isExists(path)){

            Tree tree = domainTree.get(path);

            tree.forEach(subtree->{
                Object obj = subtree.asObject();

                if(obj instanceof Operation){
                    operationNames.add(subtree.getName());
                }
            });
        }
        return operationNames;
    }

    @Override
    public void stop() {
        if(domainTree != null){

            domainTree.clear();
            domainTree = null;
            started = false;
        }
    }

    @Override
    public void print(PrintStream stream) {
        stream.print(domainTree);
    }

    @Override
    public List<Operation> getAllOperations() {
        doStartedCheck();

        List<Operation> operations = new ArrayList<>();

        domainTree.forEach(tree -> {
            Object opObj = tree.asObject();
            if(opObj instanceof Operation){
                operations.add((Operation)opObj);
            }
        });


        return operations;
    }


    @Override
    public boolean isValidOperationAt(String path,String operationName) {
        List<String> operationsAt = getOperationsAt(path);
        return operationsAt.contains(operationName);
    }

    @Override
    public Operation getOperationAt(String fullyQualifiedDomainName, String operationName) {
        return getOperation(String.format("%s.%s",fullyQualifiedDomainName,operationName));
    }

    @Override
    public List<Operation> getAllOperationsAt(String fullyQualifiedDomainName) {
        List<Operation> allOperations = new ArrayList<>();
        if(domainTree.isExists(fullyQualifiedDomainName)){

            Tree tree = domainTree.get(fullyQualifiedDomainName);

            tree.forEach(subtree->{
                Object obj = subtree.asObject();

                if(obj instanceof Operation){
                    allOperations.add((Operation)obj);
                }
            });
        }
        return allOperations;
    }

    @Override
    public boolean isValidDomainAt(String path, String domain) {
        List<String> domainNamesAt = getDomainNamesAt(path);
        return domainNamesAt.contains(domain);
    }
}
