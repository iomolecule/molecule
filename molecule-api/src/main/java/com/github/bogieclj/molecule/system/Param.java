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


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The input and output of {@link Fn} are abstracted as Param represented by the interface {@link com.github.bogieclj.molecule.system.Param}
 * Param can be logically visualized as a persistent Map similar to the concept in the language Clojure.
 * The values and keys in a Param can only be plussed or minussed which will return a new Param instance , whereas the original Param instance is unmodified.
 * The Param can be conceptually thought of as a map of objects each of which can be retrieved with a specified key.
 *
 */
public interface Param {

    /**
     * Empties the Param contents and returns a new Param with no elements.
     * The original Param is unaffected.
     * @return New Param instance with its contents emptied
     */
    public Param zero();

    /**
     * Creates and returns a new Param with the specified key and value added.
     * The original Param is unaffected.
     *
     * @param s The key of the new item plussed in the Param instance
     * @param o The value of the new item plussed in the Param instance
     * @return Param New Param instance with the specified key and value added
     */
    public Param plus(String s, Object o);

    /**
     * Returns a new Param instance with the specified item key and value removed
     * The original Param is unaffected
     *
     * @param s The key of the item to be minussed in the Param instance
     * @return New Param instance with its contents emptied
     */
    public Param minus(String s);


    /**
     * Retrieves the size of the Param instance.
     * @return int The count of items in the Param instance.
     */
    public int size();


    /**
     * Checks whether the Param instance is empty
     * @return boolean Whether the param instance is empty with no items.
     */
    public boolean isEmpty();


    /**
     * Checks whether the param instance contains the specified Key.
     * @param key The key to check for in the param
     * @return boolean Whether the specified key is present or not present in the param
     */
    public boolean containsKey(String key);


    /**
     * Checks whether the specified item value is present in the param instance.
     * @param value The value of item to be checked for presence in the param instance.
     * @return boolean Whether the specified itme is present or absent in the Param instance.
     */
    public boolean containsValue(Object value);

    /**
     * Retrieves the value of an item given its key
     * @param key The key of the item to be retrieved from the Param
     * @return Object The value of the item matching the key
     */
    public Object get(String key);

    /**
     * Retrieves the set of keys in the Param instance.
     * @return The set of keys in the Param instance.
     */
    public Set<String> keySet();

    /**
     * Retrieves the collection of values in the param instances.
     * @return The collection of values in the param instance.
     */
    public Collection<Object> values();

    /**
     * Retrieves the set of entries in the param instance.
     * @return The set of entries in the param instance
     */
    public Set<Map.Entry<String, Object>> entrySet();

    /**
     * Returns the data in the Param as an immutable map.
     * @return The immutable map of data from the Param instance.
     */
    public Map<String,Object> asMap();


    /**
     * Retrieves the Map of values in with the key marked as out params.
     * This is a convenient method used in Molecule very frequently to retrieve the output values after the invocation of a {@link Fn}
     * @return The output params as a Map instance
     */
    public Map<String,Object> outParams();

    /**
     * A convenience method used to check whether the Output values are present in the Param instance.
     * This is a convenient method used in Molecule very frequently to check the presence of output values after the invocation of a {@link Fn}
     * @return A flag indicating whether output params are present in the Param instance
     */
    public boolean hasOutParams();
}
