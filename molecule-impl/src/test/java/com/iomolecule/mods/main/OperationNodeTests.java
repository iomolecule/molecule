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
import com.iomolecule.util.ds.InvalidTreeNodePathException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class OperationNodeTests {


    @Test
    public void testOpNodeSimple1() throws InvalidTreeNodePathException {
        OperationNode rootNode = new OperationNode("--root--");
        rootNode.setDataAtPath("cargo.operations.shipments.get",new SimpleOperation("cargo.operations.shipments.get"));
        rootNode.setDataAtPath("cargo.operations.shipments.post",new SimpleOperation("cargo.operations.shipments.post"));
        rootNode.setDataAtPath("cargo.operations.shipments.update",new SimpleOperation("cargo.operations.shipments.update"));
        rootNode.setDataAtPath("accounting.revenue.shipments.get",new SimpleOperation("accounting.revenue.shipments.totalrev"));
        rootNode.setDataAtPath("accounting.revenue.operations.get",new SimpleOperation("accounting.revenue.shipments.totalrev"));
        rootNode.setDataAtPath("customs.revenue.acct.post",new SimpleOperation("accounting.revenue.shipments.totalrev"));
        rootNode.setDataAtPath("pricing.revenue.rad.something",new SimpleOperation("accounting.revenue.shipments.totalrev"));
        rootNode.setDataAtPath("accounting.[revenue].rad.[test].get",new SimpleOperation("accounting.revenue.shipments.totalrev"));

        System.out.println(rootNode);

    }

    @Test
    public void testOpNodesGetDataAtPath1() throws InvalidTreeNodePathException {
        Map<String, Operation> operationMap = getOperationMap();

        OperationNode rootNode = new OperationNode("--root--");

        Operation op = operationMap.get("op1");
        rootNode.setDataAtPath(op.getName(),op);

        op = operationMap.get("op2");
        rootNode.setDataAtPath(op.getName(),op);


        op = operationMap.get("op3");
        rootNode.setDataAtPath(op.getName(),op);

        op = operationMap.get("op4");
        rootNode.setDataAtPath(op.getName(),op);

        op = operationMap.get("op5");
        rootNode.setDataAtPath(op.getName(),op);


        op = operationMap.get("getShipment");
        rootNode.setDataAtPath(op.getName(),op);

        op = operationMap.get("getHouseShipment");
        rootNode.setDataAtPath(op.getName(),op);

        String path = "domain1.subdomain3.post";
        Operation dataAtPath = rootNode.getDataAtPath(path);
        System.out.println(String.format("Data for %s - %s",path,dataAtPath));

        path = "domain2.subdomain1.post";
        dataAtPath = rootNode.getDataAtPath(path);
        System.out.println(String.format("Data for %s - %s",path,dataAtPath));

        path = "cargo.operations.shipments.AWB1234567.get";
        dataAtPath = rootNode.getDataAtPath(path);
        System.out.println(String.format("Data for %s - %s",path,dataAtPath));

        path = "cargo.operations.shipments.AWB1234568.hawbs.HAWB4567890.put";
        dataAtPath = rootNode.getDataAtPath(path);
        System.out.println(String.format("Data for %s - %s",path,dataAtPath));

        //System.out.println(rootNode);



    }

    private Map<String, Operation> getOperationMap() {

        Map<String,Operation> operationMap = new HashMap<>();

        Operation op = new SimpleOperation("domain1.subdomain1.get","function://system/example/fn1","NA");
        operationMap.put("op1",op);

        op = new SimpleOperation("domain1.subdomain2.post","function://system/example/fn2","NA");
        operationMap.put("op2",op);

        op = new SimpleOperation("domain2.subdomain1.post","function://system/example/fn3","NA");
        operationMap.put("op3",op);

        op = new SimpleOperation("domain2.subdomain2.get","function://system/example/fn4","NA");
        operationMap.put("op4",op);

        op = new SimpleOperation("domain1.subdomain3.post","function://system/example/fn5","NA");
        operationMap.put("op5",op);

        op = new SimpleOperation("cargo.operations.shipments.[shipment].get","function://cargo/operations/functions/getShipment","NA");
        operationMap.put("getShipment",op);

        op = new SimpleOperation("cargo.operations.shipments.[shipment].put","function://cargo/operations/functions/putShipment","NA");
        operationMap.put("putShipment",op);

        op = new SimpleOperation("cargo.operations.shipments.[shipment].hawbs.[hawb].put","function://cargo/operations/functions/getHouseShipment","NA");
        operationMap.put("getHouseShipment",op);

        return operationMap;
    }


}
