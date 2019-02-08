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

package com.github.bogieclj.molecule.util;

import com.github.bogieclj.molecule.system.InOutParam;
import com.github.bogieclj.molecule.system.Param;
import org.junit.Test;
import com.github.bogieclj.molecule.system.DefaultParamDeclaration;
import com.github.bogieclj.molecule.system.InOutParam;
import com.github.bogieclj.molecule.system.Param;
import com.github.bogieclj.molecule.system.ParamDeclaration;

import java.util.List;

import static com.github.bogieclj.molecule.util.CollectionUtils.LIST;

public class FnUtilsTest {

    @Test
    public void testParamMapping(){
        Param in = getSampleParam();
        List<ParamDeclaration> outParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class,
                new DefaultParamDeclaration("key1",String.class,true),
                new DefaultParamDeclaration("key2",Integer.class,true));

        Param out = FnUtils.mapOutParams(in, outParamDeclaration, "OUT");

        System.out.println(out);
    }

    @Test(expected = RuntimeException.class)
    public void testMissingMandatoryParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> outParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class,
                new DefaultParamDeclaration("key1",String.class,true),
                new DefaultParamDeclaration("key3",Integer.class,true));

        Param out = FnUtils.mapOutParams(in, outParamDeclaration, "OUT");

        System.out.println(out);
    }

    @Test
    public void testMissingOptionalParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> outParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class,
                new DefaultParamDeclaration("key1",String.class,true),
                new DefaultParamDeclaration("key3",Integer.class,false));

        Param out = FnUtils.mapOutParams(in, outParamDeclaration, "OUT");

        System.out.println(out);
    }

    @Test
    public void testVoidParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> outParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class);

        Param out = FnUtils.mapOutParams(in, outParamDeclaration, "OUT");

        System.out.println(out);
    }

    @Test
    public void testMandatoryInParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> inParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class,
                new DefaultParamDeclaration("key1",String.class,true),
                new DefaultParamDeclaration("key3",Integer.class,false));

        Param out = FnUtils.verifyInParams(in,inParamDeclaration);

        System.out.println(out);

    }

    @Test(expected = RuntimeException.class)
    public void testMissingMandatoryInParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> outParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class,
                new DefaultParamDeclaration("key1",String.class,true),
                new DefaultParamDeclaration("key3",Integer.class,true));

        Param out = FnUtils.verifyInParams(in,outParamDeclaration);

        System.out.println(out);
    }

    @Test
    public void testVoidInParams(){
        Param in = getSampleParam();
        List<ParamDeclaration> inParamDeclaration = CollectionUtils.LIST(ParamDeclaration.class);

        Param out = FnUtils.verifyInParams(in,inParamDeclaration);

        System.out.println(out);
    }

    private Param getSampleParam() {
        Param newParam = new InOutParam();

        newParam = newParam.plus("key1","Some Random String");
        newParam = newParam.plus("key2",500);

        return newParam;
    }
}
