package org.molecule.util;

import java.net.URI;
import java.net.URISyntaxException;

public class FnUtils {

    public static URI toURI(String val){
        try {
            return new URI(val);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
