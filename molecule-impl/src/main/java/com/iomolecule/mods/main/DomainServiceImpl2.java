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

import com.iomolecule.system.Operation;
import com.iomolecule.system.SimpleOperation;
import com.iomolecule.system.services.DomainService;
import com.iomolecule.util.ds.InvalidTreeNodePathException;
import com.iomolecule.util.ds.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.iomolecule.util.CollectionUtils.KV;
import static com.iomolecule.util.CollectionUtils.MAP;

@Slf4j
public class DomainServiceImpl2 implements DomainService {

    private Set<List<Operation>> operationsSet;
    private boolean started;
    private Operation noOp = new SimpleOperation("__no_op__");
    private OperationNode rootNode = new OperationNode("--root--");

    DomainServiceImpl2(Set<List<Operation>> operationsSet){
        this.operationsSet = operationsSet;
    }

    DomainServiceImpl2(List<Operation> operations){
        Set<List<Operation>> ops = new HashSet<>();
        ops.add(operations);
        this.operationsSet = ops;
    }

    @Override
    public void start() {
        if(!started){

            if (operationsSet != null && operationsSet.size() > 0) {
                for (List<Operation> operations : operationsSet) {
                    for (Operation operation : operations) {
                        try {
                            rootNode.setDataAtPath(operation.getName(),operation);

                        } catch (InvalidTreeNodePathException e) {
                            //e.printStackTrace();
                            log.warn("Invalid Tree Node path {}",e.getMessage());
                            log.warn("Ignoring Operation {} from the domain tree...",operation.getName());
                        }
                    }
                }

                //finally freeze the nodes
                rootNode.freeze();
            }

            started = true;
        }

    }

    private void doStartedCheck() {
        checkArgument(started,"Domain service in an invalid state. It does not seem to be stared!");
    }


    @Override
    public boolean isValidOperation(String path) {

        doStartedCheck();

        boolean retVal = false;

        try {
            Operation operation = rootNode.getDataAtPath(path);
            if(operation != null){
                retVal = true;
            }
        } catch (InvalidTreeNodePathException e) {

        }

        return retVal;
    }

    @Override
    public Operation getOperation(String path) {

        doStartedCheck();

        Operation op = this.noOp;

        try {
            op = rootNode.getDataAtPath(path);
        } catch (InvalidTreeNodePathException e) {

        }

        return op;
    }

    @Override
    public List<String> getDomainNamesAt(String path) {

        doStartedCheck();

        ArrayList<String> children = new ArrayList<>();

        try {

            TreeNode<Operation> child = rootNode;
            if(!StringUtils.isEmpty(path)){
                child = rootNode.getChildAtPath(path);
            }

            //TreeNode<Operation> child = rootNode.getChildAtPath(path);

            if(child.hasChildren()){
                for (TreeNode<Operation> childChild : child.getChildren()) {
                    if(childChild.getData() == null) { //show only domains not operations
                        children.add(childChild.getName());
                    }
                }
            }
        } catch (InvalidTreeNodePathException e) {
            e.printStackTrace();
        }

        return children;
    }

    @Override
    public List<String> getOperationsAt(String path) {
        doStartedCheck();

        ArrayList<String> children = new ArrayList<>();

        try {
            TreeNode<Operation> child = rootNode;
            if(!StringUtils.isEmpty(path)){
                child = rootNode.getChildAtPath(path);
            }


            if(child.hasChildren()){
                for (TreeNode<Operation> childChild : child.getChildren()) {
                    if(childChild.getData() != null){
                        Operation opData = (Operation)childChild.getData();
                        children.add(opData.getSimpleName());
                    }
                }
            }
        } catch (InvalidTreeNodePathException e) {
            e.printStackTrace();
        }

        return children;
    }

    @Override
    public void stop() {
        if(operationsSet != null){
            operationsSet = null;
        }

        if(noOp != null){
            noOp = null;
        }

        if(rootNode != null){
            rootNode = null;
        }

    }

    @Override
    public void print(PrintStream stream) {
        stream.print(rootNode);
    }

    @Override
    public List<Operation> getAllOperations() {
        doStartedCheck();

        List<Operation> operations = getOperationsFor(rootNode,null);

        return operations;

    }

