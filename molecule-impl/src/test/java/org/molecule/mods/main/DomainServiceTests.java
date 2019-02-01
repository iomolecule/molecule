package org.molecule.mods.main;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.molecule.system.Operation;
import org.molecule.system.SimpleOperation;
import org.molecule.system.services.DomainService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.molecule.util.CollectionUtils.LIST;

@Slf4j
public class DomainServiceTests {

    @Test
    public void testSimpleNestedDomain() throws URISyntaxException {
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        domainService.print(System.out);

        Operation operation = domainService.getOperation("commercial.accounts.view");

        log.info("Operation Commercial accounts {}",operation);

        domainService.stop();

    }

    @Test
    public void testAvailabilityOfOperation() throws URISyntaxException{
        List<Operation> operations = getOperations();

        DomainService domainService = new DefaultDomainService(operations);
        domainService.start();

        //domainService.print(System.out);

        //Operation operation = domainService.getOperation("commercial.accounts.view");

        //log.info("Operation Commercial accounts {}",operation);

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
