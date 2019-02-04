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

package org.molecule.system.services;

import org.molecule.system.Operation;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;

public interface DomainService {

    public void start();

    public boolean isValidOperation(String path);

    //public void forEach(Consumer<Operation> operationConsumer);

    public Operation getOperation(String path);

    public List<String> getDomainNamesAt(String path);

    public List<String> getOperationsAt(String path);

    public void stop();

    public void print(PrintStream stream);

    public List<Operation> getAllOperations();

    public boolean isValidOperationAt(String path,String operationName);

    public Operation getOperationAt(String fullyQualifiedDomainName, String operationName);

    public List<Operation> getAllOperationsAt(String fullyQualifiedDomainName);

    public boolean isValidDomainAt(String path,String domain);
}