    private List<Operation> getOperationsFor(TreeNode<Operation> node,List<Operation> opList){
        List<Operation> operationList = (opList == null ? new ArrayList<Operation>() : opList);

        TreeNode<Operation>[] children = node.getChildren();

        for (TreeNode<Operation> op: children) {
            if(op.getData() != null){
                operationList.add(op.getData());
            }
            operationList = getOperationsFor(op,operationList);
        }

        return operationList;
    }

    @Override
    public boolean isValidOperationAt(String path, String operationName) {
        List<String> operationsAt = getOperationsAt(path);
        return operationsAt.contains(operationName);
    }

    @Override
    public Operation getOperationAt(String fullyQualifiedDomainName, String operationName) {
        return getOperation(String.format("%s.%s",fullyQualifiedDomainName,operationName));
    }

    @Override
    public List<Operation> getAllOperationsAt(String fullyQualifiedDomainName) {
        doStartedCheck();

        ArrayList<Operation> children = new ArrayList<>();

        try {

            TreeNode<Operation> child = rootNode;
            if(!StringUtils.isEmpty(fullyQualifiedDomainName)){
                child = rootNode.getChildAtPath(fullyQualifiedDomainName);
            }

            if(child.hasChildren()){
                for (TreeNode<Operation> childChild : child.getChildren()) {
                    if(childChild.getData() != null){
                        Operation opData = (Operation)childChild.getData();
                        children.add(opData);
                    }
                }
            }
        } catch (InvalidTreeNodePathException e) {
            e.printStackTrace();
        }

        return children;
    }

    @Override
    public boolean isValidDomainAt(String path, String domain) {
        List<String> domainNamesAt = getDomainNamesAt(path);
        List<String>[] sortedNames = getTemplateNames(domainNamesAt);

        boolean nameMatched = false;
        //first check the simple names
        if(isValidDomainNameAt(domain,sortedNames[0])){
            nameMatched = true;
        }else{
            nameMatched = (sortedNames[1].size() > 0) ? true : false;
        }

        return nameMatched;
    }

    private boolean isValidDomainNameAt(String domain, List<String> sortedName) {
        boolean retVal = false;
        for (String name : sortedName) {
            if(name.equalsIgnoreCase(domain)){
                retVal = true;
                break;
            }
        }

        return retVal;
    }

    private List<String>[] getTemplateNames(List<String> domainNamesAt) {
        List<String> templateNames = new ArrayList<>();
        List<String> simpleNames = new ArrayList<>();
        for (String domainName : domainNamesAt) {
            Optional<String> templateVariable = com.iomolecule.util.StringUtils.getTemplateVariable(domainName,
                    OperationNode.TEMPLATE_START, OperationNode.TEMPLATE_END);
            if(templateVariable.isPresent()){
                templateNames.add(templateVariable.get());
            }else{
                simpleNames.add(domainName);
            }
        }

        return new List[]{simpleNames,templateNames};
    }



    @Override
    public Map getDomainTree() {
        return getTreeNodeAsMap(rootNode);
    }

    @Override
    public String getFullyQualifiedDomainPathAt(String actualPath) {
        String retVal = null;
        try {
            TreeNode<Operation> childAtPath = rootNode.getChildAtPath(actualPath);
            retVal = childAtPath.getFullyQualifiedName();
        } catch (InvalidTreeNodePathException e) {
            //e.printStackTrace();
        }

        return retVal;
    }

    private Map getTreeNodeAsMap(TreeNode<Operation> node) {
        Map mapTree = new HashMap();


        if(node.hasChildren()){
            for (TreeNode<Operation> child : node.getChildren()) {
                if(child.hasChildren()) {
                    mapTree.put(child.getName(), getTreeNodeAsMap(child));
                }else if(child.getData() != null){
                    Operation op = child.getData();
                    mapTree.put(op.getSimpleName(),MAP(KV("uri",op.getFunctionURI()),KV("doc",op.getDoc())));
                }
            }

        }else if(node.getData() != null){
            //means this is a leaf node holding operation information
            Operation op = node.getData();
            mapTree.put(op.getSimpleName(),MAP(KV("uri",op.getFunctionURI()),KV("doc",op.getDoc())));
        }

        return mapTree;
    }
}
