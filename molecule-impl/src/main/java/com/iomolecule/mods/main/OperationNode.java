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
import com.iomolecule.util.StringUtils;
import com.iomolecule.util.ds.InvalidTreeNodePathException;
import com.iomolecule.util.ds.TreeNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
class OperationNode implements TreeNode<Operation> {

    private String name;
    private String fullyQualifiedName;
    private Operation data;
    private List<OperationNode> children = new ArrayList<>();
    private boolean templateName = false;
    private TreeNode<Operation> parentNode;
    private boolean frozen = false;


    public static final String TEMPLATE_START = "[";
    public static final String TEMPLATE_END = "]";


    public OperationNode(String name,Operation data){
        this(name);
        this.data = data;
    }

    public OperationNode(String name){
        if(name.startsWith(TEMPLATE_START) && name.endsWith(TEMPLATE_END)){
            //we have a template name so mark the flag appropriately
            this.templateName = true;
        }
        this.name = name;
    }

    public boolean hasTemplateName() {
        return templateName;
    }

    void addChild(OperationNode child){
        children.add(child);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    @Override
    public void setData(Operation data) {
        this.data = data;
    }

    @Override
    public Operation getData() {
        return data;
    }

    @Override
    public boolean hasChildren() {
        boolean retVal = false;
        if(children != null && children.size() > 0){
            retVal = true;
        }
        return retVal;
    }

    @Override
    public TreeNode<Operation>[] getChildren() {
        if(children == null){
            return null;
        }else{
            return children.toArray(new OperationNode[0]);
        }
    }

    @Override
    public boolean isValidChild(String path) {
        boolean validChild = false;

        for (OperationNode child : children) {
            if(child.getName().equalsIgnoreCase(path)){
                validChild = true;
            }
        }

        return validChild;
    }

    @Override
    public TreeNode<Operation> getChildAtPath(String path) throws InvalidTreeNodePathException{
        String[] splitPath = StringUtils.splitPath(path);

        if(splitPath == null || splitPath.length == 0){
            String message = String.format("Invalid path %s.%s for this node",this.name,path);
            throw new InvalidTreeNodePathException(message);
        }

        String childName = splitPath[0]; //the first is always the name

        Optional<TreeNode<Operation>> optionalChildNode = getChild(childName);

        OperationNode childNode = null;

        if(optionalChildNode.isPresent()) {
            childNode = (OperationNode)optionalChildNode.get();
        }else{
            String message = String.format("Invalid path %s.%s for this node",this.name,path);
            throw new InvalidTreeNodePathException(message);
        }

        if(splitPath.length > 1){
            //means there is a subpath
            //so recurse on the childNode
            String subPath = splitPath[1]; //the second is always the rest of the path
            return childNode.getChildAtPath(subPath);
        }else{
            return childNode;
        }

    }

    @Override
    public void setDataAtPath(String path,Operation data) throws InvalidTreeNodePathException{
        String[] splitPath = StringUtils.splitPath(path);

        if(splitPath == null || splitPath.length == 0){
            String message = String.format("Invalid path %s.%s for this node",this.name,path);
            throw new InvalidTreeNodePathException(message);
        }

        String childName = splitPath[0]; //the first is always the name

        Optional<TreeNode<Operation>> optionalChildNode = getChild(childName);

        OperationNode childNode = null;

        boolean newChild = false;
        if(optionalChildNode.isPresent()) {
            childNode = (OperationNode)optionalChildNode.get();
        }else{
            childNode = new OperationNode(childName);
            newChild = true;
        }

        if(splitPath.length > 1){
            //means there is a subpath
            //so recurse on the childNode
            String subPath = splitPath[1]; //the second is always the rest of the path
            childNode.setDataAtPath(subPath,data);
        }else{
            //means we are at the end of the path
            //so set the data to the child directly
            childNode.setData(data);
        }

        if(newChild){

            //update the parent to the child node
            childNode.setParentNode(this);

            //add the child to the list of children
            this.children.add(childNode);
        }


    }

    @Override
    public Optional<TreeNode<Operation>> getChild(String name) {

        Optional<TreeNode<Operation>> optionalChild = Optional.empty();

        for (OperationNode child : children) {
            if(child.getName().equalsIgnoreCase(name)
                || child.hasTemplateName()){

                //then it is a direct match
                //caution if there are multiple child with template names for a given node
                //the first matching will be picked up and the others will be hidden
                optionalChild = Optional.of(child);
                break;
            }
        }

        return optionalChild;
    }

    @Override
    public void setParentNode(TreeNode<Operation> parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public void freeze() {

        this.fullyQualifiedName = formFullyQualifiedName();

        for (OperationNode child : children) {
            child.freeze();
        }

        //finally freeze this node once the child is frozen
        this.frozen = true;
    }

    @Override
    public boolean isRootNode() {
        return parentNode == null;
    }

    private String formFullyQualifiedName() {
        String fullName = null;
        if(parentNode != null){
            if(parentNode.isRootNode()){
                fullName = this.name;
            }else {
                fullName = String.format("%s.%s", parentNode.getFullyQualifiedName(), this.name);
            }
        }else{
            fullName = name;
        }
        return fullName;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public Operation getDataAtPath(String path) throws InvalidTreeNodePathException{

        String[] splitPath = StringUtils.splitPath(path);

        if(splitPath == null || splitPath.length == 0){
            return this.data;
        }

        String childName = splitPath[0]; //the first is always the name

        Optional<TreeNode<Operation>> optionalChildNode = getChild(childName);

        TreeNode<Operation> childNode = null;

        if(optionalChildNode.isPresent()){
            childNode = optionalChildNode.get();

            if(splitPath.length > 1){
                //this means we have more of the sub path
                String dataPath = splitPath[1]; //the second is always the subpath

                //now delegate the data fetch to the child node
                return childNode.getDataAtPath(dataPath);
            }else{

                //this means we are at the last node in the path
                return childNode.getData();
            }

        }else{
            //this means there is no valid child and the path is invalid as per this treenode
            String message = String.format("Invalid path %s.%s",this.name,childName);
            throw new InvalidTreeNodePathException(message);

        }
    }


    @Override
    public String toString() {
        return "OperationNode{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", children=" + children +
                '}';
    }

}
