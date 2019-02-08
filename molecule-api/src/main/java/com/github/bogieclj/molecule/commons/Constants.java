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

package com.github.bogieclj.molecule.commons;

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
