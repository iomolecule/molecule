package org.molecule.util;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import org.molecule.system.DefaultParamDeclaration;
import org.molecule.system.Param;
import org.molecule.system.ParamDeclaration;
import org.molecule.system.annotations.*;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class FnUtils {

    public static final String AVAILABLE_PARAMS = "available-params";
    public static final String NON_AVAILABLE_PARAMS = "non-available-params";

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

    public static List<ParamDeclaration> getInParams(Class clz){
        checkArgument(clz != null,"Invalid Class provided!");

        In inParamAnnotation = (In)clz.getAnnotation(In.class);

        List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
        if(inParamAnnotation != null){
            ParamDecl[] params = inParamAnnotation.params();

            if(params != null){
                for (ParamDecl param : params) {
                    ParamDeclaration paramDeclaration = new DefaultParamDeclaration(param.key(),param.type(),param.mandatory());
                    paramDeclarationList.add(paramDeclaration);
                }
            }
            return paramDeclarationList;
        }else{
            throw new RuntimeException(String.format("Class %s does not have a valid annotation of type %s",clz,In.class));
        }
    }

    public static List<ParamDeclaration> getOutParams(Class clz){
        checkArgument(clz != null,"Invalid Class provided!");

        Out outParamAnnotation = (Out)clz.getAnnotation(Out.class);

        List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
        if(outParamAnnotation != null){
            ParamDecl[] params = outParamAnnotation.params();

            if(params != null){
                for (ParamDecl param : params) {
                    ParamDeclaration paramDeclaration = new DefaultParamDeclaration(param.key(),param.type(),param.mandatory());
                    paramDeclarationList.add(paramDeclaration);
                }
            }
            return paramDeclarationList;
        }else{
            throw new RuntimeException(String.format("Class %s does not have a valid annotation of type %s",clz,Out.class));
        }
    }

    public static Param verifyInParams(Param in, List<ParamDeclaration> inParams){

        //segregate the available and non available params based on the declaration
        Map<String,List<ParamDeclaration>> availableAndNotAvailableParams = getAvailableAndNotAvailableParams(in,inParams);

        //is any of the non available params mandatory?
        List<ParamDeclaration> mandatoryNonAvailableParams = getMandatoryNonAvailableParams(availableAndNotAvailableParams);

        //if any then throw an exception with the missing params
        if(!mandatoryNonAvailableParams.isEmpty()){
            String message = String.format("Missing mandatory inParams specified in the in param declaration %s",mandatoryNonAvailableParams);
            throw new RuntimeException(message);
        }

        return in;
    }

    public static Param mapOutParams(Param in, List<ParamDeclaration> outParams,String mapToParamKey){

        //segregate the available and non available params based on the declaration

        Map<String,List<ParamDeclaration>> availableAndNotAvailableParams = getAvailableAndNotAvailableParams(in,outParams);

        //does any of the non available param is mandatory
        List<ParamDeclaration> mandatoryNonAvailableParams = getMandatoryNonAvailableParams(availableAndNotAvailableParams);


        //if any then throw an exception with the missing params
        if(!mandatoryNonAvailableParams.isEmpty()){
            String message = String.format("Missing mandatory outParams specified in the out param declaration %s",mandatoryNonAvailableParams);
            throw new RuntimeException(message);
        }

        //if not then proceed to mapping
        List<ParamDeclaration> availableParams = getAllAvailableParams(availableAndNotAvailableParams);

        Param out = mapParams(in,availableParams,mapToParamKey);

        return out;


    }

    private static Param mapParams(Param in, List<ParamDeclaration> availableParams, String mapToParamKey) {
        PersistentMap<String,Object> outMap = PersistentHashMap.emptyMap();

        for (ParamDeclaration availableParam : availableParams) {
            Object value = in.get(availableParam.getKey());
            outMap = outMap.plus(availableParam.getKey(),value);
        }

        Param out = in.plus(mapToParamKey,outMap);
        return out;
    }


    private static List<ParamDeclaration> getAllAvailableParams(Map<String, List<ParamDeclaration>> availableAndNotAvailableParams) {
        return availableAndNotAvailableParams.get(AVAILABLE_PARAMS);
    }


    private static List<ParamDeclaration> getMandatoryNonAvailableParams(Map<String, List<ParamDeclaration>> availableAndNotAvailableParams) {

        List<ParamDeclaration> nonAvailableParams = getAllNonAvailableParams(availableAndNotAvailableParams);

        List<ParamDeclaration> mandatoryNonAvailableParams = new ArrayList<>();

        nonAvailableParams.forEach(paramDeclaration -> {
            if(paramDeclaration.isMandatory()){
                mandatoryNonAvailableParams.add(paramDeclaration);
            }
        });

        return mandatoryNonAvailableParams;
    }

    private static List<ParamDeclaration> getAllNonAvailableParams(Map<String, List<ParamDeclaration>> availableAndNotAvailableParams) {
        return availableAndNotAvailableParams.get(NON_AVAILABLE_PARAMS);
    }

    private static Map<String, List<ParamDeclaration>> getAvailableAndNotAvailableParams(Param in, List<ParamDeclaration> outParams) {

        List<ParamDeclaration> availableParams = new ArrayList<>();
        List<ParamDeclaration> notAvailableParams = new ArrayList<>();
        Map<String,List<ParamDeclaration>> availableAndNotAvailableParams = new HashMap<>();


        outParams.forEach(paramDeclaration -> {
            if(in.containsKey(paramDeclaration.getKey())){
                availableParams.add(paramDeclaration);
            }else{
                notAvailableParams.add(paramDeclaration);
            }
        });


        availableAndNotAvailableParams.put(AVAILABLE_PARAMS,availableParams);
        availableAndNotAvailableParams.put(NON_AVAILABLE_PARAMS,notAvailableParams);
        return availableAndNotAvailableParams;
    }


}
