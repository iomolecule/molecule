package org.molecule.util;

import org.molecule.system.annotations.Doc;
import org.molecule.system.annotations.Id;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

public class FnUtils {

    public static URI toURI(String val){
        try {
            return new URI(val);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI getURI(Class clz) throws URISyntaxException {
        checkArgument(clz != null,"Invalid Class provided!");


        Id idAnnotation = (Id)clz.getAnnotation(Id.class);

        if(idAnnotation != null){
            return new URI(idAnnotation.value());
        }else{
            throw new RuntimeException(String.format("Class %s does not have a valid annotation of type %s",clz,Id.class));
        }
    }

    public static String getDoc(Class clz){
        checkArgument(clz != null,"Invalid Class provided!");

        Doc docAnnotation = (Doc)clz.getAnnotation(Doc.class);

        if(docAnnotation != null){
            return docAnnotation.value();
        }else{
            throw new RuntimeException(String.format("Class %s does not have a valid annotation of type %s",clz,Doc.class));
        }
    }
}
