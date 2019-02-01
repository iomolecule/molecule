package org.molecule.commons;

public final class Constants {

    private Constants(){}

    public static final String FUNCTION_SCHEME = "function";
    public static final String FUNCTION_TO_INVOKE = "function-to-invoke";
    public static final String FNBUS_NAME = "core/system/fn-bus";
    public static final String STATUS = "status";
    public static final String FAILED = "failed";
    public static final String REASON = "reason";
    public static final String EXCEPTION = "exception";
    public static final String OUT_PARAMS = "function-out-params";
    public static final String IN_PARAMS = "function-in-params";

    /** Error Codes **/
    public static final String ERROR_NO_FUNCTION_TO_INVOKE_SPECIFIED = "error-no-function-to-invoke-specified";
    public static final String ERROR_NO_VALID_FUNCTION_REGISTERED_FOR_URI = "error-no-valid-function-registered-for-uri";

}
