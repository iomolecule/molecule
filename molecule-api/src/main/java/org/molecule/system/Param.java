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
