package org.molecule.system;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecule.util.FnUtils.toURI;

public class URIFn implements Fn{

    private URI fnURI;
    private Function<Param,Param> function;
    private List<ParamDeclaration> inDeclarations = Collections.EMPTY_LIST;
    private List<ParamDeclaration> outDeclarations = Collections.EMPTY_LIST;

    public URIFn(String uri, Function<Param,Param> fn,List<ParamDeclaration> in,List<ParamDeclaration> out){
        checkArgument(uri != null,"Function URI cannot be null or empty");
        checkArgument(!uri.isEmpty(),"Function URI cannot be null or empty");
        checkArgument(fn != null,"Function instance annot be null");
        fnURI = toURI(uri);
        this.function = fn;
        if(in != null)
            this.inDeclarations = Collections.unmodifiableList(in);

        if(out != null)
            this.outDeclarations = Collections.unmodifiableList(out);
    }

    public URIFn(String uri, Function<Param,Param> fn){
        checkArgument(uri != null,"Function URI cannot be null or empty");
        checkArgument(!uri.isEmpty(),"Function URI cannot be null or empty");
        checkArgument(fn != null,"Function instance annot be null");
        fnURI = toURI(uri);
        this.function = fn;
    }

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



    @Override
    public List<ParamDeclaration> getOutDeclarations() {
        return outDeclarations;
    }

    @Override
    public List<ParamDeclaration> getInDeclarations() {
        return inDeclarations;
    }

    @Override
    public URI getURI() {
        return fnURI;
    }

    @Override
    public Param apply(Param param) {
        return function.apply(param);
    }
}
