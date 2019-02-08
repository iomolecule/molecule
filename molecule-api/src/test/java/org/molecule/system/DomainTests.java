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

package org.molecule.system;

import io.datatree.Tree;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class DomainTests {

    @Test
    public void testSimple() throws URISyntaxException {



        /*Domain simpleDomain = new DefaultDomain(
                "somedomain",
                new DefaultOperation("view",new URI("function://view-fn")),
                new DefaultOperation("update",new URI("function://update-fn"))
        );


        Domain rootDomain = new DefaultDomain("/",getOperations(),simpleDomain);
        */

        Tree dataTree = new Tree();

        dataTree.put("domain1.subdomain1.operation1","view-op");
        dataTree.put("domain1.subdomain1.operation2","view-op2");

        dataTree.putMap("domain1.subdomain1");
        dataTree.putList("domain1.subdomain1.list1");
        dataTree.putMap("domain1.subdomain1.subdomain2.subdomain3");
        dataTree.putMap("domain1.subdomain3.subdomain4");
        dataTree.putList("domain1.list3");


        Tree domain1 = dataTree.get("domain1");

        //System.out.println(dataTree);



        //System.out.println(domain1);

        domain1.forEach(tree -> {
            System.out.println(tree.getName());
            System.out.println(tree);
        });
    }
}
