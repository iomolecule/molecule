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

package com.iomolecule.system.services;

import com.iomolecule.system.Operation;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * The DomainService provides information about valid 'Domains' and its related 'Operation' in the Molecule framework.
 * For more information about 'Domains' and 'Operation's please @see https://github.com/bogie-clj/molecule
 */
public interface DomainService {

    /**
     * Start the DomainService instance.
     * The Start operations may do the initialization of the DomainService
     * before being used in the system.
     */
    public void start();

    /**
     * Checks whether the given '.' based path is pointing to a valid 'Operation' in the system
     * @param path A '.' separated string like the fully qualified name of the Class in java pointing to the operation
     * @return 'True' if the given path is valid. 'False' if the given path is not valid
     */
    public boolean isValidOperation(String path);


    /**
     * Retrieves the operation pointed to by the path. The path name should be a '.' separated path as in the fully qualified
     * name of a Class in java language.
     * @param path A '.' separated string
     * @return The Operation instance if the path is valid, otherwise returns a 'No Op' operation instance.
     */
    public Operation getOperation(String path);

    /**
     * Retrieves the list of valid domain names in the given path.
     * @param path The path to look up
     * @return The list of valid domain names
     */
    public List<String> getDomainNamesAt(String path);

    /**
     * Retrieves the list of valid operation names in the given path.
     * @param path The path to look up
     * @return The list of valid operation names
     */
    public List<String> getOperationsAt(String path);

    /**
     * Stops the DomainService and clears up internal datastructures.
     * Note: This instance is not usable after this method invocation.
     */
    public void stop();

    /**
     * Prints the content of the Domains registered in the DomainService.
     * @param stream The Stream to print to
     */
    public void print(PrintStream stream);

    /**
     * Retrieves all the valid operations across all the domains within the DomainService
     * @return The list of all operations
     */
    public List<Operation> getAllOperations();

    /**
     * Checks if a valid operation is at the given path and operationName
     * @param path The path to check the operation at
     * @param operationName The operation name to check for
     * @return 'True' if the operation is valid at the path, 'False' otherwise
     */
    public boolean isValidOperationAt(String path,String operationName);

    /**
     * Retrieves the Operation instance at the given domain and operation
     * @param fullyQualifiedDomainName The fully qualified domain name
     * @param operationName The operation name to lookup
     * @return The Operation instance if present. Else 'No Op' if not present.
     */
    public Operation getOperationAt(String fullyQualifiedDomainName, String operationName);

    /**
     * Get a list of all operations at the given domain
     * @param fullyQualifiedDomainName The fully qualified domain name
     * @return The list of operation instances
     */
    public List<Operation> getAllOperationsAt(String fullyQualifiedDomainName);

    /**
     * Checks whether a domain name is valid at the given path
     * @param path The path to check for the presence of the Domain
     * @param domain The Name of the domain to look up
     * @return 'True' if the domain is valid in the path, 'False' otherwise
     */
    public boolean isValidDomainAt(String path,String domain);


    /**
     * Retrieves all the domain information as a tree in a map data structure
     * @return Map of domain tree
     */
    public Map getDomainTree();
}
