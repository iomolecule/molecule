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

/**
 * This package holds the key annotations used to look up the respective services in Molecule Framework
 * <p>
 * {@link com.iomolecule.system.annotations.AsyncEventBus}  - This annotation is used to look up the EventBus which can post messages asynchronously
 * </p>
 * <p>
 * {@link com.iomolecule.system.annotations.Doc}  - This annotation is used to document an {@link com.iomolecule.system.Fn}
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.DomainOperations}  - This annotation is used to publish an {@link com.iomolecule.system.Operation} instance to the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.EventSink}  - This annotation is used to publish or register an EventSink.
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Fun}  - This annotation is used to register an {@link com.iomolecule.system.Fn} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Func}  - This annotation is used to register an {@link java.util.function.Function} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Funs}  - This annotation is used to register a bunch of {@link com.iomolecule.system.Fn} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Id}  - This annotation is used to attach a valid {@link java.net.URI} to a {@link java.util.function.Function} instance
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.In}  - This annotation is used to specify the In parameters expected by a {@link java.util.function.Function} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.LifecycleManagers}  - This annotation is used to register a {@link com.iomolecule.system.LifecycleManager} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.MainArgs}  - This annotation is used to inject the Arguments received by the java program on startup
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Out}  - This annotation is used to specify the Out parameters provided by a {@link java.util.function.Function} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.ParamDecl}  - This annotation is used to declare the param information (whether In or Out) for a {@link java.util.function.Function} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.Shells}  - This annotation is used by Shell implementers to register an instance of {@link com.iomolecule.system.Shell} in the system
 * </p>
 *<p>
 * {@link com.iomolecule.system.annotations.SyncEventBus}  - This annotation is used to look up the EventBus which can post messages synchronously
 * </p>
 *
 */
package com.iomolecule.system.annotations;