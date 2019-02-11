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

package com.github.bogieclj.molecule.system;

import com.github.bogieclj.molecule.util.FnUtils;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.github.bogieclj.molecule.util.FnUtils.toURI;

/**
 * The URIFn is a wrapper {@link com.github.bogieclj.molecule.system.Fn} implementation which provides the ability to
 * wrap a {@link java.util.function.Function} to provide the ability to specify URI for the Fn as well as support in and out param declarations.
 * This is a convenience class which reduces the need to create multiple Fn classes and provides an easy way to wrap java.util.function.Function classes.
 *
 */
public class URIFn implements Fn{

    private URI fnURI;
    private Function<Param,Param> function;
    private List<ParamDeclaration> inDeclarations = Collections.EMPTY_LIST;
    private List<ParamDeclaration> outDeclarations = Collections.EMPTY_LIST;

    /**
     * Constructor allowing URIFn to be created using uri, Function , in and out declarations
     * @param uri The URI as a string
     * @param fn The java.util.function.Function instance to be wrapped
     * @param in The list of in param declarations
     * @param out The list of out param declarations
     */
    public URIFn(String uri, Function<Param,Param> fn,List<ParamDeclaration> in,List<ParamDeclaration> out){
        checkArgument(uri != null,"Function URI cannot be null or empty");
        checkArgument(!uri.isEmpty(),"Function URI cannot be null or empty");
        checkArgument(fn != null,"Function instance annot be null");
        fnURI = FnUtils.toURI(uri);
        this.function = fn;
        if(in != null)
            this.inDeclarations = Collections.unmodifiableList(in);

        if(out != null)
            this.outDeclarations = Collections.unmodifiableList(out);
    }

    /**
     * Constructor allowing URIFn to be created using only the uri and Function instances
     * @param uri The URI as a string
     * @param fn The java.util.function.Function instance to be wrapped
     */
    public URIFn(String uri, Function<Param,Param> fn){
        checkArgument(uri != null,"Function URI cannot be null or empty");
        checkArgument(!uri.isEmpty(),"Function URI cannot be null or empty");
        checkArgument(fn != null,"Function instance annot be null");
        fnURI = FnUtils.toURI(uri);
        this.function = fn;
    }

    /**
     * Constructor allowing URIFn to be created using uri, Function , in and out declarations
     * @param uri The URI as a URI instance
     * @param fn The java.util.function.Function instance to be wrapped
     * @param in The list of in param declarations
     * @param out The list of out param declarations
     */
    public URIFn(URI uri, Function<Param,Param> fn,List<ParamDeclaration> in,List<ParamDeclaration> out){
        checkArgument(uri != null,"Function URI cannot be null or empty");
        checkArgument(fn != null,"Function instance annot be null");
        fnURI = uri;
        this.function = fn;
        if(in != null)
            this.inDeclarations = Collections.unmodifiableList(in);

        if(out != null)
            this.outDeclarations = Collections.unmodifiableList(out);
    }


    /**
     * Provides the list of Out Declarations of the URIFn
     * @return The list of Out declarations
     */
    @Override
    public List<ParamDeclaration> getOutDeclarations() {
        return outDeclarations;
    }

    /**
     * Provides the list of In Declarations of the URIFn
     * @return The list of In declarations
     */
    @Override
    public List<ParamDeclaration> getInDeclarations() {
        return inDeclarations;
    }

    /**
     * Retrieves the URI for the Fn
     * @return URI of Fn
     */
    @Override
    public URI getURI() {
        return fnURI;
    }


    @Override
    public Param apply(Param param) {
        return function.apply(param);
    }
}
