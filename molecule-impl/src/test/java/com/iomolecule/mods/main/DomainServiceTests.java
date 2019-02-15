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

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import com.iomolecule.system.Operation;
import com.iomolecule.system.SimpleOperation;
import com.iomolecule.system.services.DomainService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static com.iomolecule.util.CollectionUtils.LIST;

@Slf4j
public class DomainServiceTests {

    @Test
    public void testSimpleNestedDomain() throws URISyntaxException {
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        domainService.print(System.out);

        Operation operation = domainService.getOperation("commercial.accounts.view");

        log.debug("Operation Commercial accounts {}",operation);

        domainService.stop();

    }

    @Test
    public void testAvailabilityOfOperation() throws URISyntaxException{
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        //domainService.print(System.out);

        //Operation operation = domainService.getOperation("commercial.accounts.view");

        //log.debug("Operation Commercial accounts {}",operation);

        boolean validOperation = domainService.isValidOperation("common.usermanagement.addUser");
        assertTrue(validOperation);
        domainService.stop();
    }

    @Test
    public void testNonAvailabilityOfOperation() throws URISyntaxException{
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        boolean validOperation = domainService.isValidOperation("common.usermanagement.addUsers");

        assertTrue(!validOperation);
        domainService.stop();
    }

    @Test
    public void testDomainOperations() throws URISyntaxException {
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        //boolean validOperation = domainService.isValidOperation("common.usermanagement.addUsers");

        List<String> domainNamesAt = domainService.getDomainNamesAt("common.admin");

        System.out.println(domainNamesAt);

        List<String> operationsAt = domainService.getOperationsAt("common.admin");

        System.out.println(operationsAt);

        //assertTrue(!validOperation);
        domainService.stop();
    }

    private List<Operation> getOperations() throws URISyntaxException {
        return LIST(Operation.class,new SimpleOperation("commercial.accounts.view",
                new URI("function://viewAccountsFun"),""),
                new SimpleOperation("commercial.account.remove",
                        new URI("function://removeAccountsFun"),""),
                new SimpleOperation("common.usermanagement.addUser",new URI("function://addUserFun"),""),
                new SimpleOperation("common.admin.addUser",new URI("function://addUserFun"),""),
                new SimpleOperation("common.admin.addUsers",new URI("function://addUsersFun"),""),
                new SimpleOperation("common.admin.newAdmin.removeUser",new URI("function://removeUserFun"),""),
                new SimpleOperation("commercial.services.crm.viewCustomers",new URI("function://viewCRMUsers"),"")
        );
    }
}
