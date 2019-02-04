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

package org.molecule.system;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Param {

    public Param zero();

    public Param plus(String s, Object o);

    public Param minus(String s);

    public int size();

    public boolean isEmpty();

    public boolean containsKey(String key);

    public boolean containsValue(Object value);

    public Object get(String key);

    public Set<String> keySet();

    public Collection<Object> values();

    public Set<Map.Entry<String, Object>> entrySet();

    public Map<String,Object> asMap();

    public Map<String,Object> outParams();

    public boolean hasOutParams();
}
