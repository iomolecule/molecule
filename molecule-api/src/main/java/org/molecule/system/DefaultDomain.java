package org.molecule.system;

import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultDomain implements MutableDomain{

    private String name;
    private Map<String,Domain> immediateChildrenMap = new HashMap<>();
    protected Map<String,Operation> operationMap = new HashMap<>();
    private Domain parent;

    public DefaultDomain(String name, Operation... operations) {
        this(name,operations,null);
    }

    public DefaultDomain(String name, Operation[] operations,Domain... children) {
        checkArgument(name != null && !name.isEmpty(),"Domain name cannot be null or empty!");
        this.name = name;
        if(operations != null && operations.length > 0){
            for (Operation operation : operations) {
                operationMap.put(operation.getName(),operation);
            }
        }

        if(children != null && children.length > 0){
            for (Domain child : children) {
                immediateChildrenMap.put(child.getName(),child);
                //child.setParent(this);
            }

        }

    }

    @Override
    public String getName() {
        return name;
    }

    public void addOperation(Operation op){
        if(!operationMap.containsKey(op.getName())) {
            operationMap.put(op.getName(), op);
        }else{
            throw new RuntimeException(String.format("Operation %s already exists in the domain %s",op.getName(),this.name));
        }
    }

    @Override
    public Domain getChild(String path) {
        checkArgument(path != null && !path.isEmpty(),"domain path cannot be null or empty!");

        String[] paths = path.split("/");
        paths = sanitize(paths);
        if(paths.length == 0){
            return this;
        }
        Domain matchingDomain = null;

        String immediateChildLookingFor = paths[0];
        if(immediateChildrenMap.containsKey(immediateChildLookingFor)){
            Domain immediateChild = immediateChildrenMap.get(immediateChildLookingFor);
            if(paths.length > 1) {
                //go further down the tree
                matchingDomain = immediateChild.getChild(getSubPath(paths));
            }else{
                matchingDomain = immediateChild;
            }
        }
        return matchingDomain;
    }

    private String getSubPath(String[] paths) {
        String path = null;
        if(paths.length > 1){
            StringBuffer subPath = new StringBuffer();
            for(int i=1;i<paths.length;i++){
                subPath.append(paths[i]);
                subPath.append("/");
            }
            path = subPath.toString();
        }
        return path;
    }

    private String[] sanitize(String[] paths) {
        List<String> originalList = Arrays.asList(paths);
        List<String> finalList = new ArrayList<>();

        originalList.forEach(s->{
            if(!s.isEmpty()){
                finalList.add(s);
            }
        });

        return finalList.toArray(new String[0]);
    }

    @Override
    public Operation getOperation(String name) {
        return operationMap.get(name);
    }

    @Override
    public Map<String, Operation> getOperations() {
        return ImmutableMap.copyOf(operationMap);
    }

    @Override
    public boolean hasChild(String path) {
        Domain child = getChild(path);
        return child != null ? true : false;
    }

    @Override
    public boolean hasOperation(String name) {
        return operationMap.containsKey(name);
    }

    @Override
    public Domain getParent() {
        return parent;
    }

    @Override
    public void setParent(Domain domain) {
        if(this == domain){
            throw new RuntimeException("A domain cannot be a parent of itself!");
        }
        this.parent = domain;
    }

    @Override
    public boolean isRoot() {
        return parent == null ? true : false;
    }

    @Override
    public boolean isLeaf() {
        return immediateChildrenMap.isEmpty();
    }

    @Override
    public void addOperation(OperationDef operationDef) {
        if(!hasOperation(operationDef)){
            URI uri = operationDef.getURI();
            String path = uri.getPath();
            String[] paths = path.split("/");
            paths = sanitize(paths);

            if(paths.length > 0){
                String operationName = paths[paths.length - 1];

                if(paths.length > 1){
                    String domainPath = getDomainPath(paths,0,paths.length - 2);
                    if(!hasChild(domainPath)){
                        //addDomain(domainPath);
                    }

                    Domain child = getChild(domainPath);
                    //child.addOperation(new DefaultOperation(operationName,operationDef.getFunctionURI()));

                }

            }


        }else{
            throw new RuntimeException(String.format("OperationDef %s is being duplicated which is not allowed!",operationDef));
        }
    }

    @Override
    public boolean hasOperation(OperationDef operationDef) {
        boolean retVal = false;

        URI operationURI = operationDef.getURI();
        String uriPath = operationURI.getPath();
        String[] paths = uriPath.split("/");
        paths = sanitize(paths);

        if(paths.length > 0){
            String operationName = paths[paths.length - 1];
            if(paths.length > 1){
                String domainPath = getDomainPath(paths,0,paths.length - 2);
                Domain childDomain = getChild(domainPath);
                if(childDomain.hasOperation(operationName)){
                    retVal = true;
                }
            }else{
                if(this.hasOperation(operationName)){
                    retVal = true;
                }
            }
        }

        return retVal;
    }

    private String getDomainPath(String[] paths, int startIndex, int endIndex) {
        StringBuffer domainPath = new StringBuffer();
        for(int i = startIndex ; i < endIndex ; i++){
            domainPath.append(paths[i]);
            domainPath.append("/");
        }
        return domainPath.toString();
    }

    @Override
    public String toString() {
        return "DefaultDomain{" +
                "name='" + name + '\'' +
                ", immediateChildrenMap=" + immediateChildrenMap +
                ", operationMap=" + operationMap +
                ", parent=" + parent +
                '}';
    }
}
