package org.molecule.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import io.datatree.Tree;
import org.junit.Test;
import org.molecule.system.Operation;
import org.molecule.system.SimpleOperation;

import java.io.IOException;
import java.io.InputStream;
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
